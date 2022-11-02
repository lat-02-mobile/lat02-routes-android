package com.jalasoft.routesapp.ui.adminPages.routes.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
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
