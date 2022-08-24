package com.jalasoft.routesapp.ui.cityPicker.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.remote.managers.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityPickerViewModel
@Inject
constructor(private val repository: CountryRepository) : ViewModel() {

    var _citiesList: MutableLiveData<List<City>> = MutableLiveData()
    val citiesList: LiveData<List<City>> = _citiesList
    var originalList: List<City> = listOf()

    companion object {
        const val TAG = "Settings"
    }
    fun fetchCountries() = viewModelScope.launch {
        repository.getAllCountries().forEach { country ->
            Log.d(TAG, country.name)
        }
    }

    fun fetchCities() = viewModelScope.launch {
        _citiesList.value = repository.getAllCities()
        originalList = _citiesList.value!!
    }

    fun filterCities(criteria: String): Int {
        _citiesList.value = originalList.filter { city ->
            city.name.lowercase().contains(criteria.lowercase()) || city.country.lowercase().contains(criteria.lowercase())
        }
        return _citiesList.value!!.size
    }
}
