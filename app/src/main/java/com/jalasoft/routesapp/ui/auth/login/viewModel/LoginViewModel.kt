package com.jalasoft.routesapp.ui.auth.login.viewModel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.jalasoft.routesapp.R
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

    @SuppressLint("StaticFieldLeak")
    var context: Context? = null

    /*fun loginUserAuth(email: String, password: String): Boolean {
        if (validateFields(email, password)) {
            AuthFirebaseManager.loginUserAuth(email, password, {
               // loginUser(email, password)
                // change to true
                loginIsSuccessful = true
            }, { error ->
                loginIs465564Successful = false
                errorMessage.value = error
            })
        }*/

    fun loginUserAuth(email: String, password: String) {
        /*val valid = validateFields(email, password)
        var user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            if (valid.isEmpty()) {
                // if (validateEmail(email)) {
                auth.loginUserAuth(email, password, {
                    loginIsSuccessful.value = true
                    Log.d("sda", "succ")
                    print("success")
                }, {
                    loginIsSuccessful.value = true
                    print("error")
                    Log.d("sda", "${it}estrellita")
                })
                // } else {
                //    loginIsSuccessful.value = false
                //    print("fail")
                // }
            }
        }*/
    }

    private fun showErrorAlert() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Error authenticating user")
        builder.setPositiveButton("Ok", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun validateFields(email: String, password: String): String {
        var isValid = ""
        if (email.isEmpty() && password.isEmpty()) {
            isValid = context?.getString(R.string.reg_val_name).toString()
            errorMessage.value = "Empty Fields"
            showErrorAlert()
            return isValid
        }
        if (email.isEmpty() && password.isNotEmpty()) {
            isValid = context?.getString(R.string.reg_val_email).toString()
            errorMessage.value = "Empty Email"
            showErrorAlert()
            return isValid
        }
        if (email.isNotEmpty() && password.isEmpty()) {
            isValid = context?.getString(R.string.reg_val_password).toString()
            errorMessage.value = "Empty Password"
            showErrorAlert()
            return isValid
        }
        // validateEmail(email)

        return isValid
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
