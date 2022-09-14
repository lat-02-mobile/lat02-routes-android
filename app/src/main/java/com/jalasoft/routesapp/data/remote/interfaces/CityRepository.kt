package com.jalasoft.routesapp.data.remote.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.model.remote.City

interface CityRepository {
    suspend fun getAllCities(): List<City>
    suspend fun getAllTourPoints(context: Context): List<TourPointEntity>
}
