package com.jalasoft.routesapp.ui.auth.registerUser.viewModel

import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.managers.FirebaseCollections
import com.jalasoft.routesapp.data.remote.managers.FirebaseManager
import com.jalasoft.routesapp.util.UserType

object RegisterUserViewModel {

    fun registerUser(name: String, email: String, password: String) {
        val userId = FirebaseManager.getDocId(FirebaseCollections.Users)
        val user = User(userId, name, email, password, 0,  UserType.NORMAL.int)

        FirebaseManager.addUserDocument(user, FirebaseCollections.Users, { documentId ->
            println(documentId)
        }, { errorMessage ->
            println(errorMessage)
        })
    }
}