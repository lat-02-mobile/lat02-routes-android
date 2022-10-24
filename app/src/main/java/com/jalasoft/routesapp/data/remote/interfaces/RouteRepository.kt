package com.jalasoft.routesapp.data.remote.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo

interface RouteRepository {
    suspend fun getAllLinesToSaveLocally(context: Context): List<LineEntity>
    suspend fun getAllLinesCategoryToSaveLocally(): List<LineCategoriesEntity>
    suspend fun getAllLinesRouteToSaveLocally(idLine: String): List<LineRouteInfo>
    suspend fun searchForUpdatedLineCategory(lineCategoryLastUpdated: Long): List<LineCategoriesEntity>
    suspend fun searchForUpdatedLines(context: Context, linesLastUpdated: Long): List<LineEntity>
    suspend fun searchForUpdatedLineRoutes(idLine: String, lineRoutesLastUpdated: Long): List<LineRouteInfo>
}
