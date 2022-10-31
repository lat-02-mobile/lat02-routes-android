package com.jalasoft.routesapp.ui.adminPages.lines.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.LineAux
import com.jalasoft.routesapp.data.model.remote.LineCategories
import com.jalasoft.routesapp.data.remote.interfaces.CityRepository
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinesAdminViewModel
@Inject
constructor(private val lineRepository: RouteRepository, private val cityRepository: CityRepository) : ViewModel() {
    var _lineList: MutableLiveData<List<LineAux>> = MutableLiveData()
    val lineList: LiveData<List<LineAux>> = _lineList
    var originalList: List<LineAux> = listOf()
    var searchQuery = ""

    private var _lineCategories: MutableLiveData<List<LineCategories>> = MutableLiveData()
    val lineCategories: LiveData<List<LineCategories>> = _lineCategories
    var categorySelected = ""
    private var _cities: MutableLiveData<List<City>> = MutableLiveData()
    val cities: LiveData<List<City>> = _cities
    var citySelected = ""
    var enable = false

    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val successResult: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

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

    fun getLineCategories() = viewModelScope.launch {
        _lineCategories.value = lineRepository.getAllLineCategories()
    }

    fun getCities() = viewModelScope.launch {
        _cities.value = cityRepository.getAllCities()
    }

    fun validateFields(name: String): String {
        var isValid = ""
        if (name.isEmpty()) {
            isValid = RoutesAppApplication.resource?.getString(R.string.reg_val_name).toString()
            return isValid
        }
        return isValid
    }

    fun saveNewLine(name: String) = viewModelScope.launch {
        val valid = validateFields(name)
        if (valid.isEmpty()) {
            when (val result = lineRepository.addNewLine(name, categorySelected, citySelected, enable)) {
                is Response.Success -> {
                    result.data?.let { successResult.value = true }
                }
                is Response.Error -> {
                    errorMessage.value = result.message
                }
            }
        } else {
            errorMessage.value = valid
        }
    }

    fun updateLine(idLine: String, name: String) = viewModelScope.launch {
        val valid = validateFields(name)
        if (valid.isEmpty()) {
            when (val result = lineRepository.updateLine(idLine, name, categorySelected, citySelected, enable)) {
                is Response.Success -> {
                    result.data?.let { successResult.value = true }
                }
                is Response.Error -> {
                    errorMessage.value = result.message
                }
            }
        } else {
            errorMessage.value = valid
        }
    }

    fun deleteLine(idLine: String) = viewModelScope.launch {
        if (idLine.isNotEmpty()) {
            when (val result = lineRepository.deleteLine(idLine)) {
                is Response.Success -> {
                    result.data?.let { successResult.value = true }
                }
                is Response.Error -> {
                    errorMessage.value = result.message
                }
            }
        } else {
            errorMessage.value = RoutesAppApplication.resource?.getString(R.string.unknown_Error).toString()
        }
    }
}
