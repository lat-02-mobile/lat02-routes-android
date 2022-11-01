package com.jalasoft.routesapp.data.source

import android.content.Context
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.LineAux
import com.jalasoft.routesapp.data.model.remote.LineCategories
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.Response

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

    override suspend fun getAllLines(): List<LineAux> {
        return FakeRoutesData.lineAuxList
    }

    override suspend fun getAllLineCategories(): List<LineCategories> {
        return FakeRoutesData.lineCategoriesList
    }

    override suspend fun searchForUpdatedLineCategory(lineCategoryLastUpdated: Long): List<LineCategoriesEntity> {
        return FakeRoutesData.lineCategoriesEntity
    }

    override suspend fun searchForUpdatedLines(cityId: String, linesLastUpdated: Long): List<LineEntity> {
        return FakeRoutesData.linesEntity
    }

    override suspend fun searchForUpdatedLineRoutes(idLine: String, lineRoutesLastUpdated: Long): List<LineRouteInfo> {
        return FakeRoutesData.lineRouteInfo
    }

    override suspend fun addNewLine(name: String, idCategory: String, idCity: String, enable: Boolean): Response<String> {
        return Response.Success(name)
    }

    override suspend fun updateLine(idLine: String, name: String, idCategory: String, idCity: String, enable: Boolean): Response<String> {
        return Response.Success(idLine)
    }

    override suspend fun deleteLine(idLine: String): Response<String> {
        return Response.Success(idLine)
    }

    override suspend fun updateLineRoutes(routeId: String, routePoints: List<GeoPoint>, routeStops: List<GeoPoint>): Response<Unit> {
        return Response.Success(Unit)
    }
}
