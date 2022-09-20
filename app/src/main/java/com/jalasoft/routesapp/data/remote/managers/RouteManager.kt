package com.jalasoft.routesapp.data.remote.managers

import android.content.Context
import com.jalasoft.routesapp.data.model.remote.*
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.FirebaseCollections

class RouteManager(private val firebaseManager: FirebaseManager) : RouteRepository {

    override suspend fun getAllLines(context: Context): List<LineInfo> {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        val result = firebaseManager.getDocumentsWithDoubleConditionAndBoolean<Line>(FirebaseCollections.Lines, "idCity", "enable", currentCityId, true).data
        if (result != null) {
            val linePathList = result.map {
                val routePaths = getLineRouteById(it.id)
                return@map it.lineToLineInfo(routePaths)
            }
            return linePathList
        }
        return listOf()
    }
    override suspend fun getLineRouteById(idLine: String): List<LineRouteInfo> {
        val result = firebaseManager.getDocumentsWithCondition<LineRoute>(FirebaseCollections.LineRoute, "idLine", idLine).data
        if (result != null) {
            val lineRouteList = result.map {
                it.lineRouteToLineRouteInfo()
            }
            return lineRouteList
        }
        return listOf()
    }
}
