package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.auth.AuthCredential
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.UserTypeLogin

interface UserRepository {
    suspend fun validateEmailUser(email: String): Response<MutableList<User>>
    suspend fun createUser(name: String, email: String, typeLogin: UserTypeLogin): Response<String>
    suspend fun createUserAuth(email: String, password: String): Response<String>
    suspend fun signInWithCredential(credential: AuthCredential): Response<String>
    fun signOut()
}
