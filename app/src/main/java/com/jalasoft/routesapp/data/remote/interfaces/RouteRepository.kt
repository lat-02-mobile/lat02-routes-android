package com.jalasoft.routesapp.data.remote.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.LineAux
import com.jalasoft.routesapp.data.model.remote.LineCategories
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.util.Response

interface RouteRepository {
    suspend fun getAllLinesToSaveLocally(context: Context): List<LineEntity>
    suspend fun getAllLinesCategoryToSaveLocally(): List<LineCategoriesEntity>
    suspend fun getAllLinesRouteToSaveLocally(idLine: String): List<LineRouteInfo>
    suspend fun getAllLines(): List<LineAux>
    suspend fun getAllLineCategories(): List<LineCategories>
    suspend fun searchForUpdatedLineCategory(lineCategoryLastUpdated: Long): List<LineCategoriesEntity>
    suspend fun searchForUpdatedLines(cityId: String, linesLastUpdated: Long): List<LineEntity>
    suspend fun searchForUpdatedLineRoutes(idLine: String, lineRoutesLastUpdated: Long): List<LineRouteInfo>
    suspend fun addNewLine(name: String, idCategory: String, idCity: String, enable: Boolean): Response<String>
    suspend fun updateLine(idLine: String, name: String, idCategory: String, idCity: String, enable: Boolean): Response<String>
    suspend fun deleteLine(idLine: String): Response<String>
}
