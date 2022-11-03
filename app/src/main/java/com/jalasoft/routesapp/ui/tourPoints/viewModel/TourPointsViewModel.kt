package com.jalasoft.routesapp.ui.tourPoints.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jalasoft.routesapp.data.local.room.interfaces.TourPointLocalRepository
import com.jalasoft.routesapp.data.model.remote.TourPointPath
import com.jalasoft.routesapp.util.helpers.FilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TourPointsViewModel
@Inject
constructor(private val tourPointLocalDB: TourPointLocalRepository) : ViewModel() {
    var _tourPointsList: MutableLiveData<List<TourPointPath>> = MutableLiveData()
    val tourPoints: LiveData<List<TourPointPath>> = _tourPointsList
    var originalList: List<TourPointPath> = listOf()
    var searchQuery = ""
    var sortQuery: FilterType? = null

    fun fetchTourPoints(cityId: String) {
        val tourPointList = tourPointLocalDB.getAllLocalTourPointsByCityId(cityId)
        _tourPointsList.value = tourPointList
        originalList = tourPointList
    }

    fun applyFilterAndSort() {
        if (searchQuery.isEmpty() && sortQuery == null) return
        var filteredTourPoints = filterByCategory(sortQuery ?: FilterType.ALL, originalList)
        filteredTourPoints = filterByQuery(searchQuery, filteredTourPoints)
        _tourPointsList.value = filteredTourPoints
    }

    private fun filterByQuery(query: String, tourPoints: List<TourPointPath>): List<TourPointPath> {
        if (searchQuery.isEmpty()) return tourPoints
        return tourPoints.filter { tourPoint ->
            val name = tourPoint.name ?: return@filter false
            name.lowercase().contains(query.lowercase())
        }
    }

    private fun filterByCategory(sort: FilterType, tourPoints: List<TourPointPath>): List<TourPointPath> {
        return when (sort) {
            FilterType.CATEGORY -> {
                tourPoints.sortedBy { it.categoryName }
            }
            FilterType.ALL -> {
                tourPoints
            }
        }
    }
}
