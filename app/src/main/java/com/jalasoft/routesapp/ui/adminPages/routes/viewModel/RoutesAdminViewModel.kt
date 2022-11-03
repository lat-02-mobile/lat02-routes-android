package com.jalasoft.routesapp.ui.adminPages.routes.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.model.local.LineRouteAux
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutesAdminViewModel
@Inject
constructor(private val routeRepository: RouteRepository) : ViewModel() {
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val successFetching: MutableLiveData<List<LineRouteInfo>> by lazy {
        MutableLiveData<List<LineRouteInfo>>()
    }

    val successSave: MutableLiveData<Unit> by lazy {
        MutableLiveData<Unit>()
    }

    fun saveRoute(routeInfo: LineRouteAux, isNew: Boolean) = viewModelScope.launch {
        when (val response = if (isNew) routeRepository.createRouteForLine(routeInfo) else routeRepository.updateRouteInfo(routeInfo)) {
            is Response.Success -> {
                successSave.value = Unit
            }
            is Response.Error -> {
                errorMessage.value = response.message
            }
        }
    }

    fun getAllRoutesForLine(idLine: String) {
        routeRepository.getAllRoutesForLine(idLine) { response ->
            when (response) {
                is Response.Success -> {
                    successFetching.value = response.data
                }
                is Response.Error -> {
                    errorMessage.value = response.message
                }
            }
        }
    }
}
