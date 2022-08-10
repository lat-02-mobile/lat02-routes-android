package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.util.helpers.FirebaseCollections

object FirebaseManager {
    private val db = Firebase.firestore

    fun getDocId(collection: FirebaseCollections): String {
        return db.collection(collection.toString()).document().id
    }

    fun <T : Any> addDocument(document: T, collection: FirebaseCollections, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
        db.collection(collection.toString()).add(document).addOnSuccessListener { documentReference ->
            successListener(documentReference.id)
        }.addOnFailureListener { exception ->
            errorListener(exception.message.toString())
        }
    }

    fun getUsersByParameter(collection: FirebaseCollections, field: String, parameter: String, successListener: (MutableList<User>) -> Unit, errorListener: (String) -> Unit) {
        db.collection(collection.toString()).whereEqualTo(field, parameter).get().addOnSuccessListener { documents ->
            var users = documents.toObjects(User::class.java)
            successListener(users)
        }.addOnFailureListener { exception ->
            errorListener(exception.message.toString())
        }
    }
}
