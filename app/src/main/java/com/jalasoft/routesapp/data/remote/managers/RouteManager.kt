package com.jalasoft.routesapp.data.remote.managers

import android.content.Context
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.Line
import com.jalasoft.routesapp.data.model.remote.LineCategories
import com.jalasoft.routesapp.data.model.remote.LinePath
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.FirebaseCollections

class RouteManager(private val firebaseManager: FirebaseManager) : RouteRepository {

    override suspend fun getAllRouteLines(context: Context): List<LinePath> {
        val currentCityId = PreferenceManager.getCurrentCityID(context)

        val result = firebaseManager.getDocumentsWithCondition<Line>(FirebaseCollections.Lines, "idCity", currentCityId).data
        if (result != null) {
            val linePathList = result.map {
                it.lineToLinePath()
            }
            return linePathList
        }
        return listOf()
    }

    override suspend fun getAllLinesToSaveLocally(context: Context): List<LineEntity> {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        val result = firebaseManager.getDocumentsWithCondition<Line>(FirebaseCollections.Lines, "idCity", currentCityId).data
        if (result != null) {
            val lineLocal = result.map {
                it.lineToLineLocal()
            }
            return lineLocal
        }
        return listOf()
    }

    override suspend fun getAllLinesCategoryToSaveLocally(): List<LineCategoriesEntity> {
        val result = firebaseManager.getAllDocuments<LineCategories>(FirebaseCollections.LineCategories).data
        if (result != null) {
            val lineCategories = result.map {
                it.lineCategoriesToLineCategoriesLocal()
            }
            return lineCategories
        }
        return listOf()
    }
}
