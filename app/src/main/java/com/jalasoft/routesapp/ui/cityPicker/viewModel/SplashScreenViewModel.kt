package com.jalasoft.routesapp.ui.cityPicker.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.local.room.interfaces.RouteLocalRepository
import com.jalasoft.routesapp.data.local.room.interfaces.TourPointLocalRepository
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.data.remote.interfaces.TourPointRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel
@Inject
constructor(private val linesRepository: RouteRepository, private val tourPointsRepository: TourPointRepository, private val tourPointLocalRepository: TourPointLocalRepository, private val routeLocalRepository: RouteLocalRepository, private val localDB: LocalDataBaseRepository) : ViewModel() {
    var dataSaved: MutableLiveData<Boolean> = MutableLiveData()

    fun getDataAndSaveLocally(context: Context) = viewModelScope.launch {
        routeLocalRepository.deleteAllRoutePointsHolder()
        routeLocalRepository.deleteAllStopsHolder()
        val lines = linesRepository.getAllLinesToSaveLocally(context)
        if (lines.isNotEmpty()) {
            for (item in lines) {
                getLineRoutesAndSaveLocally(item.idLine)
                routeLocalRepository.addLocalLine(item)
            }
        }
        getLinesCategoryAndSaveLocally(context)
    }

    fun getLinesCategoryAndSaveLocally(context: Context) = viewModelScope.launch {
        val linesCategory = linesRepository.getAllLinesCategoryToSaveLocally()
        if (linesCategory.isNotEmpty()) {
            for (item in linesCategory) {
                routeLocalRepository.addLocalLineCategory(item)
            }
        }
        getTourPointsAndSaveLocally(context)
    }

    fun getLineRoutesAndSaveLocally(idLine: String) = viewModelScope.launch {
        val lineRoute = linesRepository.getAllLinesRouteToSaveLocally(idLine)
        if (lineRoute.isNotEmpty()) {
            routeLocalRepository.addLocalLineRoute(lineRoute)
        }
    }

    fun getTourPointsAndSaveLocally(context: Context) = viewModelScope.launch {
        val tourPoints = tourPointsRepository.getAllTourPointsToSaveLocally(context)
        if (tourPoints.isNotEmpty()) {
            for (item in tourPoints) {
                tourPointLocalRepository.addLocalTourPoint(item)
            }
        }
        getTourPointsCategoryAndSaveLocally(context)
    }

    fun getTourPointsCategoryAndSaveLocally(context: Context) = viewModelScope.launch {
        val tourPointsCategory = tourPointsRepository.getAllTourPointsCategoriesToSaveLocally()
        if (tourPointsCategory.isNotEmpty()) {
            for (item in tourPointsCategory) {
                tourPointLocalRepository.addLocalTourPointCategory(item)
            }
        }
        addSyncHistory(context)
    }

    fun addSyncHistory(context: Context) {
        localDB.addSyncHistory(context)
        dataSaved.value = true
    }
}
