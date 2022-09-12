package com.jalasoft.routesapp.ui.tourPoints.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.model.remote.TourPointPath
import com.jalasoft.routesapp.data.remote.interfaces.TourPointRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TourPointsViewModel
@Inject
constructor(private val repository: TourPointRepository) : ViewModel() {
    var _tourPointsList: MutableLiveData<List<TourPointPath>> = MutableLiveData()
    val tourPoints: LiveData<List<TourPointPath>> = _tourPointsList
    var originalList: List<TourPointPath> = listOf()

    fun fetchTourPoints(context: Context) = viewModelScope.launch {
        _tourPointsList.value = repository.getAllTourPoints(context)
        originalList = _tourPointsList.value!!
    }
}
