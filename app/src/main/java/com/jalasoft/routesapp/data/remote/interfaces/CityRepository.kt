package com.jalasoft.routesapp.data.remote.interfaces

import com.jalasoft.routesapp.data.model.remote.City

interface CityRepository {
    suspend fun getAllCities(): List<City>
}
