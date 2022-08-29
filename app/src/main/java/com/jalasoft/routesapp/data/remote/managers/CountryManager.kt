package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.firestore.DocumentReference
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.CityRoute
import com.jalasoft.routesapp.data.model.remote.Country
import com.jalasoft.routesapp.data.remote.interfaces.CountryRepository
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import kotlinx.coroutines.tasks.await

class CountryManager(private val firebaseManager: FirebaseManager) : CountryRepository {

    override suspend fun getAllCountries(): List<Country> {
        return firebaseManager.getAllDocuments<Country>(FirebaseCollections.Countries).data
            ?: listOf()
    }

    override suspend fun getAllCityRoutes(): List<CityRoute> {
        return firebaseManager.getAllDocuments<CityRoute>(FirebaseCollections.CityRoute).data
            ?: listOf()
    }

    override suspend fun getAllCities(): List<City> {
        val cities = mutableListOf<City>()
        getAllCountries().forEach { country ->
            val cityRoutes = getCityRoutesByCountry(country.cities)
            for (city in cityRoutes) {
                val newCity = City(city.name, country.name, country.code, country.phone, city.lat, city.lng)
                cities.add(newCity)
            }
        }
        return cities
    }

    override suspend fun getCityRoutesByCountry(reference: List<DocumentReference>): List<CityRoute> {
        val cities = mutableListOf<CityRoute>()
        reference.forEach { city ->
            val cityTarget = city.get().await()
            cityTarget.toObject(CityRoute::class.java)?.let { cities.add(it) }
        }
        return cities
    }
}
