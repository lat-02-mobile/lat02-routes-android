package com.jalasoft.routesapp.ui.adminPages.routeEditor.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.databinding.FragmentRouteEditorBinding
import com.jalasoft.routesapp.ui.adminPages.routeEditor.viewModel.RouteEditorViewModel
import com.jalasoft.routesapp.util.Extensions.toLatLong
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.Constants
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteEditorFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraMoveListener {

    private var _binding: FragmentRouteEditorBinding? = null
    private val binding get() = _binding!!

    private var mMap: GoogleMap? = null
    private val map get() = mMap!!

    private lateinit var route: LineRouteInfo
    private var routePoints = mutableListOf<Marker?>()
    private var routeStops = mutableListOf<Marker?>()

    val viewModel: RouteEditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observers()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_route_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
        viewModel.errorMessage.observe(this, errorObserver)
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.style_silver
            )
        )
        mMap = googleMap
        route = arguments?.getSerializable(Constants.BUNDLE_KEY_ROUTE_SELECTED_DATA) as LineRouteInfo
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener(this)
        map.setOnCameraMoveListener(this)

        if (route.stops.isEmpty()) {
            setMapOnCurrentCity()
        } else {
            setMarkers()
        }
    }

    private fun setMapOnCurrentCity() {
        val currentLat = PreferenceManager.getCurrentLocationLat(requireContext())
        val currentLng = PreferenceManager.getCurrentLocationLng(requireContext())
        if (currentLat != PreferenceManager.NOT_FOUND && currentLng != PreferenceManager.NOT_FOUND) {
            val location = LatLng(currentLat.toDouble(), currentLng.toDouble())
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 11F))
        }
    }

    private fun setMarkers() {
        val builder = LatLngBounds.Builder()

        route.routePoints.forEachIndexed { index, location ->
            val indexStr = (index + 1).toString()
            GoogleMapsHelper.getBitmapMarker(requireContext(), R.drawable.ic_point_circle, indexStr)?.let {
                builder.include(location.toLatLong())
                routePoints.add(addMarker(location.toLatLong(), it))
            }
            if (route.stops.contains(location)) {
                GoogleMapsHelper.getBitmapMarker(requireContext(), R.drawable.ic_stop_circle, indexStr)?.let {
                    builder.include(location.toLatLong())
                    routeStops.add(addMarker(location.toLatLong(), it))
                }
            }
        }

        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, Constants.POLYLINE_PADDING)
        map.animateCamera(cu)
    }

    private fun addMarker(point: LatLng, withBitmap: Bitmap): Marker? {
        return map.addMarker(MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromBitmap(withBitmap)))
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        map.moveCamera(CameraUpdateFactory.newLatLng(p0.position))
        binding.btnSort.isEnabled = true
        val locationIsStop = routeStops.firstOrNull { maker ->
            maker?.position?.equals(p0.position) ?: false
        }
        changeStopButton(locationIsStop != null)
        binding.btnAddPoint.setImageResource(R.drawable.ic_baseline_close_24)
        return true
    }

    override fun onMapClick(p0: LatLng) {
        binding.btnSort.isEnabled = false
        changeStopButton(false)
        binding.btnAddPoint.setImageResource(R.drawable.ic_baseline_add_24)
    }

    private fun changeStopButton(isMarkerStop: Boolean) {
        if (isMarkerStop) {
            binding.btnAddStop.setImageResource(R.drawable.delete_bust_stop)
        } else {
            binding.btnAddStop.setImageResource(R.drawable.ic_bus_stop_no_bg)
        }
    }

    override fun onCameraMove() {
        binding.btnSort.isEnabled = false
        changeStopButton(false)
        binding.btnAddPoint.setImageResource(R.drawable.ic_baseline_add_24)
    }
}
