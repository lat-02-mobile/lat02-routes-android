package com.jalasoft.routesapp.ui.routes.route.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import com.jalasoft.routesapp.databinding.FragmentRouteBinding
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper

class RouteFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentRouteBinding? = null
    private val binding get() = _binding!!
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mMap: GoogleMap? = null
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
        _binding = FragmentRouteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        // Add markers
        val firstPoint = LatLng(37.420614, -122.078212)
        val secondPoint = LatLng(37.420463, -122.078052)
        val thirdPoint = LatLng(37.417258, -122.078144)
        val fourthPoint = LatLng(37.415592, -122.078204)
        val fifthPoint = LatLng(37.415464, -122.079019)
        val list = arrayListOf<LatLng>()
        list.add(firstPoint)
        list.add(secondPoint)
        list.add(thirdPoint)
        list.add(fourthPoint)
        list.add(fifthPoint)

        val oriDest = arrayListOf<LatLng>()
        oriDest.add(firstPoint)
        oriDest.add(fifthPoint)

        val stops = arrayListOf<LatLng>()
        stops.add(thirdPoint)

        addMarkers(oriDest)
        addStopMarkers(stops)
        addPolyline(list)
    }

    private fun btnActions() {
        binding.btnBackList.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSettings.setOnClickListener {
            // TODO
        }
    }

    fun addMarkers(list: ArrayList<LatLng>) {
        for (i in list) {
            mMap!!.addMarker(MarkerOptions().position(i).title("Point"))
        }
    }

    fun addStopMarkers(list: ArrayList<LatLng>) {
        for (i in list) {
            mMap!!.addMarker(
                MarkerOptions().position(i).title("Stop").icon(GoogleMapsHelper.bitmapFromVector(requireContext(), R.drawable.ic_bus_stop))
            )
        }
    }
    fun addPolyline(list: ArrayList<LatLng>) {
        for (i in list.indices) {
            val item = list[i]
            if (item == list.last()) {
                mMap!!.addPolyline(
                    PolylineOptions()
                        .add(list[i - 1], item)
                        .width(5f)
                        .color(Color.RED)
                        .geodesic(true)
                )
            } else {
                mMap!!.addPolyline(
                    PolylineOptions()
                        .add(item, list[i + 1])
                        .width(5f)
                        .color(Color.RED)
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
}
