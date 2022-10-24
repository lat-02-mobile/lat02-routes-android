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
import com.jalasoft.routesapp.data.local.room.interfaces.RouteLocalRepository
import com.jalasoft.routesapp.data.local.room.interfaces.TourPointLocalRepository
import com.jalasoft.routesapp.data.model.local.FavoriteDestinationEntity
import com.jalasoft.routesapp.data.model.local.SyncHistoryEntity
import com.jalasoft.routesapp.data.model.remote.AvailableTransport
import com.jalasoft.routesapp.data.model.remote.LineRoutePath
import com.jalasoft.routesapp.data.remote.interfaces.DirectionsRepository
import com.jalasoft.routesapp.data.remote.interfaces.PlaceRepository
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.data.remote.interfaces.TourPointRepository
import com.jalasoft.routesapp.util.Extensions.toLocation
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.algorithm.RouteCalculator
import com.jalasoft.routesapp.util.helpers.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(private val placeManager: PlaceRepository, private val gDirectionsRepository: DirectionsRepository, private val localDB: LocalDataBaseRepository, private val tourPointsRepository: TourPointRepository, private val tourPointLocalRepository: TourPointLocalRepository, private val routeRepository: RouteRepository, private val routeLocalRepository: RouteLocalRepository) : ViewModel() {
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

    fun getRoutePaths(context: Context, cityId: String): List<LineRoutePath> {
        return routeLocalRepository.getAllLineRoutePaths(context, cityId)
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

    fun checkForUpdatedData(context: Context) = viewModelScope.launch(Dispatchers.IO) {
        val localHistory = localDB.getSyncHistory(context)
        if (localHistory.isNotEmpty()) {
            val history = localHistory.first()
            val resultList = routeRepository.searchForUpdatedLineCategory(history.lineCategoryLastUpdated)
            if (resultList.isNotEmpty()) {
                for (item in resultList) {
                    routeLocalRepository.updateLocalLineCategory(item)
                }
                val updateHistory = SyncHistoryEntity(history.cityId, Date().time, history.linesLastUpdated, history.lineRoutesLastUpdated, history.TourPointCategoryLastUpdated, history.TourPointLastUpdated)
                localDB.updateSyncHistory(updateHistory)
            }
            updateLocalLines(context, history)
        }
    }

    fun updateLocalLines(context: Context, history: SyncHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        val resultList = routeRepository.searchForUpdatedLines(context, history.linesLastUpdated)
        if (resultList.isNotEmpty()) {
            for (item in resultList) {
                routeLocalRepository.updateLocalLines(item)
                updateLocalLineRoutes(item.idLine, history)
            }
            val updateHistory = SyncHistoryEntity(history.cityId, history.lineCategoryLastUpdated, Date().time, history.lineRoutesLastUpdated, history.TourPointCategoryLastUpdated, history.TourPointLastUpdated)
            localDB.updateSyncHistory(updateHistory)
        } else {
            val currentCityId = PreferenceManager.getCurrentCityID(context)
            val localList = routeLocalRepository.getAllLocalLinesByCityId(currentCityId)
            for (item in localList) {
                updateLocalLineRoutes(item.idLine, history)
            }
        }
        updateTourPoints(context, history)
    }

    fun updateLocalLineRoutes(idLine: String, history: SyncHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        val resultList = routeRepository.searchForUpdatedLineRoutes(idLine, history.lineRoutesLastUpdated)
        if (resultList.isNotEmpty()) {
            routeLocalRepository.updateLocalLineRoutes(resultList)
            val updateHistory = SyncHistoryEntity(history.cityId, history.lineCategoryLastUpdated, history.linesLastUpdated, Date().time, history.TourPointCategoryLastUpdated, history.TourPointLastUpdated)
            localDB.updateSyncHistory(updateHistory)
        }
    }

    fun updateTourPointsCategory(history: SyncHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        val resultList = tourPointsRepository.searchForUpdatedTourPointsCategory(history.TourPointCategoryLastUpdated)
        if (resultList.isNotEmpty()) {
            for (item in resultList) {
                tourPointLocalRepository.updateLocalTourPointCategory(item)
            }
            val updateHistory = SyncHistoryEntity(history.cityId, history.lineCategoryLastUpdated, history.linesLastUpdated, history.lineRoutesLastUpdated, Date().time, history.TourPointLastUpdated)
            localDB.updateSyncHistory(updateHistory)
        }
    }

    fun updateTourPoints(context: Context, history: SyncHistoryEntity) = viewModelScope.launch(Dispatchers.IO) {
        val resultList = tourPointsRepository.searchForUpdatedTourPoints(context, history.TourPointLastUpdated)
        if (resultList.isNotEmpty()) {
            for (item in resultList) {
                tourPointLocalRepository.updateLocalTourPoint(item)
            }
            val updateHistory = SyncHistoryEntity(history.cityId, history.lineCategoryLastUpdated, history.linesLastUpdated, history.lineRoutesLastUpdated, history.TourPointCategoryLastUpdated, Date().time)
            localDB.updateSyncHistory(updateHistory)
        }
        updateTourPointsCategory(history)
    }
}
