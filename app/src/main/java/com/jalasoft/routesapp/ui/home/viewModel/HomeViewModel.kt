package com.jalasoft.routesapp.ui.home.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.jalasoft.routesapp.data.api.models.gmaps.EndLocation
import com.jalasoft.routesapp.data.api.models.gmaps.Place
import com.jalasoft.routesapp.data.api.models.gmaps.Route
import com.jalasoft.routesapp.data.api.models.gmaps.StartLocation
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.model.local.FavoriteDestinationEntity
import com.jalasoft.routesapp.data.model.remote.AvailableTransport
import com.jalasoft.routesapp.data.model.remote.LineRoutePath
import com.jalasoft.routesapp.data.remote.interfaces.DirectionsRepository
import com.jalasoft.routesapp.data.remote.interfaces.PlaceRepository
import com.jalasoft.routesapp.util.Extensions.toLocation
import com.jalasoft.routesapp.util.algorithm.RouteCalculator
import com.jalasoft.routesapp.util.helpers.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(private val placeManager: PlaceRepository, private val gDirectionsRepository: DirectionsRepository, private val localDB: LocalDataBaseRepository) : ViewModel() {
    val fetchedPlaces: MutableLiveData<List<Place>> by lazy {
        MutableLiveData<List<Place>>(listOf())
    }

    val selectedOrigin: MutableLiveData<LatLng?> by lazy {
        MutableLiveData<LatLng?>(null)
    }

    val selectedDestination: MutableLiveData<LatLng?> by lazy {
        MutableLiveData<LatLng?>(null)
    }

    private var _possibleRoutesList: MutableLiveData<List<AvailableTransport>> = MutableLiveData()
    val possibleRoutesList: LiveData<List<AvailableTransport>> = _possibleRoutesList

    fun searchPlaces(criteria: String, location: String) = viewModelScope.launch {
        val fetchedPlacesResponse = placeManager.getAllPlaces(criteria, location)
        if (fetchedPlacesResponse.data != null) {
            fetchedPlaces.value = fetchedPlacesResponse.data
        }
    }

    fun setOrigin(location: LatLng?) {
        selectedOrigin.value = location
    }

    fun setDestination(location: LatLng?) {
        selectedDestination.value = location
    }

    fun fetchDirections(startLocation: StartLocation, endLocation: EndLocation): List<Route>? {
        return runBlocking {
            return@runBlocking gDirectionsRepository.getDirections(startLocation, endLocation).data
        }
    }

    fun getPossibleRoutes(cityLinePaths: List<LineRoutePath>, origin: LatLng, destination: LatLng) = viewModelScope.launch {
        val result = RouteCalculator.calculate(cityLinePaths, destination.toLocation(), origin.toLocation(), Constants.MIN_DISTANCE_FROM_ORIGIN_DESTINATION, Constants.MIN_DISTANCE_BETWEEN_STOPS)
        _possibleRoutesList.value = result
    }

    fun getRoutePaths(context: Context): List<LineRoutePath> {
        return localDB.getAllLineRoutePaths(context)
    }

    fun clearPossibleRoutes() {
        _possibleRoutesList.value = listOf()
    }

    fun saveFavoriteDestination(lat: Double, lon: Double, name: String, context: Context) {
        localDB.addLocalFavoriteDestination(lat, lon, name, context)
    }

    fun getFavoriteDestinationsByCityAndUserId(context: Context): List<FavoriteDestinationEntity> {
        return localDB.getFavoriteDestinationByCityAndUserId(context)
    }

    fun deleteFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity) {
        localDB.deleteFavoriteDestination(favoriteDestinationEntity)
    }
}
