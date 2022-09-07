package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.api.models.gmaps.Place
import com.jalasoft.routesapp.data.remote.interfaces.PlaceRepository
import com.jalasoft.routesapp.util.Response

class FakePlaceManager : PlaceRepository {
    override suspend fun getAllPlaces(criteria: String, location: String): Response<List<Place>> {
        return Response.Success(listOf())
    }
}
