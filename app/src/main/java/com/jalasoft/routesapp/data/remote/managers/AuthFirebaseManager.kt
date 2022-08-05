package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthFirebaseManager {
    object Singleton {
        private val auth = Firebase.auth

        fun createUserAuth(email: String, password: String,successListener: (String) -> Unit, errorListener: (String) -> Unit) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    successListener(task.result.user?.uid.toString())
                } else {
                    errorListener(task.exception?.message.toString())
                }

            }
        }
    }

}