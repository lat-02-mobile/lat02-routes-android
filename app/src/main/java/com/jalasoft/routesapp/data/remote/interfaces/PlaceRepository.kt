package com.jalasoft.routesapp.data.remote.interfaces

import com.jalasoft.routesapp.data.api.models.gmaps.Place
import com.jalasoft.routesapp.util.Response

interface PlaceRepository {
    suspend fun getAllPlaces(criteria: String): Response<List<Place>>
}
