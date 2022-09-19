package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.data.remote.interfaces.FirebaseDataSource
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import kotlinx.coroutines.tasks.await

class FirebaseManager(val db: FirebaseFirestore) : FirebaseDataSource {

    override suspend fun <T : Any> addDocument(documentId: String, document: T, collection: FirebaseCollections): Response<String> {
        return try {
            db.collection(collection.toString()).document(documentId).set(document).await()
            return Response.Success(documentId)
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

    suspend inline fun <reified T : Any> getDocumentsWithCondition(collection: FirebaseCollections, field: String, parameter: String): Response<List<T>> {
        return try {
            val result = db.collection(collection.toString()).whereEqualTo(field, parameter).get().await()
            Response.Success(result.toObjects(T::class.java))
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }
    suspend inline fun <reified T : Any> getDocumentsWithDoubleConditionAndBoolean(collection: FirebaseCollections, field1: String, field2: String, parameter: String, boolean: Boolean): Response<List<T>> {
        return try {
            val result = db.collection(collection.toString())
                .whereEqualTo(field1, parameter)
                .whereEqualTo(field2, boolean)
                .get()
                .await()
            Response.Success(result.toObjects(T::class.java))
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }

    suspend inline fun <reified T : Any> getDocumentsByReferenceWithCondition(collection: FirebaseCollections, field: String, parameter: DocumentReference): Response<List<T>> {
        return try {
            val result = db.collection(collection.toString()).whereEqualTo(field, parameter).get().await()
            Response.Success(result.toObjects(T::class.java))
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }
}
