package com.jalasoft.routesapp.ui.adminPages.routeEditor.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jalasoft.routesapp.data.remote.managers.FirebaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RouteEditorViewModel
@Inject
constructor(private val firebaseManager: FirebaseManager) : ViewModel() {

    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}
