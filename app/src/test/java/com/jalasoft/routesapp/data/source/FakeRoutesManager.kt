package com.jalasoft.routesapp.data.source

import android.content.Context
import com.jalasoft.routesapp.data.model.remote.LineInfo
import com.jalasoft.routesapp.data.model.remote.LineRoutePath
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository

class FakeRoutesManager : RouteRepository {
    override suspend fun getAllLines(context: Context): List<LineInfo> {
        return FakeRoutesData.lineInfo
    }

    override suspend fun getLineRouteById(idLine: String): List<LineRoutePath> {
        return FakeRoutesData.line
    }
}
