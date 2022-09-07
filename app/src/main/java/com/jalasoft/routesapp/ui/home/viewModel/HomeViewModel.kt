package com.jalasoft.routesapp.ui.home.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.jalasoft.routesapp.data.api.models.gmaps.Place
import com.jalasoft.routesapp.data.remote.interfaces.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(private val placeManager: PlaceRepository) : ViewModel() {
    val fetchedPlaces: MutableLiveData<List<Place>> by lazy {
        MutableLiveData<List<Place>>(listOf())
    }

    val selectedOrigin: MutableLiveData<LatLng?> by lazy {
        MutableLiveData<LatLng?>(null)
    }

    val selectedDestination: MutableLiveData<LatLng?> by lazy {
        MutableLiveData<LatLng?>(null)
    }

    fun searchPlaces(criteria: String, location: String) = viewModelScope.launch {
        val fetchedPlacesResponse = placeManager.getAllPlaces(criteria, location)
        if (fetchedPlacesResponse.data != null) {
            fetchedPlaces.value = fetchedPlacesResponse.data
        }
    }

    fun setOrigin(location: LatLng?) {
        selectedOrigin.value = location
    }

    fun setDestination(location: LatLng?) {
        selectedDestination.value = location
    }
}
