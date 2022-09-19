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
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.LineRoutePath
import com.jalasoft.routesapp.databinding.FragmentRouteSelectedBinding
import com.jalasoft.routesapp.util.helpers.Constants
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper

class RouteSelectedFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentRouteSelectedBinding? = null
    private val binding get() = _binding!!
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mMap: GoogleMap? = null
    private var route: LineRoutePath? = null
    private var positionSelected = 0
    private val requestFINELOCATIONPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            checkPermissions(isGranted)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRouteSelectedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        route = arguments?.getSerializable(Constants.BUNDLE_KEY_ROUTE_SELECTED_DATA) as LineRoutePath
        positionSelected = arguments?.getInt(Constants.BUNDLE_KEY_ROUTE_SELECTED_POSITION) ?: 0

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_route_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        btnActions()
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
        mMap?.addMarker(MarkerOptions().position(start).title(R.string.start_of_route.toString()).icon(GoogleMapsHelper.bitmapFromVector(requireContext(), R.drawable.ic_start_route)))
        mMap?.addMarker(MarkerOptions().position(end).title(R.string.end_of_route.toString()).icon(GoogleMapsHelper.bitmapFromVector(requireContext(), R.drawable.ic_end_route)))

        moveToLocation(start, 15F)
        addStopMarkers(nRoute.stops)
        drawPolyline(nRoute.routePoints)
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
                MarkerOptions().position(GoogleMapsHelper.locationToLatLng(i)).title(R.string.bus_stop.toString()).icon(GoogleMapsHelper.bitmapFromVector(requireContext(), R.drawable.ic_bus_stop))
            )
        }
    }

    private fun drawPolyline(list: List<Location>) {
        if (mMap == null) { return }
        for (i in list.indices) {
            val item = list[i]
            if (item == list.last()) {
                mMap?.addPolyline(
                    PolylineOptions()
                        .add(GoogleMapsHelper.locationToLatLng(list[i - 1]), GoogleMapsHelper.locationToLatLng(item))
                        .width(10f)
                        .color(resources.getColor(R.color.color_primary, null))
                        .geodesic(true)
                )
            } else {
                mMap?.addPolyline(
                    PolylineOptions()
                        .add(GoogleMapsHelper.locationToLatLng(item), GoogleMapsHelper.locationToLatLng(list[i + 1]))
                        .width(10f)
                        .color(resources.getColor(R.color.color_primary, null))
                        .geodesic(true)
                )
            }
        }
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

    private fun moveToLocation(location: LatLng, zoom: Float) {
        val cameraTargetLocation = CameraUpdateFactory.newLatLngZoom(location, zoom)
        mMap?.animateCamera(cameraTargetLocation)
    }
}
