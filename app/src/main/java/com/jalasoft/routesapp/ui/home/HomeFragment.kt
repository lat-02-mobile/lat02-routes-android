package com.jalasoft.routesapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.ui.auth.login.viewModel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 5
    private val viewModel: LoginViewModel by viewModels()

    companion object {
        const val TAG = "HomeFragment"
    }

    private val requestFINELOCATIONPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocation()
                Log.d(TAG, "has permissions fine location")
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please give precise location permissions",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
//    val requestCOARSELOCATIONPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                Log.d(TAG, "has permissions coarse")
//            } else {
//                Log.d(TAG, "do not has permissions")
//
//            }
//        }

    private var mMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        view.findViewById<ImageButton>(R.id.btn_current_location).setOnClickListener {
//            getLocation()
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    getLocation()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    AlertDialog.Builder(requireContext()).setTitle("Access location Permission")
                        .setMessage("Please allow the location permission")
                        .setPositiveButton("Allow") { _, _ ->
                            requestFINELOCATIONPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }.show()
                }
                else -> {
                    requestFINELOCATIONPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }

        // Button for logout
//        view.findViewById<Button>(R.id.btn_sign_out).setOnClickListener {
//            viewModel.signOutUser()
//            findNavController().navigate(R.id.loginFragment)
//        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.style_silver
            )
        )
        mMap = googleMap
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient.lastLocation.addOnCompleteListener() { task ->
                val location: Location? = task.result
                if (location != null) {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val list: List<Address> =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    moveToLocation(location)
                    Toast.makeText(
                        requireContext(),
                        "Latitude: ${list[0].latitude}, Longitude: ${list[0].longitude}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Log.d("location", "nulling")
                }
            }
        } else {
            Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun moveToLocation(location: Location) {
        val latlng = LatLng(location.latitude, location.longitude)
        val location = CameraUpdateFactory.newLatLngZoom(latlng, 13f)
        mMap?.animateCamera(location)
    }
}
