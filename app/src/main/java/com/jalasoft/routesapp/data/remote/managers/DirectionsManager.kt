package com.jalasoft.routesapp.data.remote.managers

import com.jalasoft.routesapp.BuildConfig
import com.jalasoft.routesapp.data.api.models.gmaps.EndLocation
import com.jalasoft.routesapp.data.api.models.gmaps.Route
import com.jalasoft.routesapp.data.api.models.gmaps.StartLocation
import com.jalasoft.routesapp.data.api.retrofit.IGmapsDirections
import com.jalasoft.routesapp.data.remote.interfaces.DirectionsRepository
import com.jalasoft.routesapp.util.Response
import java.lang.Exception

class DirectionsManager(val service: IGmapsDirections) : DirectionsRepository {
    override suspend fun getDirections(
        startLocation: StartLocation,
        endLocation: EndLocation
    ): Response<List<Route>> {
        try {
            val from = startLocation.lat.toString() + "," + startLocation.lng.toString()
            val to = endLocation.lat.toString() + "," + endLocation.lng.toString()
            val response = service.getDirection(from, to, BuildConfig.GOOGLE_DIRECTIONS_API_KEY)
            if (response.isSuccessful) {
                return Response.Success(response.body()?.routes ?: listOf())
            }
        } catch (e: Exception) {
            return Response.Error(message = e.toString())
        }
        return Response.Error(message = "null")
    }
}
