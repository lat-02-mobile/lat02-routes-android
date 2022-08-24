package com.jalasoft.routesapp.ui.settings.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.remote.managers.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(private val repository: CountryRepository) : ViewModel() {

    companion object {
        const val TAG = "Settings"
    }
    fun fetchCountries() = viewModelScope.launch {
        repository.getAllCountries().forEach { country ->
            Log.d(TAG, country.name)
        }
    }

    fun fetchCities() = viewModelScope.launch {
        repository.getAllCities().forEach { city ->
            Log.d(TAG, "${city.name} - ${city.country}")
        }
    }
}
