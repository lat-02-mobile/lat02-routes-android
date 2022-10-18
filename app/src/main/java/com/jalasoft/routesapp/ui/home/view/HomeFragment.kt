package com.jalasoft.routesapp.ui.home.view

import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.api.models.gmaps.EndLocation
import com.jalasoft.routesapp.data.api.models.gmaps.StartLocation
import com.jalasoft.routesapp.data.model.local.FavoriteDestinationEntity
import com.jalasoft.routesapp.data.model.local.RouteDetail
import com.jalasoft.routesapp.data.model.local.RouteDetail.Companion.getRouteDetailFromLocationList
import com.jalasoft.routesapp.data.model.remote.AvailableTransport
import com.jalasoft.routesapp.databinding.DialogSettingsBinding
import com.jalasoft.routesapp.ui.home.adapters.PossibleRouteAdapter
import com.jalasoft.routesapp.ui.home.adapters.RouteDetailsAdapter
import com.jalasoft.routesapp.util.CustomProgressDialog
import com.jalasoft.routesapp.util.Extensions.toLatLong
import com.jalasoft.routesapp.util.Extensions.toLocation
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.Constants
import com.jalasoft.routesapp.util.helpers.GoogleMapsHelper
import com.jalasoft.routesapp.util.helpers.WalkDirection

enum class HomeSelectionStatus {
    SELECTING_POINTS, SHOWING_POSSIBLE_ROUTES, SHOWING_ROUTE_DETAILS
}

class HomeFragment : HomeBaseFragment(), PossibleRouteAdapter.IPossibleRouteListener {
    private var homeSelectionStatus = HomeSelectionStatus.SELECTING_POINTS
    private var isFavorite = false
    private var favoriteDestination: FavoriteDestinationEntity? = null
    private val progressDialog by lazy { CustomProgressDialog(requireActivity()) }

