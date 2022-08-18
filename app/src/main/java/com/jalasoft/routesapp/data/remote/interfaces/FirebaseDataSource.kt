package com.jalasoft.routesapp.data.remote.interfaces

import com.jalasoft.routesapp.data.model.remote.User
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.FirebaseCollections

interface FirebaseDataSource {
    suspend fun getUsersByParameter(collection: FirebaseCollections, field: String, parameter: String): Response<MutableList<User>>
    suspend fun <T : Any> addDocument(document: T, collection: FirebaseCollections): Response<String>
}
