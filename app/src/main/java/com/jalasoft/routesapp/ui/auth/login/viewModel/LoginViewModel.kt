package com.jalasoft.routesapp.ui.auth.login.viewModel

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.remote.managers.UserRepository
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(private val repository: UserRepository) : ViewModel() {

    val loginIsSuccessful: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val signInGoogleOrFacebook: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun validateFields(email: String, password: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.value = RoutesAppApplication.resource?.getString(R.string.login_email_empty).toString()
        } else if (email.isNotEmpty() && password.isEmpty()) {
            errorMessage.value = RoutesAppApplication.resource?.getString(R.string.login_password_empty).toString()
        } else {
            firebaseLogin(email, password)
        }
    }

    private fun firebaseLogin(email: String, password: String) = viewModelScope.launch {
        val users = repository.signInWithEmailAndPassword(email, password)
        if (users.data?.isNotEmpty() == true) {
            loginIsSuccessful.value = true
            FirebaseAuth.getInstance().currentUser
        } else {
            loginIsSuccessful.value = false
        }
    }

    fun validateEmailGoogleOrFacebook(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential) = viewModelScope.launch {
        val isEmailRegistered = repository.validateEmailUser(email).data?.isEmpty() == true
        userAuthWithCredentials(name, email, typeLogin, credential, isEmailRegistered)
    }

    fun userAuthWithCredentials(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential, emailValid: Boolean) {
        if (emailValid) {
            registerUserToFirebaseFromGoogleOrFacebook(name, email, typeLogin, credential)
        } else {
            singInWithCredentials(credential)
        }
    }

    fun registerUserToFirebaseFromGoogleOrFacebook(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential) = viewModelScope.launch {
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

    fun signOutUser() {
        repository.signOut()
    }
}

@Suppress("UNCHECKED_CAST")
class LoginModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (LoginViewModel(userRepository) as T)
}