    private lateinit var possibleRoutesBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.possibleRouteBottomLayout.view.visibility = View.GONE
        binding.routeDetailsBottomLayout.view.visibility = View.GONE
        possibleRoutesBottomSheetBehavior =
            BottomSheetBehavior.from(binding.possibleRouteBottomLayout.view)
        setUpButtons()
        setPossibleRoutesRecycler()
        setRouteDetailRecycler()
    }

    private fun setUpButtons() {
        // Go back button
        binding.btnGoBack.visibility = View.GONE
        binding.btnGoBack.setOnClickListener {
            when (homeSelectionStatus) {
                HomeSelectionStatus.SELECTING_POINTS -> {
                    when (selectPointsStatus) {
                        SelectPointsStatus.ORIGIN -> {
                            return@setOnClickListener
                        }
                        SelectPointsStatus.DESTINATION -> {
                            viewModel.setOrigin(null)
                            selectPointsStatus = SelectPointsStatus.ORIGIN
                        }
                        SelectPointsStatus.BOTH -> {
                            viewModel.setDestination(null)
                            selectPointsStatus = SelectPointsStatus.DESTINATION
                        }
                    }
                }
                HomeSelectionStatus.SHOWING_POSSIBLE_ROUTES -> {
                    removePossibleRouteFromMap()
                    viewModel.clearPossibleRoutes()
                    binding.btnCheckNextLocation.visibility = View.VISIBLE
                    binding.btnCurrentLocation.visibility = View.VISIBLE
                    binding.pin.visibility = View.VISIBLE
                    homeSelectionStatus = HomeSelectionStatus.SELECTING_POINTS
                    binding.possibleRouteBottomLayout.view.visibility = View.GONE
                    binding.bottomLayout1.bottomSheetLayout.visibility = View.VISIBLE
                }
                HomeSelectionStatus.SHOWING_ROUTE_DETAILS -> {
                    homeSelectionStatus = HomeSelectionStatus.SHOWING_POSSIBLE_ROUTES
                    binding.routeDetailsBottomLayout.view.visibility = View.GONE
                    binding.possibleRouteBottomLayout.view.visibility = View.VISIBLE
                }
            }
        }

        // Next button
        binding.btnCheckNextLocation.setOnClickListener {
            when (selectPointsStatus) {
                SelectPointsStatus.ORIGIN -> {
                    val newLocation = getSelectedLocation() ?: LatLng(0.0, 0.0)
                    markerOrigin = addMarker(mMap, newLocation, R.drawable.ic_origin)
                    viewModel.setOrigin(newLocation)
                    selectPointsStatus = if (args.preSelectDestCoords != null) {
                        markerDestination = addMarker(mMap, args.preSelectDestCoords ?: LatLng(0.0, 0.0), R.drawable.ic_destination)
                        viewModel.setDestination(args.preSelectDestCoords)
                        SelectPointsStatus.BOTH
                    } else SelectPointsStatus.DESTINATION
                }
                SelectPointsStatus.DESTINATION -> {
                    val newLocation = getSelectedLocation() ?: LatLng(0.0, 0.0)
                    markerDestination = addMarker(mMap, newLocation, R.drawable.ic_destination)
                    viewModel.setDestination(newLocation)
                    selectPointsStatus = SelectPointsStatus.BOTH
                }
                SelectPointsStatus.BOTH -> {
                    val origin = viewModel.selectedOrigin.value ?: return@setOnClickListener
                    val destination =
                        viewModel.selectedDestination.value ?: return@setOnClickListener
                    if (origin.toLocation().distanceTo(destination.toLocation()) <= Constants.MINIMUM_DISTANCE_ORIGIN_DESTINATION
                    ) {
                        mMap?.let {
                            drawWalkingPath(
                                StartLocation(origin.latitude, origin.longitude),
                                EndLocation(destination.latitude, destination.longitude),
                                it
                            ) {}
                        }
                    } else {
                        val currentCityId = PreferenceManager.getCurrentCityID(requireContext())
                        val lineRoutePaths =
                            viewModel.getRoutePaths(requireContext(), currentCityId)
                        viewModel.getPossibleRoutes(lineRoutePaths, origin, destination)
                        if (viewModel.possibleRoutesList.value?.isNotEmpty() == true) possibleRoutesBottomSheetBehavior.state =
                            BottomSheetBehavior.STATE_EXPANDED
                    }
                    binding.btnCheckNextLocation.visibility = View.GONE
                    binding.btnCurrentLocation.visibility = View.GONE
                    binding.pin.visibility = View.GONE
                    homeSelectionStatus = HomeSelectionStatus.SHOWING_POSSIBLE_ROUTES
                    binding.bottomLayout1.bottomSheetLayout.visibility = View.GONE
                    binding.possibleRouteBottomLayout.view.visibility = View.VISIBLE
                }
            }
        }

        binding.routeDetailsBottomLayout.favoriteButton.setOnClickListener {
            if (isFavorite) {
                onTapDeleteFavorite()
            } else {
                onTapSaveFavorite()
            }
        }

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

        viewModel.possibleRoutesList.observe(viewLifecycleOwner) {
            updatePossibleRouteRecycler(it.toMutableList())
        }
    }

    private fun setPossibleRoutesRecycler() {
        binding.possibleRouteBottomLayout.recyclerPossibleRoutes.layoutManager =
            LinearLayoutManager(requireContext())
        binding.possibleRouteBottomLayout.recyclerPossibleRoutes.adapter =
            PossibleRouteAdapter(mutableListOf(), this)
    }

    private fun updatePossibleRouteRecycler(list: List<AvailableTransport>) {
        (binding.possibleRouteBottomLayout.recyclerPossibleRoutes.adapter as PossibleRouteAdapter).updateList(
            list.toMutableList()
        )
    }

    private fun setRouteDetailRecycler() {
        binding.routeDetailsBottomLayout.routeDetailsRecycler.layoutManager =
            LinearLayoutManager(requireContext())
        binding.routeDetailsBottomLayout.routeDetailsRecycler.adapter =
            RouteDetailsAdapter(mutableListOf())
    }

    private fun updateRouteDetailRecycler(list: List<RouteDetail>) {
        (binding.routeDetailsBottomLayout.routeDetailsRecycler.adapter as RouteDetailsAdapter).updateList(
            list.toMutableList()
        )
    }

    override fun onPossibleRouteTap(possibleRoute: AvailableTransport, position: Int) {
        val selectedOrigin = viewModel.selectedOrigin.value ?: return
        val selectedDestination = viewModel.selectedDestination.value ?: return
        isFavorite =
            verifyFavoriteDestination(selectedDestination.latitude, selectedDestination.longitude)
        if (isFavorite) {
            binding.routeDetailsBottomLayout.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            binding.routeDetailsBottomLayout.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
        val map = mMap ?: return
        homeSelectionStatus = HomeSelectionStatus.SHOWING_ROUTE_DETAILS
        val text = RoutesAppApplication.resource?.getString(R.string.calculating_route)
        progressDialog.start(text ?: "")
        val details = mutableListOf<RouteDetail>()
        updateRouteDetailRecycler(details.toList())
        binding.routeDetailsBottomLayout.view.visibility = View.VISIBLE
        binding.possibleRouteBottomLayout.view.visibility = View.GONE
        removePossibleRouteFromMap()

        val originName = Geocoder(requireContext()).getFromLocation(selectedOrigin.latitude, selectedOrigin.longitude, 1)
        val destinationName = Geocoder(requireContext()).getFromLocation(selectedDestination.latitude, selectedDestination.longitude, 1)

        binding.routeDetailsBottomLayout.tvDestinationName.text = destinationName.first().thoroughfare
        binding.routeDetailsBottomLayout.tvOriginName.text = originName.first().thoroughfare

        val startRouteMarker = addMarker(map, selectedDestination, R.drawable.ic_start_route)
        if (startRouteMarker != null) startEndMarkers.add(startRouteMarker)

        val builder = LatLngBounds.Builder()
        val start = possibleRoute.transports.first().routePoints.first().toLatLong()
        val end = possibleRoute.transports.last().routePoints.last().toLatLong()
        drawWalkingPath(StartLocation(selectedOrigin.latitude, selectedOrigin.longitude), EndLocation(start.latitude, start.longitude), map) { list ->
            list.map { location ->
                builder.include(location.toLatLong())
            }
            details.add(0, getRouteDetailFromLocationList("", list, blackIcon = "", whiteIcon = "", walkDirection = WalkDirection.TO_FIRST_STOP))
        }

        val endRouteMarker = addMarker(map, end, R.drawable.ic_end_route)
        if (endRouteMarker != null) startEndMarkers.add(endRouteMarker)

        for (line in possibleRoute.transports) {
            val polyline = GoogleMapsHelper.drawPolyline(map, line.routePoints.map { point -> point.toLatLong() }, line.color)
            polylines.add(polyline)
            val lineRouteName = "${line.routeName}, ${line.lineName}"
            details.add(getRouteDetailFromLocationList(lineRouteName, line.routePoints, line.whiteIcon, line.blackIcon, line.category, line.averageVelocity))
            line.routePoints.map { location ->
                builder.include(location.toLatLong())
            }
        }
        if (possibleRoute.transports.size > 1) {
            val first = possibleRoute.transports.first().routePoints.last().toLatLong()
            val second = possibleRoute.transports.last().routePoints.first().toLatLong()

            val stopMarkerFrom = addMarker(map, first, R.drawable.ic_bus_stop)
            if (stopMarkerFrom != null) startEndMarkers.add(stopMarkerFrom)
            val stopMarkerTo = addMarker(map, second, R.drawable.ic_bus_stop)
            if (stopMarkerTo != null) startEndMarkers.add(stopMarkerTo)

            drawWalkingPath(StartLocation(first.latitude, first.longitude), EndLocation(second.latitude, second.longitude), map) { list ->
                list.map { location ->
                    builder.include(location.toLatLong())
                }
                details.add(2, getRouteDetailFromLocationList("", list, blackIcon = "", whiteIcon = "", walkDirection = WalkDirection.TO_NEXT_STOP))
            }
        }
        drawWalkingPath(StartLocation(end.latitude, end.longitude), EndLocation(selectedDestination.latitude, selectedDestination.longitude), map) { list ->
            list.map { location ->
                builder.include(location.toLatLong())
            }
            details.add(getRouteDetailFromLocationList("", list, blackIcon = "", whiteIcon = "", walkDirection = WalkDirection.TO_DESTINATION))
            updateRouteDetailRecycler(details.toList())
        }
        progressDialog.stop()
        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, Constants.POLYLINE_PADDING)
        mMap?.animateCamera(cu)
    }

    private fun drawWalkingPath(
        startLocation: StartLocation,
        endLocation: EndLocation,
        googleMap: GoogleMap,
        completion: (list: List<Location>) -> Unit
    ) {
        val result = viewModel.fetchDirections(startLocation, endLocation)
        result?.let { route ->
            if (route.isNotEmpty()) {
                val shape = route.first().overviewPolyline?.points
                shape?.let { points ->
                    val latLngList = PolyUtil.decode(points)
                    val dotPolyline = GoogleMapsHelper.drawDotPolyline(googleMap, latLngList)
                    polylines.add(dotPolyline)
                    completion(latLngList.map { it.toLocation() })
                }
            }
        }
    }

    private fun verifyFavoriteDestination(lat: Double, lng: Double): Boolean {
        var isFavorite = false
        val newDestination = Location(LocationManager.NETWORK_PROVIDER)
        newDestination.latitude = lat
        newDestination.longitude = lng
        val destinations = Location(LocationManager.NETWORK_PROVIDER)
        val list = viewModel.getFavoriteDestinationsByCityAndUserId(requireContext())

        return if (list.isEmpty()) {
            false
        } else {
            for (item in list) {
                destinations.latitude = item.destination.latitude
                destinations.longitude = item.destination.longitude

                val dist = destinations.distanceTo(newDestination)
                isFavorite = dist < Constants.MINIMUM_DISTANCE_TO_STILL_FAVORITE
                if (isFavorite) {
                    favoriteDestination = item
                }
            }
            isFavorite
        }
    }

    private fun onTapSaveFavorite() {
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setTitle(R.string.save_dest)
        builder.setMessage(R.string.choose_name_for_new_fav_dest)
        val input = EditText(context)

        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setPaddingRelative(20, 20, 20, 20)
        builder.setView(input)

        builder.setPositiveButton(R.string.save) { _, _ ->
            isFavorite = true
            val name = input.text.toString()
            viewModel.selectedDestination.value?.let { destination ->
                viewModel.saveFavoriteDestination(
                    destination.latitude,
                    destination.longitude,
                    name,
                    requireContext()
                )
                binding.routeDetailsBottomLayout.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_24)
            }
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }
        builder.setCancelable(true)
        builder.show()
    }

    private fun onTapDeleteFavorite() {
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setTitle(R.string.remove_fav_dest)
        val name = favoriteDestination?.name ?: ""
        val message = RoutesAppApplication.resource?.getString(R.string.sure_remove_fav_dest)
            .toString() + " " + name
        builder.setMessage(message)
        builder.setPositiveButton(R.string.yes) { _, _ ->
            if (favoriteDestination != null) {
                isFavorite = false
                viewModel.deleteFavoriteDestination(favoriteDestination as FavoriteDestinationEntity)
                binding.routeDetailsBottomLayout.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
        }
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }
        builder.setCancelable(true)
        builder.show()
    }

    private fun removePossibleRouteFromMap() {
        polylines.forEach {
            it.remove()
        }
        startEndMarkers.forEach {
            it.remove()
        }
    }
}
