package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.jalasoft.routesapp.data.remote.interfaces.AuthFirebaseDataSource
import com.jalasoft.routesapp.util.Response
import kotlinx.coroutines.tasks.await

class AuthFirebaseManager(private val auth: FirebaseAuth) : AuthFirebaseDataSource {

    // MARK: Temporary solution for signOut user
    companion object {
        fun signOutUser() {
            val auth = FirebaseAuth.getInstance()
            auth.signOut()
        }
    }

    override suspend fun loginUserAuth(email: String, password: String): Response<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            return Response.Success(result.toString())
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
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
