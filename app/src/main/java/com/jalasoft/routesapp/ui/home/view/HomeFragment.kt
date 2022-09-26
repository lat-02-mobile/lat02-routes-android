package com.jalasoft.routesapp.ui.home.view

import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.PolyUtil
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.api.models.gmaps.EndLocation
import com.jalasoft.routesapp.data.api.models.gmaps.StartLocation
import com.jalasoft.routesapp.data.model.local.FavoriteDestinationEntity
import com.jalasoft.routesapp.data.model.local.RouteDetail
import com.jalasoft.routesapp.data.model.local.RouteDetail.Companion.getRouteDetailFromLocationList
import com.jalasoft.routesapp.data.model.remote.AvailableTransport
import com.jalasoft.routesapp.ui.home.adapters.PossibleRouteAdapter
import com.jalasoft.routesapp.ui.home.adapters.RouteDetailsAdapter
import com.jalasoft.routesapp.util.Extensions.toLatLong
import com.jalasoft.routesapp.util.Extensions.toLocation
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.possibleRouteBottomLayout.view.visibility = View.GONE
        binding.routeDetailsBottomLayout.view.visibility = View.GONE
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
                    if (viewModel.selectedDestination.value != null) {
                        viewModel.setDestination(null)
                    } else {
                        viewModel.setOrigin(null)
                    }
                }
                HomeSelectionStatus.SHOWING_POSSIBLE_ROUTES -> {
                    mMap?.clear()
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
            if (viewModel.selectedOrigin.value == null) {
                val newLocation = getSelectedLocation()
                viewModel.setOrigin(newLocation)
            } else if (viewModel.selectedDestination.value == null) {
                val newLocation = getSelectedLocation()
                viewModel.setDestination(newLocation)
            } else {
                binding.btnCheckNextLocation.visibility = View.GONE
                binding.btnCurrentLocation.visibility = View.GONE
                binding.pin.visibility = View.GONE
                viewModel.getPossibleRoutes()
                homeSelectionStatus = HomeSelectionStatus.SHOWING_POSSIBLE_ROUTES
                binding.bottomLayout1.bottomSheetLayout.visibility = View.GONE
                binding.possibleRouteBottomLayout.view.visibility = View.VISIBLE
            }
        }

        binding.routeDetailsBottomLayout.favoriteButton.setOnClickListener {
            if (isFavorite) {
                onTapDeleteFavorite()
            } else {
                onTapSaveFavorite()
            }
        }

        viewModel.possibleRoutesList.observe(viewLifecycleOwner) {
            updatePossibleRouteRecycler(it.toMutableList())
        }
    }

    private fun setPossibleRoutesRecycler() {
        binding.possibleRouteBottomLayout.recyclerPossibleRoutes.layoutManager = LinearLayoutManager(requireContext())
        binding.possibleRouteBottomLayout.recyclerPossibleRoutes.adapter = PossibleRouteAdapter(mutableListOf(), this)
    }

    private fun updatePossibleRouteRecycler(list: List<AvailableTransport>) {
        (binding.possibleRouteBottomLayout.recyclerPossibleRoutes.adapter as PossibleRouteAdapter).updateList(list.toMutableList())
    }

    private fun setRouteDetailRecycler() {
        binding.routeDetailsBottomLayout.routeDetailsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.routeDetailsBottomLayout.routeDetailsRecycler.adapter = RouteDetailsAdapter(mutableListOf())
    }

    private fun updateRouteDetailRecycler(list: List<RouteDetail>) {
        (binding.routeDetailsBottomLayout.routeDetailsRecycler.adapter as RouteDetailsAdapter).updateList(list.toMutableList())
    }

    override fun onPossibleRouteTap(possibleRoute: AvailableTransport, position: Int) {
        // destination Hardcoded TODO change by selected destination when join other features
        isFavorite = if (position == 0) {
            // near from the saved destination
            verifyFavoriteDestination(-16.524715, -68.119393)
        } else {
            // far from the saved destination
            verifyFavoriteDestination(-16.524412, -68.119111)
        }
        if (isFavorite) {
            binding.routeDetailsBottomLayout.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_24)
        } else {
            binding.routeDetailsBottomLayout.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
        mMap?.let {
            homeSelectionStatus = HomeSelectionStatus.SHOWING_ROUTE_DETAILS
//            binding.progressBar.visibility = View.VISIBLE
            val details = mutableListOf<RouteDetail>()
            updateRouteDetailRecycler(details.toList())
            binding.routeDetailsBottomLayout.view.visibility = View.VISIBLE
            binding.possibleRouteBottomLayout.view.visibility = View.GONE
            it.clear()
            val origin = LatLng(-16.52093, -68.1242)
            val destination = LatLng(-16.52476, -68.11937)
            val originName = Geocoder(requireContext()).getFromLocation(origin.latitude, origin.longitude, 1)
            val destinationName = Geocoder(requireContext()).getFromLocation(destination.latitude, destination.longitude, 1)

            binding.routeDetailsBottomLayout.tvDestinationName.text = destinationName.first().thoroughfare
            binding.routeDetailsBottomLayout.tvOriginName.text = originName.first().thoroughfare
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
//                binding.progressBar.visibility = View.GONE
                updateRouteDetailRecycler(details.toList())
            }
            val bounds = builder.build()
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, Constants.POLYLINE_PADDING)
            mMap?.animateCamera(cu)
        }
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

    private fun verifyFavoriteDestination(lat: Double, lng: Double): Boolean {
        var isFavorite = false
        val newDestination = Location(LocationManager.NETWORK_PROVIDER)
        newDestination.latitude = lat
        newDestination.longitude = lng // -16.524412 -68.119111
        val destinations = Location(LocationManager.NETWORK_PROVIDER)
        val list = viewModel.getFavoriteDestinationsByCityAndUserId(requireContext())

        return if (list.isEmpty()) {
            false
        } else {
            for (item in list) {
                destinations.latitude = item.destination.latitude
                destinations.longitude = item.destination.longitude

                val dist = destinations.distanceTo(newDestination)
                isFavorite = dist < 8
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
            viewModel.saveFavoriteDestination(-16.52476, -68.11937, name, requireContext())
            binding.routeDetailsBottomLayout.favoriteButton.setImageResource(R.drawable.ic_baseline_favorite_24)
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
        val message = RoutesAppApplication.resource?.getString(R.string.sure_remove_fav_dest).toString() + " " + name
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
}
