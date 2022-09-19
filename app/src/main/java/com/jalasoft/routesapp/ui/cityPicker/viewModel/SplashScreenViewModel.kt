package com.jalasoft.routesapp.ui.cityPicker.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.data.remote.interfaces.TourPointRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel
@Inject
constructor(private val linesRepository: RouteRepository, private val tourPointsRepository: TourPointRepository, private val localDB: LocalDataBaseRepository) : ViewModel() {
    var dataSaved: MutableLiveData<Boolean> = MutableLiveData()

    fun getDataAndSafeLocally(context: Context) = viewModelScope.launch {
        val lines = linesRepository.getAllLinesToSaveLocally(context)
        if (lines.isNotEmpty()) {
            for (item in lines) {
                getLineRoutesAndSaveLocally(item.idLine)
                localDB.addLocalLine(item)
            }
        }
        getLinesCategoryAndSaveLocally(context)
    }

    fun getLinesCategoryAndSaveLocally(context: Context) = viewModelScope.launch {
        val linesCategory = linesRepository.getAllLinesCategoryToSaveLocally()
        if (linesCategory.isNotEmpty()) {
            for (item in linesCategory) {
                localDB.addLocalLineCategory(item)
            }
        }
        getTourPointsAndSaveLocally(context)
    }

    fun getLineRoutesAndSaveLocally(idLine: String) = viewModelScope.launch {
        val lineRoute = linesRepository.getAllLinesRouteToSaveLocally(idLine)
        if (lineRoute.isNotEmpty()) {
            localDB.addLocalLineRoute(lineRoute)
        }
    }

    fun getTourPointsAndSaveLocally(context: Context) = viewModelScope.launch {
        val tourPoints = tourPointsRepository.getAllTourPointsToSaveLocally(context)
        if (tourPoints.isNotEmpty()) {
            for (item in tourPoints) {
                localDB.addLocalTourPoint(item)
            }
        }
        getTourPointsCategoryAndSaveLocally()
    }

    fun getTourPointsCategoryAndSaveLocally() = viewModelScope.launch {
        val tourPointsCategory = tourPointsRepository.getAllTourPointsCategoriesToSaveLocally()
        if (tourPointsCategory.isNotEmpty()) {
            for (item in tourPointsCategory) {
                localDB.addLocalTourPointCategory(item)
            }
        }
        dataSaved.value = true
    }
}
