package com.jalasoft.routesapp.ui.auth.registerUser.viewModel

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.remote.managers.UserManager

class RegisterUserViewModel : ViewModel() {
    val registerUser: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    var context: Context? = null

    fun registerUserAuth(name: String, email: String, password: String, confirmPassword: String) {
        val valid = validateFields(name, email, password, confirmPassword)
        if (valid.isEmpty()) {
            if (validateEmail(email)) {
                UserManager.createUserAuth(email, password, { _ ->
                    registerUser(name, email, { _ ->
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
        } else {
            errorMessage.value = valid
        }
    }

    fun registerUser(name: String, email: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        UserManager.createUser(name, email, { userId ->
            successListener(userId)
        }, { errorMessage ->
            errorListener(errorMessage)
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

    fun validateEmail(email: String): Boolean {
        var isValid = true
        UserManager.validateEmailUser(email, { users ->
            if (users.isNotEmpty()) {
                isValid = false
            }
        }, { error ->
            Log.d(TAG, error)
        })

        return isValid
    }
}
