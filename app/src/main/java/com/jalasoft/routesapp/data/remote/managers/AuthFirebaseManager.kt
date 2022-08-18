package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.jalasoft.routesapp.data.remote.interfaces.AuthFirebaseDataSource
import com.jalasoft.routesapp.util.Response
import kotlinx.coroutines.tasks.await

class AuthFirebaseManager(private val auth: FirebaseAuth) : AuthFirebaseDataSource {
    fun loginUserAuth(email: String, password: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                successListener(task.result.user?.email.toString())
            } else {
                errorListener(task.exception?.message.toString())
            }
        }
    }

    override suspend fun createUserAuth(email: String, password: String): Response<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            return Response.Success(result.toString())
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }

    override suspend fun signInUserAuth(credential: AuthCredential): Response<String> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            return Response.Success(result.user.toString())
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }

    fun singOut() {
        auth.signOut()
    }
}
