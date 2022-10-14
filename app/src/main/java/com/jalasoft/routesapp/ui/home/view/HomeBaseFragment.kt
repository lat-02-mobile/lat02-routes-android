package com.jalasoft.routesapp.ui.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.api.models.gmaps.Place
import com.jalasoft.routesapp.databinding.FragmentHomeBinding
import com.jalasoft.routesapp.ui.home.adapters.PlaceAdapter
import com.jalasoft.routesapp.ui.home.viewModel.HomeViewModel
import com.jalasoft.routesapp.util.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint

enum class SelectPointsStatus {
    ORIGIN, DESTINATION, BOTH
}

@AndroidEntryPoint
open class HomeBaseFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, PlaceAdapter.IPlaceListener {

    private lateinit var mBottomSheetDialog: LinearLayout
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var selectPointsStatus = SelectPointsStatus.ORIGIN

    val args: HomeFragmentArgs by navArgs()
    private var _binding: FragmentHomeBinding? = null
    var markerOrigin: Marker? = null
    var markerDestination: Marker? = null

    val viewModel: HomeViewModel by viewModels()
    val binding get() = _binding!!
    var mMap: GoogleMap? = null

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
        setButtons()
        setRecycler()
        setMap()
        setBottomPopup()
        setObserversForHomeBaseFragment()
    }

    // Methods for setups
    private fun setButtons() {
        // Get current location button
        binding.btnCurrentLocation.setOnClickListener {
            if (isLocationPermissionGranted()) {
                getLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.style_silver
            )
        )
        mMap = googleMap
        mMap?.setOnMarkerClickListener(this)

        if (args.preSelectDestCoords == null) {
            setMapOnCurrentCity()
        }
    }

    override fun onPlaceTap(place: Place) {
        val locationLatLng = LatLng(place.geometry.location.lat, place.geometry.location.lng)
        moveToLocation(locationLatLng, 17F)
        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (marker == markerOrigin) {
            viewModel.selectedOrigin.value = null
        } else if (marker == markerDestination) {
            viewModel.selectedDestination.value = null
        }
        marker.remove()
        return true
    }

    private fun setObserversForHomeBaseFragment() {
        val statusTextView = binding.bottomLayout1.tvSelectLocationStatus
        viewModel.fetchedPlaces.observe(viewLifecycleOwner) {
            (binding.bottomLayout1.placesRecycler.adapter as PlaceAdapter).updateList(it.toMutableList())
        }

        viewModel.selectedDestination.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.btnGoBack.visibility = View.VISIBLE
                if (viewModel.selectedOrigin.value != null) {
                    statusTextView.text = getString(R.string.done)
                    Toast.makeText(requireContext(), getString(R.string.go_next_step), Toast.LENGTH_LONG).show()
                    binding.btnCheckNextLocation.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_secondary))
                } else {
                    statusTextView.text = getString(R.string.select_an_origin)
                }
            } else {
                markerDestination?.remove()
                statusTextView.text = getString(R.string.select_a_destination)
                binding.btnCheckNextLocation.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_primary))
                if (viewModel.selectedOrigin.value == null) {
                    binding.btnGoBack.visibility = View.GONE
                }
            }
        }

        viewModel.selectedOrigin.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.btnGoBack.visibility = View.VISIBLE
                if (viewModel.selectedDestination.value != null) {
                    statusTextView.text = getString(R.string.done)
                    Toast.makeText(requireContext(), getString(R.string.go_next_step), Toast.LENGTH_LONG).show()
                    binding.btnCheckNextLocation.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_secondary))
                } else {
                    statusTextView.text = getString(R.string.select_a_destination)
                }
            } else {
                markerOrigin?.remove()
                statusTextView.text = getString(R.string.select_an_origin)
                binding.btnCheckNextLocation.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_primary))
                if (viewModel.selectedDestination.value == null) {
                    binding.btnGoBack.visibility = View.GONE
                }
            }
        }
    }

    private fun setMap() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun setRecycler() {
        binding.bottomLayout1.placesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.bottomLayout1.placesRecycler.adapter = PlaceAdapter(mutableListOf(), this)
    }

    private fun setBottomPopup() {
        binding.bottomLayout1.placesSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val currentLat = PreferenceManager.getCurrentLocationLat(requireContext())
                    val currentLng = PreferenceManager.getCurrentLocationLng(requireContext())
                    viewModel.searchPlaces(query, "$currentLat $currentLng")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        mBottomSheetDialog = requireView().findViewById(R.id.bottom_layout_1)
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetDialog)

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.bottomLayout1.btnArrowPopupState.rotation = 0F
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.bottomLayout1.btnArrowPopupState.rotation = 180F
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    // Methods for map, location and permissions
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
            val uri = Uri.fromParts("package", activity?.packageName, null)
            intent.data = uri
            requireContext().startActivity(intent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
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

    fun moveToLocation(location: LatLng, zoom: Float) {
        Log.d("home", "moving")
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

    fun getSelectedLocation(): LatLng? {
        val cameraLocation = mMap?.cameraPosition?.target
        val markerCamera = cameraLocation?.let { MarkerOptions().position(it) }
        if (markerCamera != null) {
            if (selectPointsStatus == SelectPointsStatus.ORIGIN) {
                markerCamera.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_origin))
                markerOrigin = mMap?.addMarker(markerCamera)
            } else if (selectPointsStatus == SelectPointsStatus.DESTINATION) {
                markerCamera.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination))
                markerDestination = mMap?.addMarker(markerCamera)
            }
        }
        return if (cameraLocation != null) {
            LatLng(cameraLocation.latitude, cameraLocation.longitude)
        } else {
            null
        }
    }
}
