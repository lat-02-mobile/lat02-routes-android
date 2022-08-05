package com.jalasoft.routesapp.ui.auth.registerUser.viewModel

import androidx.lifecycle.ViewModel
import com.jalasoft.routesapp.data.remote.managers.UserManager

class RegisterUserViewModel: ViewModel() {

    fun registerUserAuth(name: String, email: String, password: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        UserManager.Singleton.createUserAuth(email, password, { _ ->
            registerUser(name, email, { success ->
                successListener(success)
            }, { error ->
                errorListener(error)
            })
        }, { errorMessage ->
            errorListener(errorMessage)
        })
    }

    fun registerUser(name: String, email: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        UserManager.Singleton.createUser(name,email, { userId ->
            successListener(userId)
        }, { errorMessage ->
            errorListener(errorMessage)
        })
    }
}