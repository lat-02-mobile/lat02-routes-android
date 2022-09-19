package com.jalasoft.routesapp.data.source

import android.content.Context
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.LinePath
import com.jalasoft.routesapp.data.model.remote.LineRoutePath
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository

class FakeRoutesManager : RouteRepository {
    override suspend fun getAllLines(context: Context): List<LinePath> {
        return FakeRoutesData.lines
    }

    override suspend fun getAllLinesToSaveLocally(context: Context): List<LineEntity> {
        return FakeRoutesData.linesEntity
    }

    override suspend fun getAllLinesCategoryToSaveLocally(): List<LineCategoriesEntity> {
        return FakeRoutesData.lineCategoriesEntity
    }

    override suspend fun getAllLinesRouteToSaveLocally(idLine: String): List<LineRoutePath> {
        return FakeRoutesData.lineRoutesPath
    }
}
