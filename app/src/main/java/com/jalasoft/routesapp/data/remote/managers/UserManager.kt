package com.jalasoft.routesapp.data.remote.managers

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.interfaces.IUserManager
import com.jalasoft.routesapp.util.helpers.DateHelper
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import com.jalasoft.routesapp.util.helpers.UserType
import com.jalasoft.routesapp.util.helpers.UserTypeLogin

class UserManager(private val authManager: AuthFirebaseManager, private val firebaseManager: FirebaseManager) {
    fun createUserAuth(email: String, password: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        authManager.createUserAuth(email, password, { userUId ->
            successListener(userUId)
        }, { errorMessage ->
            errorListener(errorMessage)
        })
    }

    fun createUser(name: String, email: String, typeLogin: UserTypeLogin, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        val userId = firebaseManager.getDocId(FirebaseCollections.Users)
        val dateStr = DateHelper.getCurrentDate()
        val date = DateHelper.convertDateToDouble(dateStr)
        val user = User(userId, name, email, "", UserType.NORMAL.int, typeLogin.int, date, date)

        firebaseManager.addDocument(user, FirebaseCollections.Users, { documentId ->
            successListener(documentId)
        }, { errorMessage ->
            errorListener(errorMessage)
        })
    }

    fun signInWithCredential(credential: AuthCredential, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        authManager.signInUserAuth(credential, {
            successListener(it)
        }, {
            errorListener(it)
        })
    }

    fun validateEmailUser(name: String, email: String, password: String, listener: IUserManager) {
        firebaseManager.getUsersByParameter(FirebaseCollections.Users, "email", email, { users ->
            listener.validateEmailNormalResponse(name, email, password, users)
        }, { error ->
            Log.d("valEmail", error)
        })
    }

    fun validateEmailUserGoogleOrFacebook(name: String, email: String, typeLogin: UserTypeLogin, credential: AuthCredential, listener: IUserManager) {
        firebaseManager.getUsersByParameter(FirebaseCollections.Users, "email", email, { users ->
            listener.validateEmailGoogleOrFacebookResponse(name, email, typeLogin, credential, users)
        }, { error ->
            Log.d("valEmail", error)
        })
    }

    fun signOutUser() {
        authManager.singOut()
    }
}
