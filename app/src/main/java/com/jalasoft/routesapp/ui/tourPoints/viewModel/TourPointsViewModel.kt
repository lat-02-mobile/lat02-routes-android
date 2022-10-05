package com.jalasoft.routesapp.ui.tourPoints.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jalasoft.routesapp.data.local.room.interfaces.TourPointLocalRepository
import com.jalasoft.routesapp.data.model.remote.TourPointPath
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TourPointsViewModel
@Inject
constructor(private val tourPointLocalDB: TourPointLocalRepository) : ViewModel() {
    var _tourPointsList: MutableLiveData<List<TourPointPath>> = MutableLiveData()
    val tourPoints: LiveData<List<TourPointPath>> = _tourPointsList
    var originalList: List<TourPointPath> = listOf()

    fun fetchTourPoints(cityId: String) {
        _tourPointsList.value = tourPointLocalDB.getAllTourPointsByCityId(cityId)
        originalList = _tourPointsList.value!!
    }
}
