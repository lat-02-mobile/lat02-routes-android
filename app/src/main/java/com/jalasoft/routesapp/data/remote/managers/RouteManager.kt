package com.jalasoft.routesapp.data.remote.managers

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.*
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.Response
import com.jalasoft.routesapp.util.helpers.DateHelper
import com.jalasoft.routesapp.util.helpers.FirebaseCollections
import kotlinx.coroutines.tasks.await

class RouteManager(private val firebaseManager: FirebaseManager) : RouteRepository {

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

    override suspend fun getAllLinesRouteToSaveLocally(idLine: String): List<LineRouteInfo> {
        val result = firebaseManager.getDocumentsWithCondition<LineRoute>(FirebaseCollections.LineRoute, "idLine", idLine).data
        if (result != null) {
            return result.map {
                it.lineRouteToLineRouteInfo()
            }
        }
        return listOf()
    }

    override suspend fun searchForUpdatedLineCategory(lineCategoryLastUpdated: Long): List<LineCategoriesEntity> {
        val date = DateHelper.fromTimestamp(lineCategoryLastUpdated)
        val lastUpdated = Timestamp(date)
        val result = firebaseManager.getDocumentsWithConditionByDate<LineCategories>(FirebaseCollections.LineCategories, "updateAt", lastUpdated).data
        if (result != null) {
            val lineCategories = result.map {
                it.lineCategoriesToLineCategoriesLocal()
            }
            return lineCategories
        }
        return listOf()
    }

    override suspend fun searchForUpdatedLines(cityId: String, linesLastUpdated: Long): List<LineEntity> {
        val date = DateHelper.fromTimestamp(linesLastUpdated)
        val lastUpdated = Timestamp(date)
        val result = firebaseManager.getDocumentsWithConditionAndByDate<Line>(FirebaseCollections.Lines, "idCity", cityId, "updateAt", lastUpdated).data
        if (result != null) {
            val lineLocal = result.map {
                it.lineToLineLocal()
            }
            return lineLocal
        }
        return listOf()
    }

    override suspend fun searchForUpdatedLineRoutes(idLine: String, lineRoutesLastUpdated: Long): List<LineRouteInfo> {
        val date = DateHelper.fromTimestamp(lineRoutesLastUpdated)
        val lastUpdated = Timestamp(date)
        val result = firebaseManager.getDocumentsWithConditionAndByDate<LineRoute>(FirebaseCollections.LineRoute, "idLine", idLine, "updateAt", lastUpdated).data
        if (result != null) {
            return result.map {
                it.lineRouteToLineRouteInfo()
            }
        }
        return listOf()
    }

    override suspend fun updateLineRoutes(routeId: String, routePoints: List<GeoPoint>, routeStops: List<GeoPoint>): Response<Unit> {
        val ref = firebaseManager.db.collection(FirebaseCollections.LineRoute.toString()).document(routeId)
        val updates = hashMapOf(
            "end" to routeStops.last(),
            "start" to routeStops.first(),
            "routePoints" to routePoints,
            "stops" to routeStops,
            "updateAt" to FieldValue.serverTimestamp()
        )
        return try {
            ref.update(updates).await()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.message.toString(), null)
        }
    }
}
