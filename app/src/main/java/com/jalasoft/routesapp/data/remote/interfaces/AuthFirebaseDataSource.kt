package com.jalasoft.routesapp.data.remote.interfaces

import android.app.Activity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.jalasoft.routesapp.util.Response

interface AuthFirebaseDataSource {
    suspend fun loginUserAuth(email: String, password: String): Response<String>
    suspend fun signInUserAuth(credential: AuthCredential): Response<String>
    suspend fun createUserAuth(email: String, password: String): Response<String>
    suspend fun sendPhoneNumberCode(phoneNumber: String, activity: Activity, mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks): Response<String>
    suspend fun verifyPhoneCode(credential: PhoneAuthCredential, activity: Activity): Response<String>
}
