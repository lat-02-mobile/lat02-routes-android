package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jalasoft.routesapp.data.model.remote.User

enum class FirebaseCollections {
    Users
}

object FirebaseManager {
    val db = Firebase.firestore

    fun getDocId(collection: FirebaseCollections): String {
        return db.collection(collection.toString()).document().id
    }

    fun addUserDocument(document: User, collection: FirebaseCollections, succesListener: (String) -> Unit, errorListener: (String) -> Unit) {
        db.collection(collection.toString()).add(document).addOnSuccessListener { documentReference ->
            succesListener(documentReference.id)
        }.addOnFailureListener { e ->
            errorListener(e.toString())
        }
    }
}