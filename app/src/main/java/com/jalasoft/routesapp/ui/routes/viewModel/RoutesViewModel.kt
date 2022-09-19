package com.jalasoft.routesapp.ui.routes.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.model.remote.LineInfo
import com.jalasoft.routesapp.data.model.remote.LineRoutePath
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel
@Inject
constructor(private val repository: RouteRepository) : ViewModel() {

    var _routesList: MutableLiveData<List<LineInfo>> = MutableLiveData()
    val routesList: LiveData<List<LineInfo>> = _routesList
    var originalList: List<LineInfo> = listOf()
    var lineRouteList: MutableLiveData<List<LineRoutePath>> = MutableLiveData()

    fun fetchLines(context: Context) = viewModelScope.launch {
        _routesList.value = repository.getAllLines(context)
        originalList = _routesList.value!!
    }

    fun filterLines(criteria: String): Int {
        _routesList.value = originalList.filter { line ->
            line.name.lowercase().contains(criteria.lowercase())
        }
        return _routesList.value!!.size
    }
    fun fetchLineRoute(idLine: String) = viewModelScope.launch {
        lineRouteList.value = repository.getLineRouteById(idLine)
    }

    fun cleanLineRouteList() {
        lineRouteList = MutableLiveData()
    }
}
