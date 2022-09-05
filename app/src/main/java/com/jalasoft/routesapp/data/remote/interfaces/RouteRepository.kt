package com.jalasoft.routesapp.data.remote.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.remote.LinePath

interface RouteRepository {
    suspend fun getAllRouteLines(context: Context): List<LinePath>
}
