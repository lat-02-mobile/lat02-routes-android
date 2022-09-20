package com.jalasoft.routesapp.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.interfaces.UserRepository
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import kotlinx.coroutines.launch

open class AuthBaseViewModel
constructor(val repository: UserRepository) : ViewModel() {
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val signInGoogleOrFacebook: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun userAuthWithCredentials(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential, userData: User?) {
        if (userData == null) {
            registerUserWithGoogleOrFacebook(name, email, typeLogin, credential)
        } else {
            userData.typeLogin?.let {
                if (it == typeLogin.int) {
                    signInWithCredentials(credential)
                } else {
                    errorMessage.value = RoutesAppApplication.resource?.getString(R.string.reg_vm_valid_email).toString()
                }
            }
        }
    }

    private fun registerUserWithGoogleOrFacebook(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential) = viewModelScope.launch {
        when (val resultForCredential = repository.signInWithCredential(credential)) {
            is Response.Success -> {
                val uid = resultForCredential.data
                uid?.let {
                    when (val resultForCreateUser = repository.createUser(uid, name, email, typeLogin)) {
                        is Response.Success -> signInGoogleOrFacebook.value = true
                        is Response.Error -> {
                            signInGoogleOrFacebook.value = false
                            errorMessage.value = resultForCreateUser.message
                        }
                    }
                }
            }
            is Response.Error -> {
                signInGoogleOrFacebook.value = false
                errorMessage.value = resultForCredential.message
            }
        }
    }

    fun signInWithCredentials(credential: AuthCredential) = viewModelScope.launch {
        when (val result = repository.signInWithCredential(credential)) {
            is Response.Success -> signInGoogleOrFacebook.value = true
            is Response.Error -> {
                signInGoogleOrFacebook.value = false
                errorMessage.value = result.message
            }
        }
    }

    fun validateEmailGoogleOrFacebook(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential) = viewModelScope.launch {
        val userData = repository.validateEmailUser(email).data?.firstOrNull()
        userAuthWithCredentials(name, email, typeLogin, credential, userData)
    }
}
