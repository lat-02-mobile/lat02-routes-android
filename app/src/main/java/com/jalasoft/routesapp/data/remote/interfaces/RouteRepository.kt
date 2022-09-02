package com.jalasoft.routesapp.data.remote.interfaces

import com.jalasoft.routesapp.data.model.remote.LinePath

interface RouteRepository {
    suspend fun getAllRouteLines(): List<LinePath>
}
