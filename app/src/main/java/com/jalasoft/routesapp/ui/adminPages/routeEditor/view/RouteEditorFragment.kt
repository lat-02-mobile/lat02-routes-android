package com.jalasoft.routesapp.ui.adminPages.routeEditor.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.GeoPoint
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
    private lateinit var alertDialog: AlertDialog.Builder
    private var routePoints = mutableListOf<Marker?>()
    private var selectedMarker: Marker? = null

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
        alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(getString(R.string.rearrange_points))

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddPoint.setOnClickListener {
            onBtnAddPointClicked()
        }

        binding.btnAddStop.setOnClickListener {
            onBtnAddStopClicked()
        }

        binding.btnDone.setOnClickListener {
            onBtnDoneClick()
        }

        binding.btnSort.setOnClickListener {
            showPickerDialog()
        }
    }

    private fun observers() {
        val errorObserver = Observer<String> { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }
        val successObserver = Observer<Unit> {
            Toast.makeText(requireContext(), R.string.points_updated, Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }
        viewModel.errorMessage.observe(this, errorObserver)
        viewModel.successSave.observe(this, successObserver)
    }

    private fun onBtnDoneClick() {
        if (routePoints.size < 2) {
            Toast.makeText(requireContext(), R.string.empty_route_error, Toast.LENGTH_LONG).show()
            return
        }
        val stops = mutableListOf<GeoPoint>()
        val points = mutableListOf<GeoPoint>()
        routePoints.forEachIndexed { index, marker ->
            marker?.let {
                val position = marker.position
                val isStop = marker.tag as? Boolean
                isStop?.let { stop ->
                    val point = GeoPoint(position.latitude, position.longitude)
                    if (index == 0 || index == (routePoints.size - 1)) {
                        stops.add(point)
                    } else if (stop) {
                        stops.add(point)
                    }
                    points.add(point)
                }
            }
        }
        viewModel.saveRouteDetails(route.id, points, stops)
    }

    private fun onBtnAddPointClicked() {
        binding.btnDone.isEnabled = true
        val isMarkerSelected = selectedMarker != null
        if (isMarkerSelected) {
            val selectedMarkerIndex = routePoints.indexOf(selectedMarker)
            routePoints.removeAt(selectedMarkerIndex)
            changeRoutePointButton(true)
            selectedMarker?.remove()
            binding.btnSort.isEnabled = false
            changeStopButton(false)
            rearrangeMarkerIcons()
        } else {
            val indexStr = (routePoints.size + 1).toString()
            val location = map.cameraPosition.target
            GoogleMapsHelper.getBitmapMarker(requireContext(), R.drawable.ic_point_circle, indexStr)?.let {
                selectedMarker = addMarker(location, it, false)
                routePoints.add(selectedMarker)
            }
            changeRoutePointButton(false)
        }
    }

    private fun onBtnAddStopClicked() {
        binding.btnDone.isEnabled = true
        val isMarkerSelected = selectedMarker != null
        if (isMarkerSelected) {
            val selectedMarkerIndex = routePoints.indexOf(selectedMarker)
            selectedMarker?.let { marker ->
                val isStop = marker.tag as? Boolean
                isStop?.let {
                    changeStopButton(!it)
                    val pointIcon = if (it) R.drawable.ic_point_circle else R.drawable.ic_stop_circle
                    val indexStr = (selectedMarkerIndex + 1).toString()
                    GoogleMapsHelper.getBitmapMarker(requireContext(), pointIcon, indexStr)?.let { bitmap ->
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        marker.tag = !it
                    }
                }
            }
        } else {
            val location = map.cameraPosition.target
            val indexStr = (routePoints.size + 1).toString()
            changeStopButton(true)
            changeRoutePointButton(false)
            GoogleMapsHelper.getBitmapMarker(requireContext(), R.drawable.ic_stop_circle, indexStr)?.let {
                selectedMarker = addMarker(location, it, true)
                routePoints.add(selectedMarker)
            }
        }
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

        binding.btnAddPoint.isEnabled = true
        binding.btnAddStop.isEnabled = true

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
            if (route.stops.contains(location)) {
                GoogleMapsHelper.getBitmapMarker(requireContext(), R.drawable.ic_stop_circle, indexStr)?.let {
                    builder.include(location.toLatLong())
                    routePoints.add(addMarker(location.toLatLong(), it, true))
                }
            } else {
                GoogleMapsHelper.getBitmapMarker(requireContext(), R.drawable.ic_point_circle, indexStr)?.let {
                    builder.include(location.toLatLong())
                    routePoints.add(addMarker(location.toLatLong(), it, false))
                }
            }
        }

        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, Constants.POLYLINE_PADDING)
        map.animateCamera(cu)
    }

    private fun addMarker(point: LatLng, withBitmap: Bitmap, isMarkerStop: Boolean): Marker? {
        val mark = map.addMarker(MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromBitmap(withBitmap)))
        mark?.tag = isMarkerStop
        return mark
    }

    override fun onMarkerClick(clickedMarker: Marker): Boolean {
        if (selectedMarker != null) {
            setDefaultSelectedMarker()
        }
        selectedMarker = clickedMarker
        map.moveCamera(CameraUpdateFactory.newLatLng(clickedMarker.position))
        val selectedMarkerIndex = routePoints.indexOf(selectedMarker)
        val indexStr = (selectedMarkerIndex + 1).toString()
        val isStop = selectedMarker?.tag as? Boolean
        isStop?.let { stop ->
            changeStopButton(stop)
            GoogleMapsHelper.getBitmapMarker(requireContext(), R.drawable.ic_point_selected, indexStr)?.let {
                selectedMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(it))
            }
        }
        binding.btnSort.isEnabled = true
        changeRoutePointButton(false)
        return true
    }

    override fun onMapClick(clickedPoint: LatLng) {
        binding.btnSort.isEnabled = false
        if (selectedMarker != null) {
            setDefaultSelectedMarker()
        }
        changeStopButton(false)
        changeRoutePointButton(true)
    }

    private fun changeRoutePointButton(isActionAdd: Boolean) {
        if (isActionAdd) {
            binding.btnAddPoint.setImageResource(R.drawable.ic_baseline_add_24)
        } else {
            binding.btnAddPoint.setImageResource(R.drawable.ic_baseline_close_24)
        }
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
        if (selectedMarker != null) {
            setDefaultSelectedMarker()
        }
        changeStopButton(false)
        changeRoutePointButton(true)
    }

    private fun rearrangeMarkerIcons() {
        selectedMarker?.let {
            map.moveCamera(CameraUpdateFactory.newLatLng(it.position))
        }
        selectedMarker = null
        routePoints.forEachIndexed { index, marker ->
            val indexStr = (index + 1).toString()
            marker?.let {
                val isStop = it.tag as? Boolean
                isStop?.let { stop ->
                    val icon = if (stop) R.drawable.ic_stop_circle else R.drawable.ic_point_circle
                    GoogleMapsHelper.getBitmapMarker(requireContext(), icon, indexStr)?.let { bitmap ->
                        it.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
                    }
                }
            }
        }
    }

    private fun setDefaultSelectedMarker() {
        val selectedMarkerIndex = routePoints.indexOf(selectedMarker)
        val indexStr = (selectedMarkerIndex + 1).toString()
        val isStop = selectedMarker?.tag as? Boolean
        isStop?.let { stop ->
            val pointIcon = if (stop) R.drawable.ic_stop_circle else R.drawable.ic_point_circle
            GoogleMapsHelper.getBitmapMarker(requireContext(), pointIcon, indexStr)?.let {
                selectedMarker?.setIcon(BitmapDescriptorFactory.fromBitmap(it))
            }
        }
        selectedMarker = null
    }

    private fun showPickerDialog() {
        val view: View = this.layoutInflater.inflate(R.layout.dialog_picker, null)
        alertDialog.setView(view)

        val picker = view.findViewById<View>(R.id.positionPicker) as NumberPicker
        picker.minValue = 1
        picker.maxValue = routePoints.size
        val index = routePoints.indexOf(selectedMarker)
        picker.value = index + 1
        alertDialog.setPositiveButton(android.R.string.ok) { _, _ ->
            routePoints.removeAt(index)
            routePoints.add(picker.value - 1, selectedMarker)
            rearrangeMarkerIcons()
        }.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = alertDialog.create()
        dialog.show()
    }
}
