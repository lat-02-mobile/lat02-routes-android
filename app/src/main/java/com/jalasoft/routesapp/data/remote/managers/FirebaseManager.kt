package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.firestore.FirebaseFirestore
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.interfaces.FirebaseDataSource
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import kotlinx.coroutines.tasks.await

class FirebaseManager(val db: FirebaseFirestore) : FirebaseDataSource {
    fun getDocId(collection: FirebaseCollections): String {
        return db.collection(collection.toString()).document().id
    }

    override suspend fun <T : Any> addDocument(document: T, collection: FirebaseCollections): Response<String> {
        return try {
            val result = db.collection(collection.toString()).add(document).await()
            return Response.Success(result.id)
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }

    override suspend fun getUsersByParameter(collection: FirebaseCollections, field: String, parameter: String): Response<MutableList<User>> {
        return try {
            val list = db.collection(collection.toString()).whereEqualTo(field, parameter).get().await()
            return Response.Success(list.toObjects(User::class.java))
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }

    suspend inline fun <reified T : Any> getAllDocuments(collection: FirebaseCollections): Response<List<T>> {
        return try {
            val result = db.collection(collection.toString()).get().await()
            Response.Success(result.toObjects(T::class.java))
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }
}
