package com.jalasoft.routesapp.data.remote.managers

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jalasoft.routesapp.data.remote.interfaces.AuthFirebaseDataSource
import com.jalasoft.routesapp.util.Response
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.concurrent.TimeUnit

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

    override suspend fun sendPhoneNumberCode(phoneNumber: String, activity: Activity, mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks): Response<String> {
        return try {
            Firebase.auth.setLanguageCode(Locale.getDefault().language)
            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(phoneNumber)
                .setActivity(activity)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(mCallBack)
                .build()
                val result = PhoneAuthProvider.verifyPhoneNumber(options)
                return Response.Success(result.toString())
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }
    override suspend fun verifyPhoneCode( credential: PhoneAuthCredential, activity: Activity): Response<String> {
        return try {
            var result = ""
            var success = false
            val user = Firebase.auth.currentUser!!
            user.linkWithCredential(credential).addOnCompleteListener(activity){ task ->
                if (task.isSuccessful) {
                    result = task.result.toString()
                    success = true
                } else {
                    result = task.exception.toString()
                    success = false
                }
            }.await()
            if (success) {
                return Response.Success(result)
            }else{
                return Response.Error(result, null)
            }
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
