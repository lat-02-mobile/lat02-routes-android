package com.jalasoft.routesapp.ui.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.api.models.gmaps.Place
import com.jalasoft.routesapp.data.remote.managers.PlaceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(private val placeManager: PlaceManager) : ViewModel() {
    val fetchedPlaces: MutableLiveData<List<Place>> by lazy {
        MutableLiveData<List<Place>>(listOf())
    }

    fun searchPlaces(criteria: String) = viewModelScope.launch {
        val fetchedPlacesResponse = placeManager.getAllPlaces(criteria)
        if (fetchedPlacesResponse.data != null) {
            fetchedPlaces.value = fetchedPlacesResponse.data
        }
    }
}
