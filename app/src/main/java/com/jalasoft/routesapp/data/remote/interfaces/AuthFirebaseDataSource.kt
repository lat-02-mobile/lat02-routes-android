package com.jalasoft.routesapp.data.remote.interfaces

import com.google.firebase.auth.AuthCredential
import com.jalasoft.routesapp.util.Response

interface AuthFirebaseDataSource {
    suspend fun loginUserAuth(email: String, password: String):Response<String>
    suspend fun signInUserAuth(credential: AuthCredential): Response<String>
    suspend fun createUserAuth(email: String, password: String): Response<String>
}
