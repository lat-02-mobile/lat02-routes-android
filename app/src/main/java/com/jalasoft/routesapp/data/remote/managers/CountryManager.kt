package com.jalasoft.routesapp.data.remote.managers

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.CityRoute
import com.jalasoft.routesapp.data.model.remote.Country
import kotlinx.coroutines.tasks.await

class CountryManager: CountryRepository {
    val db = Firebase.firestore

    override suspend fun getAllCountries() : List<Country> {
        val result = db.collection("Countries").get().await()
        val countries = mutableListOf<Country>()
        for (document in result) {
            countries.add(document.toObject(Country::class.java))

        }
        return countries
    }

    override suspend fun getAllCityRoutes(): List<CityRoute> {
        val countries = getAllCountries()
        val cities = mutableListOf<CityRoute>()
        for (country in countries) {
            cities.addAll(getCityRoutesByCountry(country.cities))
        }
        return cities
    }

    override suspend fun getAllCities(): List<City> {
        val cities = mutableListOf<City>()
        val countries = getAllCountries().forEach { country ->
            val cityRoutes = getCityRoutesByCountry(country.cities)
            for (city in cityRoutes) {
                val newCity = City(city.name, country.name, country.code, country.phone)
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