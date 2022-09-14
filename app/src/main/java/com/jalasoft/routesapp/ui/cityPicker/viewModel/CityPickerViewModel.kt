package com.jalasoft.routesapp.ui.cityPicker.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.remote.interfaces.CityRepository
import com.jalasoft.routesapp.data.local.room.managers.LocalDataBaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityPickerViewModel
@Inject
constructor(private val repository: CityRepository, private val localDB: LocalDataBaseManager) : ViewModel() {

    var _citiesList: MutableLiveData<List<City>> = MutableLiveData()
    val citiesList: LiveData<List<City>> = _citiesList
    var originalList: List<City> = listOf()
    var dataSaved: MutableLiveData<Boolean> = MutableLiveData()

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

    fun getTourPointsLocal(context: Context) = viewModelScope.launch {
        val tourPoints = repository.getAllTourPoints(context)
        if (tourPoints.isNotEmpty()) {
            for (item in tourPoints) {
                localDB.addLocalTourPoint(item)
                if (item == tourPoints.last()) {
                    dataSaved.value = true
                }
            }
        }
    }
}
