package com.jalasoft.routesapp.ui.auth.login.viewModel

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.remote.interfaces.UserRepository
import com.jalasoft.routesapp.ui.auth.AuthBaseViewModel
import com.jalasoft.routesapp.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject constructor(repository: UserRepository) : AuthBaseViewModel(repository) {

    val loginIsSuccessful: MutableLiveData<Boolean> by lazy {
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
        when (val users = repository.signInWithEmailAndPassword(email, password)) {
            is Response.Success -> loginIsSuccessful.value = true
            is Response.Error -> {
                errorMessage.value = users.message
                loginIsSuccessful.value = false
            }
        }
    }
}
