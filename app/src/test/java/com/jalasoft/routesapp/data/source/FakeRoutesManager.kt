package com.jalasoft.routesapp.data.source

import android.content.Context
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository

class FakeRoutesManager : RouteRepository {
    override suspend fun getAllLinesToSaveLocally(context: Context): List<LineEntity> {
        return FakeRoutesData.linesEntity
    }

    override suspend fun getAllLinesCategoryToSaveLocally(): List<LineCategoriesEntity> {
        return FakeRoutesData.lineCategoriesEntity
    }

    override suspend fun getAllLinesRouteToSaveLocally(idLine: String): List<LineRouteInfo> {
        return FakeRoutesData.lineRouteInfo
    }
}
