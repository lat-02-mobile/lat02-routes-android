package com.jalasoft.routesapp.data.remote.interfaces

import com.jalasoft.routesapp.data.api.models.gmaps.EndLocation
import com.jalasoft.routesapp.data.api.models.gmaps.Route
import com.jalasoft.routesapp.data.api.models.gmaps.StartLocation
import com.jalasoft.routesapp.util.Response

interface DirectionsRepository {
    suspend fun getDirections(startPoint: StartLocation, endPoint: EndLocation): Response<List<Route>>
}
