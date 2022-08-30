package com.jalasoft.routesapp.data.remote.interfaces

import com.google.firebase.firestore.DocumentReference
import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.CityRoute
import com.jalasoft.routesapp.data.model.remote.Country

interface CountryRepository {
    suspend fun getAllCountries(): List<Country>
    suspend fun getAllCityRoutes(): List<CityRoute>
    suspend fun getAllCities(): List<City>
    suspend fun getCityRoutesByCountry(reference: List<DocumentReference>): List<CityRoute>
}
