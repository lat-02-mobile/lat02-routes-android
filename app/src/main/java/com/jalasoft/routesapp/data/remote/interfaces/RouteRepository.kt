package com.jalasoft.routesapp.data.remote.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.remote.LineInfo
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo

interface RouteRepository {
    suspend fun getAllLines(context: Context): List<LineInfo>
    suspend fun getLineRouteById(idLine: String): List<LineRouteInfo>
}
