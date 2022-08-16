package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth

class AuthFirebaseManager(private val auth: FirebaseAuth) {
    fun createUserAuth(email: String, password: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                successListener(task.result.user?.uid.toString())
            } else {
                errorListener(task.exception?.message.toString())
            }
        }
    }

    fun loginUserAuth(email: String, password: String, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                successListener(task.result.user?.email.toString())
            } else {
                errorListener(task.exception?.message.toString())
            }
        }
    }

    fun signInUserAuth(credential: AuthCredential, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                successListener(it.result.user?.uid.toString())
            } else {
                errorListener(it.exception?.message.toString())
            }
        }
    }

    fun singOut() {
        auth.signOut()
    }
}
