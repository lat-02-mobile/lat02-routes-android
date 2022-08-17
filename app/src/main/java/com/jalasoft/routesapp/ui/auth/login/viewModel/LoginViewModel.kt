package com.jalasoft.routesapp.ui.auth.login.viewModel

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.interfaces.IUserManager
import com.jalasoft.routesapp.data.remote.managers.AuthFirebaseManager
import com.jalasoft.routesapp.data.remote.managers.UserManager
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(private val userManager: UserManager, private val auth: AuthFirebaseManager) : ViewModel(), IUserManager {

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
        val valid = validateFields(email, password)
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
        }
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

    fun validateEmailGoogleOrFacebook(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential) {
        userManager.validateEmailUserGoogleOrFacebook(name, email, typeLogin, credential, this)
    }

    override fun validateEmailNormalResponse(name: String, email: String, password: String, users: MutableList<User>) {
        // Nothing to implement
    }

    override fun validateEmailGoogleOrFacebookResponse(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential, users: MutableList<User>) {
        if (users.isNotEmpty()) {
            userAuthWithCredentials(name, email, typeLogin, credential, false)
        } else {
            userAuthWithCredentials(name, email, typeLogin, credential, true)
        }
    }

    private fun userAuthWithCredentials(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential, emailValid: Boolean) {
        if (emailValid) {
            registerUserToFirebaseFromGoogleOrFacebook(name, email, typeLogin, {
                singInWithCredentials(credential)
            }, { error ->
                errorMessage.value = error
            })
        } else {
            singInWithCredentials(credential)
        }
    }

    private fun registerUserToFirebaseFromGoogleOrFacebook(name: String, email: String, typeLogin: UserTypeLogin, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        userManager.createUser(name, email, typeLogin, { userId ->
            successListener(userId)
        }, { errorMessage ->
            errorListener(errorMessage)
        })
    }

    private fun singInWithCredentials(credential: AuthCredential) {
        userManager.signInWithCredential(credential, {
            signInGoogleOrFacebook.value = true
        }, {
            signInGoogleOrFacebook.value = false
            errorMessage.value = it
        })
    }

    fun signOutUser() {
        userManager.signOutUser()
    }
}
