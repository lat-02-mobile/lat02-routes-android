package com.jalasoft.routesapp.data.remote.managers

import android.app.Activity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.DateHelper
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import com.jalasoft.routesapp.util.helpers.UserType
import com.jalasoft.routesapp.util.helpers.UserTypeLogin

class UserManager(private val authManager: AuthFirebaseManager, private val firebaseManager: FirebaseManager) : UserRepository {
    override suspend fun validateEmailUser(email: String): Response<MutableList<User>> {
        return firebaseManager.getUsersByParameter(FirebaseCollections.Users, "email", email)
    }

    override suspend fun createUser(name: String, email: String, typeLogin: UserTypeLogin): Response<String> {
        val userId = firebaseManager.getDocId(FirebaseCollections.Users)
        val dateStr = DateHelper.getCurrentDate()
        val date = DateHelper.convertDateToDouble(dateStr)
        val user = User(userId, name, email, "", UserType.NORMAL.int, typeLogin.int, date, date)
        return firebaseManager.addDocument(user, FirebaseCollections.Users)
    }

    override suspend fun createUserAuth(email: String, password: String): Response<String> {
        return authManager.createUserAuth(email, password)
    }

    override suspend fun signInWithCredential(credential: AuthCredential): Response<String> {
        return authManager.signInUserAuth(credential)
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Response<String> {
        return authManager.loginUserAuth(email, password)
    }

    override suspend fun sendPhoneNumberCode(phoneNumber: String, activity: Activity, mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks): Response<String> {
        return authManager.sendPhoneNumberCode(phoneNumber, activity, mCallBack)
    }
    override suspend fun verifyPhoneCode(credential: PhoneAuthCredential, activity: Activity): Response<String> {
        return authManager.verifyPhoneCode(credential, activity)
    }
    override fun signOut() {
        authManager.singOut()
    }
}
