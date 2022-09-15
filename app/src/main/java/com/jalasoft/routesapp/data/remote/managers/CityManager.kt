package com.jalasoft.routesapp.data.remote.managers

import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.remote.interfaces.CityRepository
import com.jalasoft.routesapp.util.helpers.FirebaseCollections

class CityManager(private val firebaseManager: FirebaseManager) : CityRepository {

    override suspend fun getAllCities(): List<City> {
        val result = firebaseManager.getAllDocuments<City>(FirebaseCollections.Cities).data
        return result ?: listOf()
    }
}
