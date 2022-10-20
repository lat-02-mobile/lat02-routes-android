package com.jalasoft.routesapp.data.remote.managers

import com.jalasoft.routesapp.BuildConfig
import com.jalasoft.routesapp.data.api.models.gmaps.Place
import com.jalasoft.routesapp.data.api.retrofit.IGmapsService
import com.jalasoft.routesapp.data.remote.interfaces.PlaceRepository
import com.jalasoft.routesapp.util.Response
import java.lang.Exception

class PlaceManager(val service: IGmapsService) : PlaceRepository {
    override suspend fun getAllPlaces(criteria: String, location: String): Response<List<Place>> {
        try {
            val response = service.getPlaces(criteria, BuildConfig.GOOGLE_MAPS_API_KEY, location)
            if (response.isSuccessful) {
                return Response.Success(response.body()?.results ?: listOf())
            }
        } catch (e: Exception) {
            return Response.Error(message = e.toString())
        }
        return Response.Error(message = "null")
    }
}
