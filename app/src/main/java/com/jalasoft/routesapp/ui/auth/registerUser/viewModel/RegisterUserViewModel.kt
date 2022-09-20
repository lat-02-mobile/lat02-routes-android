package com.jalasoft.routesapp.ui.auth.registerUser.viewModel

import androidx.lifecycle.*
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.remote.interfaces.UserRepository
import com.jalasoft.routesapp.ui.auth.AuthBaseViewModel
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel
@Inject
constructor(repository: UserRepository) : AuthBaseViewModel(repository) {
    val registerUser: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun verifyRegisterUserAuth(name: String, email: String, password: String, confirmPassword: String) {
        val valid = validateFields(name, email, password, confirmPassword)
        if (valid.isEmpty()) {
            validateEmailNormal(name, email, password)
        } else {
            errorMessage.value = valid
        }
    }

    fun registerUserAuth(name: String, email: String, password: String, validEmail: Boolean) = viewModelScope.launch {
        if (validEmail) {
            when (val result = repository.createUserAuth(email, password)) {
                is Response.Success -> {
                    result.data?.let { registerUser(it, name, email, UserTypeLogin.NORMAL) }
                }
                is Response.Error -> {
                    errorMessage.value = result.message
                }
            }
        } else {
            errorMessage.value = RoutesAppApplication.resource?.getString(R.string.reg_vm_valid_email).toString()
        }
    }

    fun registerUser(uid: String, name: String, email: String, typeLogin: UserTypeLogin) = viewModelScope.launch {
        when (val result = repository.createUser(uid, name, email, typeLogin)) {
            is Response.Success -> {
                result.data?.let { registerUser.value = true }
            }
            is Response.Error -> {
                errorMessage.value = result.message
            }
        }
    }

    fun validateFields(name: String, email: String, password: String, confirmPassword: String): String {
        var isValid = ""
        if (name.isEmpty()) {
            isValid = RoutesAppApplication.resource?.getString(R.string.reg_val_name).toString()
            return isValid
        }
        if (email.isEmpty()) {
            isValid = RoutesAppApplication.resource?.getString(R.string.reg_val_email).toString()
            return isValid
        }
        if (password.isEmpty()) {
            isValid = RoutesAppApplication.resource?.getString(R.string.reg_val_password).toString()
            return isValid
        }
        if (confirmPassword.isEmpty()) {
            isValid = RoutesAppApplication.resource?.getString(R.string.reg_val_confirm_password).toString()
            return isValid
        }
        if (password != confirmPassword) {
            isValid = RoutesAppApplication.resource?.getString(R.string.reg_val_incorrect_passwords).toString()
            return isValid
        }
        return isValid
    }

    fun validateEmailNormal(name: String, email: String, password: String) = viewModelScope.launch {
        val isEmailRegistered = repository.validateEmailUser(email).data?.isEmpty() == true
        registerUserAuth(name, email, password, isEmailRegistered)
    }
}
