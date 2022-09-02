package com.jalasoft.routesapp.ui.routes.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.model.remote.LinePath
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel
@Inject
constructor(private val repository: RouteRepository) : ViewModel() {

    private var _routesList: MutableLiveData<List<LinePath>> = MutableLiveData()
    val routesList: LiveData<List<LinePath>> = _routesList
    var originalList: List<LinePath> = listOf()

    fun fetchRoutes() = viewModelScope.launch {
        _routesList.value = repository.getAllRouteLines()
        originalList = _routesList.value!!
    }

    fun filterRoutes(criteria: String): Int {
        _routesList.value = originalList.filter { line ->
            line.name.lowercase().contains(criteria.lowercase())
        }
        return _routesList.value!!.size
    }
}
