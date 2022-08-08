package com.jalasoft.routesapp.ui.auth.registerUser.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.jalasoft.routesapp.R
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.managers.UserManager

class RegisterUserViewModel: ViewModel() {

    fun registerUserAuth(name: String, email: String, password: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        if (validateEmail(email)) {
            UserManager.Singleton.createUserAuth(email, password, { _ ->
                registerUser(name, email, { success ->
                    successListener(success)
                }, { error ->
                    errorListener(error)
                })
            }, { errorMessage ->
                errorListener(errorMessage)
            })
        } else {
            errorListener(R.string.reg_vm_valid_email.toString())
        }
    }

    fun registerUser(name: String, email: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        UserManager.Singleton.createUser(name,email, { userId ->
            successListener(userId)
        }, { errorMessage ->
            errorListener(errorMessage)
        })
    }

    fun validateEmail(email: String) : Boolean {
        var isValid = true
        UserManager.Singleton.validateEmailUser(email, { users ->
            if (users.isNotEmpty()) {
                isValid = false
            }
        }, { error ->
            Log.d(TAG, error)
        })

        return isValid
    }
}