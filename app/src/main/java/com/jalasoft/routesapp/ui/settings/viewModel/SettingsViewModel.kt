package com.jalasoft.routesapp.ui.settings.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.managers.FirebaseManager
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(private val firebaseManager: FirebaseManager, private val auth: FirebaseAuth) : ViewModel() {

    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>(User())
    }

    fun fetchUserDetails() {
        viewModelScope.launch {
            when (val result = firebaseManager.getUsersByParameter(FirebaseCollections.Users, "id", auth.currentUser?.uid ?: "")) {
                is Response.Success -> {
                    user.value = result.data?.firstOrNull()
                }
                is Response.Error -> {
                    errorMessage.value = result.message
                }
            }
        }
    }
}
