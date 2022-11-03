package com.jalasoft.routesapp.ui.routes.view

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.databinding.DialogSettingsBinding
import com.jalasoft.routesapp.databinding.FragmentRouteSelectedBinding
import com.jalasoft.routesapp.ui.tourPoints.viewModel.TourPointsViewModel
import com.jalasoft.routesapp.util.Extensions.toLatLong
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.Constants
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper
import com.jalasoft.routesapp.util.helpers.ImageHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RouteSelectedFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentRouteSelectedBinding? = null
    private val binding get() = _binding!!
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mMap: GoogleMap? = null
    private var route: LineRouteInfo? = null
    private val requestFINELOCATIONPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            checkPermissions(isGranted)
        }
    private val tourPointsViewModel: TourPointsViewModel by viewModels()
    private var tourPointsMarkers: MutableList<Marker> = mutableListOf()
    private var isTourPointsEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteSelectedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        route = arguments?.getSerializable(Constants.BUNDLE_KEY_ROUTE_SELECTED_DATA) as LineRouteInfo

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_route_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        btnActions()

        isTourPointsEnabled = PreferenceManager.getTourPointsSetting(requireContext())
        if (isTourPointsEnabled) drawTourPoints()

        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
        val inflater = LayoutInflater.from(requireContext())
        val dialogSetting = DialogSettingsBinding.inflate(inflater)
        dialogBuilder.setView(dialogSetting.root)
        val dialog = dialogBuilder.create()

        binding.btnSettings.setOnClickListener {
            dialogSetting.switchTourPoints.isChecked = isTourPointsEnabled
            dialog.show()
        }

        dialogSetting.btnAccept.setOnClickListener {
            isTourPointsEnabled = dialogSetting.switchTourPoints.isChecked
            PreferenceManager.saveTourPointsSetting(requireContext(), isTourPointsEnabled)
            dialog.dismiss()
            if (tourPointsMarkers.isEmpty() && !isTourPointsEnabled) return@setOnClickListener
            if (tourPointsMarkers.isNotEmpty() && !isTourPointsEnabled) {
                tourPointsMarkers.map { marker ->
                    marker.isVisible = false
                }
                return@setOnClickListener
            }
            if (tourPointsMarkers.isNotEmpty() && isTourPointsEnabled) {
                tourPointsMarkers.forEach { marker ->
                    marker.isVisible = true
                }
                return@setOnClickListener
            }
            drawTourPoints()
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
        if (isLocationPermissionGranted()) {
            getLocation()
        }
        if (mMap == null) { return }
        val nRoute = route ?: return
        val rStart = nRoute.start ?: return
        val rEnd = nRoute.end ?: return
        val start = GoogleMapsHelper.locationToLatLng(rStart)
        val end = GoogleMapsHelper.locationToLatLng(rEnd)
        mMap?.addMarker(MarkerOptions().position(start).title(R.string.start_of_route.toString()).icon(GoogleMapsHelper.bitmapFromVector(requireContext(), R.drawable.ic_start_route)).anchor(0.5F, 0.5F))
        mMap?.addMarker(MarkerOptions().position(end).title(R.string.end_of_route.toString()).icon(GoogleMapsHelper.bitmapFromVector(requireContext(), R.drawable.ic_end_route)).anchor(0.5F, 0.5F))

        moveToLocation(start)
        addStopMarkers(nRoute.stops)
        drawPolyline(nRoute.routePoints)
        loadTourPoints()
    }

    private fun btnActions() {
        binding.btnBackList.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun addStopMarkers(list: List<Location>) {
        if (mMap == null) { return }
        for (i in list) {
            mMap?.addMarker(
                MarkerOptions().position(GoogleMapsHelper.locationToLatLng(i)).title(R.string.bus_stop.toString()).icon(GoogleMapsHelper.bitmapFromVector(requireContext(), R.drawable.ic_bus_stop)).anchor(0.5F, 0.5F)
            )
        }
    }

    private fun drawPolyline(list: List<Location>) {
        val map = mMap ?: return
        GoogleMapsHelper.drawPolyline(map, list.map { point -> point.toLatLong() }, route?.color ?: "")
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
            mMap?.uiSettings?.isMyLocationButtonEnabled = false
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
                    mMap?.isMyLocationEnabled = true
                    mMap?.uiSettings?.isMyLocationButtonEnabled = false
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

    private fun moveToLocation(location: LatLng) {
        val cameraTargetLocation = CameraUpdateFactory.newLatLngZoom(location, 15.0f)
        mMap?.animateCamera(cameraTargetLocation)
    }

    private fun drawTourPoints() {
        tourPointsViewModel.tourPoints.observe(viewLifecycleOwner) { tourPoints ->
            tourPoints.forEach { tourPoint ->
                val map = mMap ?: return@forEach
                val destination = tourPoint.destination ?: return@forEach
                lifecycleScope.launch {
                    val category = tourPoint.category ?: return@launch
                    val name = tourPoint.name ?: ""
                    val categoryName = tourPoint.categoryName ?: ""
                    val icon = ImageHelper.getBitMapFromUrl(requireContext(), category.icon) ?: return@launch
                    val newMarker = map.addMarker(
                        MarkerOptions()
                            .position(destination.toLatLong())
                            .icon(icon)
                            .title(name.uppercase())
                            .snippet(categoryName.uppercase())
                    )
                    if (newMarker != null) tourPointsMarkers.add(newMarker)
                }
            }
        }
    }

    private fun loadTourPoints() {
        val currentCityId = PreferenceManager.getCurrentCityID(requireContext())
        tourPointsViewModel.fetchTourPoints(currentCityId)
    }
}
