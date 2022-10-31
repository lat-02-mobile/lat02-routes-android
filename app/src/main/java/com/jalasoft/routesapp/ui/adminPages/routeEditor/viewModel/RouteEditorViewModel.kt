package com.jalasoft.routesapp.ui.adminPages.routeEditor.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteEditorViewModel
@Inject
constructor(private val routeManager: RouteRepository) : ViewModel() {

    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val successSave: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }

    fun saveRouteDetails(routeId: String, routePoints: List<GeoPoint>, stops: List<GeoPoint>) = viewModelScope.launch {
        when (val result = routeManager.updateLineRoutes(routeId, routePoints, stops)) {
            is Response.Success -> {
                successSave.value = Unit
            }
            is Response.Error -> {
                errorMessage.value = result.message
            }
        }
    }
}
