package com.jalasoft.routesapp.ui.settings.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.data.model.remote.LineRoute
import com.jalasoft.routesapp.data.remote.managers.FirebaseManager
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(private val firebaseManager: FirebaseManager) : ViewModel() {

    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val lineRoute: MutableLiveData<LineRoute> by lazy {
        MutableLiveData<LineRoute>()
    }

    fun callRouteDetails() = viewModelScope.launch {
        when (val result = firebaseManager.getDocumentsWithCondition<LineRoute>(FirebaseCollections.LineRoute, "id", "fPzkYkakqvqDsmTNXFqq")) {
            is Response.Success -> {
                lineRoute.value = result.data?.firstOrNull()
            }
            is Response.Error -> {
                errorMessage.value = result.message
            }
        }
    }
}
