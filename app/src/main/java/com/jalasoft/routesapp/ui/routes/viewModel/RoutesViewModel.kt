package com.jalasoft.routesapp.ui.routes.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.model.remote.LineInfo
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.helpers.FilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel
@Inject
constructor(private val repository: RouteRepository) : ViewModel() {

    private var _routesList: MutableLiveData<List<LineInfo>> = MutableLiveData()
    val routesList: LiveData<List<LineInfo>> = _routesList
    var originalList: List<LineInfo> = listOf()

    fun fetchLines(context: Context) = viewModelScope.launch {
        _routesList.value = repository.getAllLines(context)
        originalList = _routesList.value ?: listOf()
    }

    fun filterLines(criteria: String): Int {
        _routesList.value = originalList.filter { line ->
            line.name.lowercase().contains(criteria.lowercase())
        }
        return _routesList.value?.size ?: 0
    }

    fun filterByCategory(filter: FilterType) {
        when (filter) {
            FilterType.CATEGORY -> {
                val newList = mutableListOf<LineInfo>()
                newList.addAll(_routesList.value ?: listOf())
                newList.sortBy { it.category }
                _routesList.value = newList
            }
            FilterType.ALL -> {
                _routesList.value = originalList
            }
        }
    }
}
