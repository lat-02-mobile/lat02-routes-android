package com.jalasoft.routesapp.data.remote.managers

import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.Country
import com.jalasoft.routesapp.data.remote.interfaces.CityRepository
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.FirebaseCollections

class CityManager(private val firebaseManager: FirebaseManager) : CityRepository {

    override suspend fun getAllCities(): List<City> {
        val result = firebaseManager.getAllDocuments<City>(FirebaseCollections.Cities).data
        return result ?: listOf()
    }

    override suspend fun getAllCountries(): List<Country> {
        val result = firebaseManager.getAllDocuments<Country>(FirebaseCollections.Countries).data
        return result ?: listOf()
    }

    override suspend fun addNewCity(
        country: String,
        idCountry: String,
        lat: String,
        lng: String,
        name: String
    ): Response<String> {
        val docId = firebaseManager.getDocId(FirebaseCollections.Cities)
        val city = City(country = country, id = docId, idCountry = idCountry, lat, lng, name)
        return firebaseManager.addDocument(docId, city, FirebaseCollections.Cities)
    }

    override suspend fun updateCity(
        country: String,
        id: String,
        idCountry: String,
        lat: String,
        lng: String,
        name: String
    ): Response<String> {
        val city = City(country, id, idCountry, lat, lng, name)
        return firebaseManager.addDocument(id, city, FirebaseCollections.Cities)
    }

    override suspend fun deleteCity(id: String): Response<String> {
        return firebaseManager.deleteDocument(id, FirebaseCollections.Cities)
    }
}
