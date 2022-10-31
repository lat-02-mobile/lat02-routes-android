package com.jalasoft.routesapp.data.remote.interfaces

import android.content.Context
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.util.Response

interface RouteRepository {
    suspend fun getAllLinesToSaveLocally(context: Context): List<LineEntity>
    suspend fun getAllLinesCategoryToSaveLocally(): List<LineCategoriesEntity>
    suspend fun getAllLinesRouteToSaveLocally(idLine: String): List<LineRouteInfo>
    suspend fun searchForUpdatedLineCategory(lineCategoryLastUpdated: Long): List<LineCategoriesEntity>
    suspend fun searchForUpdatedLines(cityId: String, linesLastUpdated: Long): List<LineEntity>
    suspend fun searchForUpdatedLineRoutes(idLine: String, lineRoutesLastUpdated: Long): List<LineRouteInfo>
    suspend fun updateLineRoutes(routeId: String, routePoints: List<GeoPoint>, routeStops: List<GeoPoint>): Response<Unit>
}
