package com.jalasoft.routesapp.ui.routes.view

import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.PolyUtil
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.api.models.gmaps.EndLocation
import com.jalasoft.routesapp.data.api.models.gmaps.StartLocation
import com.jalasoft.routesapp.data.model.local.RouteDetail
import com.jalasoft.routesapp.data.model.remote.AvailableTransport
import com.jalasoft.routesapp.data.model.remote.LineCategoryIcons
import com.jalasoft.routesapp.databinding.FragmentPossibleRoutesBinding
import com.jalasoft.routesapp.ui.routes.adapters.PossibleRouteAdapter
import com.jalasoft.routesapp.ui.routes.adapters.RouteDetailsAdapter
import com.jalasoft.routesapp.ui.routes.viewModel.RoutesViewModel
import com.jalasoft.routesapp.util.Extensions.toLatLong
import com.jalasoft.routesapp.util.Extensions.toLocation
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.Constants
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper
import com.jalasoft.routesapp.util.helpers.WalkDirection
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class PossibleRoutesFragment : Fragment(), OnMapReadyCallback, PossibleRouteAdapter.IPossibleRouteListener {
    private var _binding: FragmentPossibleRoutesBinding? = null
    private val binding get() = _binding!!
    private var mMap: GoogleMap? = null
    private var index: Int = 0
    private var isShowingDetails = false

    private val viewModel: RoutesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPossibleRoutes()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPossibleRoutesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPossibleRoutesRecycler()
        setRouteDetailRecycler()
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        binding.detailsBottomSheet.view.visibility = View.GONE

        binding.btnGoBack.setOnClickListener {
            if (isShowingDetails) {
                binding.detailsBottomSheet.view.visibility = View.GONE
                binding.bottomSheet.view.visibility = View.VISIBLE
            } else {
                findNavController().popBackStack()
            }
            isShowingDetails = !isShowingDetails
        }

        binding.detailsBottomSheet.favoriteButton.setOnClickListener {
            Toast.makeText(requireContext(), "Added favorite", Toast.LENGTH_SHORT).show()
        }

        viewModel.possibleRoutesList.observe(viewLifecycleOwner) {
            updatePossibleRouteRecycler(it.toMutableList())
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
        setMapOnCurrentCity()
    }

    private fun moveToLocation(location: LatLng, zoom: Float) {
        val cameraTargetLocation = CameraUpdateFactory.newLatLngZoom(location, zoom)
        mMap?.animateCamera(cameraTargetLocation)
    }

    private fun setMapOnCurrentCity() {
        Log.d("Possible Route", PreferenceManager.getCurrentCity(requireContext()))
        val currentLat = PreferenceManager.getCurrentLocationLat(requireContext())
        val currentLng = PreferenceManager.getCurrentLocationLng(requireContext())
        if (currentLat != PreferenceManager.NOT_FOUND && currentLng != PreferenceManager.NOT_FOUND) {
            val location = LatLng(currentLat.toDouble(), currentLng.toDouble())
            moveToLocation(location, 11F)
        } else {
            findNavController().navigate(R.id.action_homeFragment_to_cityPickerFragment)
        }
    }

    private fun setPossibleRoutesRecycler() {
        binding.bottomSheet.recyclerPossibleRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.bottomSheet.recyclerPossibleRoutes.adapter = PossibleRouteAdapter(mutableListOf(), this)
    }

    private fun updatePossibleRouteRecycler(list: List<AvailableTransport>) {
        (binding.bottomSheet.recyclerPossibleRoutes.adapter as PossibleRouteAdapter).updateList(list.toMutableList())
    }

    private fun setRouteDetailRecycler() {
        binding.detailsBottomSheet.routeDetailsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.detailsBottomSheet.routeDetailsRecycler.adapter = RouteDetailsAdapter(mutableListOf())
    }

    private fun updateRouteDetailRecycler(list: List<RouteDetail>) {
        (binding.detailsBottomSheet.routeDetailsRecycler.adapter as RouteDetailsAdapter).updateList(list.toMutableList())
    }

    override fun onPossibleRouteTap(possibleRoute: AvailableTransport) {
        mMap?.let {
            binding.progressBar.visibility = View.VISIBLE
            val details = mutableListOf<RouteDetail>()
            updateRouteDetailRecycler(details.toList())
            isShowingDetails = true
            binding.detailsBottomSheet.view.visibility = View.VISIBLE
            binding.bottomSheet.view.visibility = View.GONE
            it.clear()
            val origin = LatLng(-16.52093, -68.1242)
            val destination = LatLng(-16.52476, -68.11937)
            val originName = Geocoder(requireContext()).getFromLocation(origin.latitude, origin.longitude, 1)
            val destinationName = Geocoder(requireContext()).getFromLocation(destination.latitude, destination.longitude, 1)

            binding.detailsBottomSheet.tvDestinationName.text = destinationName.first().thoroughfare
            binding.detailsBottomSheet.tvOriginName.text = originName.first().thoroughfare
            addMarker(it, origin, R.drawable.ic_origin)
            addMarker(it, destination, R.drawable.ic_start_route)

            val builder = LatLngBounds.Builder()
            val start = possibleRoute.transports.first().routePoints.first().toLatLong()
            val end = possibleRoute.transports.last().routePoints.last().toLatLong()
            drawWalkingPath(StartLocation(origin.latitude, origin.longitude), EndLocation(start.latitude, start.longitude), it) { list ->
                list.map { location ->
                    builder.include(location.toLatLong())
                }
                details.add(0, getRouteDetailFromLocationList("", list, walkDirection = WalkDirection.TO_FIRST_STOP))
            }
            addMarker(it, start, R.drawable.ic_start_route)
            addMarker(it, end, R.drawable.ic_end_route)
            for (line in possibleRoute.transports) {
                GoogleMapsHelper.drawPolyline(it, line.routePoints.map { point -> point.toLatLong() }, line.color)
                val lineRouteName = "${line.routeName}, ${line.lineName}"
                details.add(getRouteDetailFromLocationList(lineRouteName, line.routePoints, line.icons, line.category, line.averageVelocity))
                line.routePoints.map { location ->
                    builder.include(location.toLatLong())
                }
            }
            if (possibleRoute.transports.size > 1) {
                val first = possibleRoute.transports.first().routePoints.last().toLatLong()
                val second = possibleRoute.transports.last().routePoints.first().toLatLong()
                addMarker(it, first, R.drawable.ic_bus_stop)
                addMarker(it, second, R.drawable.ic_bus_stop)
                drawWalkingPath(StartLocation(first.latitude, first.longitude), EndLocation(second.latitude, second.longitude), it) { list ->
                    list.map { location ->
                        builder.include(location.toLatLong())
                    }
                    details.add(2, getRouteDetailFromLocationList("", list, walkDirection = WalkDirection.TO_NEXT_STOP))
                }
            }
            drawWalkingPath(StartLocation(end.latitude, end.longitude), EndLocation(destination.latitude, destination.longitude), it) { list ->
                list.map { location ->
                    builder.include(location.toLatLong())
                }
                details.add(getRouteDetailFromLocationList("", list, walkDirection = WalkDirection.TO_DESTINATION))
                binding.progressBar.visibility = View.GONE
                updateRouteDetailRecycler(details.toList())
            }
            val bounds = builder.build()
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, Constants.POLYLINE_PADDING)
            mMap?.animateCamera(cu)
        }
    }

    private fun getRouteDetailFromLocationList(
        routeName: String,
        list: List<Location>,
        icon: LineCategoryIcons = LineCategoryIcons(),
        category: String = "",
        averageVelocity: Double = Constants.AVG_WALKING_PACE,
        walkDirection: WalkDirection = WalkDirection.IS_NOT_WALKING
    ): RouteDetail {
        val distance = GoogleMapsHelper.getLocationListDistance(list)
        val estimatedTime = distance / averageVelocity
        return RouteDetail(routeName, category, icon, estimatedTime.roundToInt(), distance.roundToInt(), walkDirection)
    }

    private fun drawWalkingPath(startLocation: StartLocation, endLocation: EndLocation, googleMap: GoogleMap, completion: (list: List<Location>) -> Unit) {
        val result = viewModel.fetchDirections(startLocation, endLocation)
        result?.let { route ->
            if (route.isNotEmpty()) {
                val shape = route.first().overviewPolyline?.points
                shape?.let { points ->
                    val latLngList = PolyUtil.decode(points)
                    GoogleMapsHelper.drawDotPolyline(googleMap, latLngList)
                    completion(latLngList.map { it.toLocation() })
                }
            }
        }
    }

    private fun addMarker(googleMap: GoogleMap, point: LatLng, withDrawable: Int) {
        googleMap.addMarker(MarkerOptions().position(point).icon(GoogleMapsHelper.bitmapFromVector(requireContext(), withDrawable)).anchor(0.5F, 0.5F))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
