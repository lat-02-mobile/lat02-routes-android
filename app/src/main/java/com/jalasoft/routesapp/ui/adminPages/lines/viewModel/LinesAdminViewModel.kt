package com.jalasoft.routesapp.ui.adminPages.lines.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.model.remote.LineAux
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinesAdminViewModel
@Inject
constructor(private val lineRepository: RouteRepository) : ViewModel() {
    private var _lineList: MutableLiveData<List<LineAux>> = MutableLiveData()
    val lineList: LiveData<List<LineAux>> = _lineList
    var originalList: List<LineAux> = listOf()
    var searchQuery = ""

    fun fetchLines() = viewModelScope.launch {
        _lineList.value = lineRepository.getAllLines()
        originalList = _lineList.value ?: listOf()
    }

    fun applyFilterAndSort() {
        if (searchQuery.isEmpty()) return
        var filteredTourPoints = originalList
        filteredTourPoints = filterByQuery(searchQuery, filteredTourPoints)
        _lineList.value = filteredTourPoints
    }

    private fun filterByQuery(query: String, linesList: List<LineAux>): List<LineAux> {
        if (searchQuery.isEmpty()) return linesList
        return linesList.filter { line ->
            val name = line.name ?: return@filter false
            name.lowercase().contains(query.lowercase())
        }
    }
}
