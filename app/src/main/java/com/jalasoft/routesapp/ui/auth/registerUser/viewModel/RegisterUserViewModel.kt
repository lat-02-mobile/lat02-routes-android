package com.jalasoft.routesapp.ui.auth.registerUser.viewModel

import androidx.lifecycle.*
import com.google.firebase.auth.AuthCredential
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.remote.managers.UserRepository
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel
@Inject
constructor(private val repository: UserRepository) : ViewModel() {
    val registerUser: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val signInGoogleOrFacebook: MutableLiveData<Boolean> by lazy {
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
            val result = repository.createUserAuth(email, password)
            if (result.message?.isNotEmpty() == true) {
                errorMessage.value = result.message
            } else {
                registerUser(name, email, UserTypeLogin.NORMAL)
            }
        } else {
            errorMessage.value = RoutesAppApplication.resource?.getString(R.string.reg_vm_valid_email).toString()
        }
    }

    fun registerUser(name: String, email: String, typeLogin: UserTypeLogin) = viewModelScope.launch {
        val result = repository.createUser(name, email, typeLogin)
        if (result.message?.isNotEmpty() == true) {
            errorMessage.value = result.message
        } else {
            registerUser.value = true
        }
    }

    fun userAuthWithCredentials(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential, emailValid: Boolean) {
        if (emailValid) {
            registerUserWithGoogle(name, email, typeLogin, credential)
        } else {
            singInWithCredentials(credential)
        }
    }

    fun registerUserWithGoogle(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential) = viewModelScope.launch {
        val result = repository.createUser(name, email, typeLogin)
        if (result.data?.isNotEmpty() == true) {
            singInWithCredentials(credential)
        }
    }

    fun singInWithCredentials(credential: AuthCredential) = viewModelScope.launch {
        val result = repository.signInWithCredential(credential)
        if (result.data?.isNotEmpty() == true) {
            signInGoogleOrFacebook.value = true
        } else {
            signInGoogleOrFacebook.value = false
            errorMessage.value = result.message
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

    fun validateEmailGoogleOrFacebook(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential) = viewModelScope.launch {
        val isEmailRegistered = repository.validateEmailUser(email).data?.isEmpty() == true
        userAuthWithCredentials(name, email, typeLogin, credential, isEmailRegistered)
    }
}

@Suppress("UNCHECKED_CAST")
class RegisterUserModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (RegisterUserViewModel(userRepository) as T)
}
