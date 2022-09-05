package com.jalasoft.routesapp.ui.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.api.models.gmaps.Place
import com.jalasoft.routesapp.databinding.FragmentHomeBinding
import com.jalasoft.routesapp.ui.home.adapters.PlaceAdapter
import com.jalasoft.routesapp.ui.home.viewModel.HomeViewModel
import com.jalasoft.routesapp.util.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback, PlaceAdapter.IPlaceListener {

    private lateinit var mBottomSheetDialog: LinearLayout
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var mMap: GoogleMap? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private val requestFINELOCATIONPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            checkPermissions(isGranted)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecycler()

        // Map initialization
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        binding.btnCurrentLocation.setOnClickListener {
            if (isLocationPermissionGranted()) {
                getLocation()
            }
        }

        binding.btnCheckNextLocation.visibility = View.GONE
        // SearchView popup config
        binding.bottomLayout1.placesSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchPlaces(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        viewModel.fetchedPlaces.observe(viewLifecycleOwner) {
            (binding.bottomLayout1.placesRecycler.adapter as PlaceAdapter).updateList(it.toMutableList())
        }

        mBottomSheetDialog = view.findViewById(R.id.bottom_layout_1)
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetDialog)

        // Bottom sheet dialog config
        binding.btnMenu.setOnClickListener {
            if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.btnCheckNextLocation.visibility = View.VISIBLE
                    binding.bottomLayout1.btnArrowPopupState.rotation = 0F
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.btnCheckNextLocation.visibility = View.GONE
                    binding.bottomLayout1.btnArrowPopupState.rotation = 180F
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    @SuppressLint("CutPasteId")
    private fun setRecycler() {
        binding.bottomLayout1.placesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.bottomLayout1.placesRecycler.adapter = PlaceAdapter(mutableListOf(), this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.style_silver
            )
        )
        mMap = googleMap
        setMapOnCurrentCity()
    }

    @SuppressLint("MissingPermission")
    private fun isLocationPermissionGranted(): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                mMap?.isMyLocationEnabled = true
                return true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                AlertDialog.Builder(requireContext()).setTitle(getString(R.string.access_location_permission))
                    .setMessage(getString(R.string.allow_get_precise_location_permission))
                    .setPositiveButton(getString(R.string.allow)) { _, _ ->
                        requestFINELOCATIONPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()
                return false
            }
            else -> {
                requestFINELOCATIONPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                return false
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkPermissions(isGranted: Boolean) {
        if (isGranted) {
            mMap?.isMyLocationEnabled = true
            getLocation()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.give_the_app_precise_location_permissions),
                Toast.LENGTH_LONG
            ).show()
            // Redirect the user to the app settings to give permissions manually
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity?.packageName ?: null, null)
            intent.data = uri
            requireContext().startActivity(intent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient.lastLocation.addOnCompleteListener() { task ->
                val location: Location? = task.result
                if (location != null) {
                    val newLatLng = LatLng(location.latitude, location.longitude)
                    moveToLocation(newLatLng, 13F)
                }
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.turn_on_location), Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun moveToLocation(location: LatLng, zoom: Float) {
        val cameraTargetLocation = CameraUpdateFactory.newLatLngZoom(location, zoom)
        mMap?.animateCamera(cameraTargetLocation)
    }

    private fun setMapOnCurrentCity() {
        val currentLat = PreferenceManager.getCurrentLocationLat(requireContext())
        val currentLng = PreferenceManager.getCurrentLocationLng(requireContext())
        if (currentLat != PreferenceManager.NOT_FOUND && currentLng != PreferenceManager.NOT_FOUND) {
            val location = LatLng(currentLat.toDouble(), currentLng.toDouble())
            moveToLocation(location, 11F)
        } else {
            findNavController().navigate(R.id.action_homeFragment_to_cityPickerFragment)
        }
    }

    override fun onPlaceTap(place: Place) {
        val statusTextView = binding.bottomLayout1.tvSelectLocationStatus
        if (statusTextView.text == "Origin") statusTextView.text = "Destination"
    }
}
