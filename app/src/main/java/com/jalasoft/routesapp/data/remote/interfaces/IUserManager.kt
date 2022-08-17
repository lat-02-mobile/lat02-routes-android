package com.jalasoft.routesapp.data.remote.interfaces

import com.google.firebase.auth.AuthCredential
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.util.helpers.UserTypeLogin

interface IUserManager {
    fun validateEmailNormalResponse(name: String, email: String, password: String, users: MutableList<User>)
    fun validateEmailGoogleOrFacebookResponse(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential, users: MutableList<User>)
}
