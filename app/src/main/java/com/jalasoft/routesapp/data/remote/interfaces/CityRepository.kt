package com.jalasoft.routesapp.data.remote.interfaces

import com.jalasoft.routesapp.data.model.remote.City
import com.jalasoft.routesapp.data.model.remote.Country
import com.jalasoft.routesapp.util.Response

interface CityRepository {
    suspend fun getAllCities(): List<City>
    suspend fun getAllCountries(): List<Country>
    suspend fun addNewCity(country: String, idCountry: String, lat: String, lng: String, name: String): Response<String>
    suspend fun updateCity(country: String, id: String, idCountry: String, lat: String, lng: String, name: String): Response<String>
    suspend fun deleteCity(id: String): Response<String>
}
