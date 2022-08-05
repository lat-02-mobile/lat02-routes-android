package com.jalasoft.routesapp.data.remote.managers

import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.util.helpers.DateHelper
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import com.jalasoft.routesapp.util.helpers.UserType
import com.jalasoft.routesapp.util.helpers.UserTypeLogin

class UserManager {
    object Singleton {
        fun createUserAuth(email: String, password: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
            AuthFirebaseManager.Singleton.createUserAuth(email,password, { userUId ->
                successListener(userUId)
            }, { errorMessage ->
                errorListener(errorMessage)
            })
        }

        fun createUser(name: String, email: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
            val userId = FirebaseManager.Singleton.getDocId(FirebaseCollections.Users)
            val date = DateHelper.getCurrentDate()
            val user = User(userId, name, email, 0,  UserType.NORMAL.int, UserTypeLogin.NORMAL.int, date, date)

            FirebaseManager.Singleton.addDocument(user, FirebaseCollections.Users, { documentId ->
                successListener(documentId)
            }, { errorMessage ->
                errorListener(errorMessage)
            })
        }
    }
}