package com.jalasoft.routesapp.ui.adminPages.cities.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.Country
import com.jalasoft.routesapp.data.remote.interfaces.CityRepository
import com.jalasoft.routesapp.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityAdminViewModel
@Inject
constructor(private val cityRepository: CityRepository) : ViewModel() {
    var _cityList: MutableLiveData<List<City>> = MutableLiveData()
    val cityList: LiveData<List<City>> = _cityList
    var originalList: List<City> = listOf()
    var searchQuery = ""

    private var _cityCountry: MutableLiveData<List<Country>> = MutableLiveData()
    val cityCountry: LiveData<List<Country>> = _cityCountry // val lineCategories: LiveData<List<LineCategories>> = _lineCategories
    var countrySelected = ""
    var countryID = ""
    var country = ""
    var lat = ""
    var lng = ""

    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val successResult: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun fetchCities() = viewModelScope.launch {
        _cityList.value = cityRepository.getAllCities()
        originalList = _cityList.value ?: listOf()
    }

    fun applyFilterAndSort() {
        if (searchQuery.isEmpty()) return
        var filteredCities = originalList
        filteredCities = filterByQuery(searchQuery, filteredCities)
        _cityList.value = filteredCities
    }

    private fun filterByQuery(query: String, cityList: List<City>): List<City> {
        if (searchQuery.isEmpty()) return cityList
        return cityList.filter { city ->
            val name = city.name ?: return@filter false
            name.lowercase().contains(query.lowercase())
        }
    }

    fun getCityCountry() = viewModelScope.launch {
        _cityCountry.value = cityRepository.getAllCountries()
    }

    fun validateFields(name: String): String {
        var isValid = ""
        if (name.isEmpty()) {
            isValid = RoutesAppApplication.resource?.getString(R.string.reg_val_name).toString()
            return isValid
        }
        return isValid
    }

    fun saveNewCity(name: String,latitude: String, longitude: String, countrySelected: String,countryId:String) = viewModelScope.launch {
        val valid = validateFields(name)
       if (valid.isEmpty()) {
            when (val result = cityRepository.addNewCity(countrySelected, countryId, latitude, longitude, name)) {
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

    fun updateCity(name: String,latitude: String, longitude: String, countrySelected: String,countryId:String,city: City) = viewModelScope.launch {
        val valid = validateFields(name)
        if (valid.isEmpty()) {
            when (val result = cityRepository.updateCity(countrySelected, city.id,countryId, latitude, longitude, name)) {
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

    fun deleteCity(id: String) = viewModelScope.launch {
        if (id.isNotEmpty()) {
            when (val result = cityRepository.deleteCity(id)) {
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
