package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.api.models.gmaps.EndLocation
import com.jalasoft.routesapp.data.api.models.gmaps.OverviewPolyline
import com.jalasoft.routesapp.data.api.models.gmaps.Route
import com.jalasoft.routesapp.data.api.models.gmaps.StartLocation
import com.jalasoft.routesapp.data.remote.interfaces.DirectionsRepository
import com.jalasoft.routesapp.util.Response

class FakeDirectionsManager : DirectionsRepository {
    override suspend fun getDirections(
        startPoint: StartLocation,
        endPoint: EndLocation
    ): Response<List<Route>> {
        return Response.Success(
            listOf(
                Route(null, null, null, OverviewPolyline("hnzcBtvx~K@SN[FLT@z@L|@R"), null, null, null)
            )
        )
    }
}
