package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.auth.AuthCredential
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.util.helpers.DateHelper
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import com.jalasoft.routesapp.util.helpers.UserType
import com.jalasoft.routesapp.util.helpers.UserTypeLogin

object UserManager {
    fun createUserAuth(email: String, password: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        AuthFirebaseManager.createUserAuth(email, password, { userUId ->
            successListener(userUId)
        }, { errorMessage ->
            errorListener(errorMessage)
        })
    }

    fun createUser(name: String, email: String, typeLogin: UserTypeLogin, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        val userId = FirebaseManager.getDocId(FirebaseCollections.Users)
        val dateStr = DateHelper.getCurrentDate()
        val date = DateHelper.convertDateToDouble(dateStr)
        val user = User(userId, name, email, "", UserType.NORMAL.int, typeLogin.int, date, date)

        FirebaseManager.addDocument(user, FirebaseCollections.Users, { documentId ->
            successListener(documentId)
        }, { errorMessage ->
            errorListener(errorMessage)
        })
    }

    fun signInWithCredential(credential: AuthCredential, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        AuthFirebaseManager.signInUserAuth(credential, {
            successListener(it)
        }, {
            errorListener(it)
        })
    }

    fun validateEmailUser(email: String, successListener: (MutableList<User>) -> Unit, errorListener: (String) -> Unit) {
        FirebaseManager.getUsersByParameter(FirebaseCollections.Users, "email", email, { users ->
            successListener(users)
        }, { error ->
            errorListener(error)
        })
    }

    fun signOutUser() {
        AuthFirebaseManager.singOut()
    }
}
