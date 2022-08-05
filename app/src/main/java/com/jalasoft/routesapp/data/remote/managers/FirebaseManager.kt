package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import com.jalasoft.routesapp.util.helpers.UserType

class FirebaseManager {
    object Singleton {
        private val db = Firebase.firestore

        fun getDocId(collection: FirebaseCollections): String {
            return db.collection(collection.toString()).document().id
        }

        fun <T : Any> addDocument(document: T, collection: FirebaseCollections, successListener: (String) -> Unit, errorListener: (String) -> Unit) {
            db.collection(collection.toString()).add(document).addOnSuccessListener { documentReference ->
                successListener(documentReference.id)
            }.addOnFailureListener { e ->
                errorListener(e.message.toString())
            }
        }
    }
}