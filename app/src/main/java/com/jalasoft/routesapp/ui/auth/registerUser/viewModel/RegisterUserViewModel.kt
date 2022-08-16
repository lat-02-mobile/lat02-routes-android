package com.jalasoft.routesapp.ui.auth.registerUser.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.interfaces.IUserManager
import com.jalasoft.routesapp.data.remote.managers.UserManager
import com.jalasoft.routesapp.util.helpers.UserTypeLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel
@Inject
constructor(private val userManager: UserManager) : ViewModel(), IUserManager {
    val registerUser: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val signInGoogle: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    var context: Context? = null

    fun verifyRegisterUserAuth(name: String, email: String, password: String, confirmPassword: String) {
        val valid = validateFields(name, email, password, confirmPassword)
        if (valid.isEmpty()) {
            validateEmailNormal(name, email, password)
        } else {
            errorMessage.value = valid
        }
    }

    private fun registerUserAuth(name: String, email: String, password: String, validEmail: Boolean) {
        if (validEmail) {
            userManager.createUserAuth(email, password, { _ ->
                registerUser(name, email, UserTypeLogin.NORMAL, { _ ->
                    registerUser.value = true
                }, { error ->
                    errorMessage.value = error
                })
            }, { error ->
                errorMessage.value = error
            })
        } else {
            errorMessage.value = context?.getString(R.string.reg_vm_valid_email).toString()
        }
    }

    fun registerUser(name: String, email: String, typeLogin: UserTypeLogin, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        userManager.createUser(name, email, typeLogin, { userId ->
            successListener(userId)
        }, { errorMessage ->
            errorListener(errorMessage)
        })
    }

    fun userGoogleAuth(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential, emailValid: Boolean) {
        if (emailValid) {
            registerUserWithGoogle(name, email, typeLogin, { _ ->
                singInWithGoogleCredentials(credential)
            }, { error ->
                errorMessage.value = error
            })
        } else {
            singInWithGoogleCredentials(credential)
        }
    }

    fun registerUserWithGoogle(name: String, email: String, typeLogin: UserTypeLogin, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        userManager.createUser(name, email, typeLogin, { userId ->
            successListener(userId)
        }, { errorMessage ->
            errorListener(errorMessage)
        })
    }

    fun singInWithGoogleCredentials(credential: AuthCredential) {
        userManager.signInWithCredential(credential, {
            signInGoogle.value = true
        }, {
            signInGoogle.value = false
            errorMessage.value = it
        })
    }

    fun validateFields(name: String, email: String, password: String, confirmPassword: String): String {
        var isValid = ""
        if (name.isEmpty()) {
            isValid = context?.getString(R.string.reg_val_name).toString()
            return isValid
        }
        if (email.isEmpty()) {
            isValid = context?.getString(R.string.reg_val_email).toString()
            return isValid
        }
        if (password.isEmpty()) {
            isValid = context?.getString(R.string.reg_val_password).toString()
            return isValid
        }
        if (confirmPassword.isEmpty()) {
            isValid = context?.getString(R.string.reg_val_confirm_password).toString()
            return isValid
        }
        if (password != confirmPassword) {
            isValid = context?.getString(R.string.reg_val_incorrect_passwords).toString()
            return isValid
        }
        return isValid
    }

    fun validateEmailNormal(name: String, email: String, password: String) {
        userManager.validateEmailUser(name, email, password, this)
    }

    fun validateEmailGoogle(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential) {
        userManager.validateEmailUserGoogle(name, email, typeLogin, credential, this)
    }

    override fun validateEmailGoogleResponse(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential, users: MutableList<User>) {
        if (users.isNotEmpty()) {
            userGoogleAuth(name, email, typeLogin, credential, false)
        } else {
            userGoogleAuth(name, email, typeLogin, credential, true)
        }
    }

    override fun validateEmailNormalResponse(name: String, email: String, password: String, users: MutableList<User>) {
        if (users.isNotEmpty()) {
            registerUserAuth(name, email, password, false)
        } else {
            registerUserAuth(name, email, password, true)
        }
    }
}
