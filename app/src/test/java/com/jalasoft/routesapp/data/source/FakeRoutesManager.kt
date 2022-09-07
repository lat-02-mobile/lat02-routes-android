package com.jalasoft.routesapp.data.source

import android.content.Context
import com.jalasoft.routesapp.data.model.remote.LinePath
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository

class FakeRoutesManager : RouteRepository {
    override suspend fun getAllRouteLines(context: Context): List<LinePath> {
        return FakeRoutesData.lines
    }
}
