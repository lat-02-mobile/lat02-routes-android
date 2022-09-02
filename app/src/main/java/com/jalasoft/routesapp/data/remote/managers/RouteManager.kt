package com.jalasoft.routesapp.data.remote.managers

import com.jalasoft.routesapp.data.model.remote.Line
import com.jalasoft.routesapp.data.model.remote.LinePath
import com.jalasoft.routesapp.data.remote.interfaces.RouteRepository
import com.jalasoft.routesapp.util.helpers.FirebaseCollections

class RouteManager(private val firebaseManager: FirebaseManager) : RouteRepository {

    override suspend fun getAllRouteLines(): List<LinePath> {
        val result = firebaseManager.getAllDocuments<Line>(FirebaseCollections.Lines).data
        if (result != null) {
            val linePathList = result.map {
                it.lineToLinePath()
            }
            return linePathList
        }
        return listOf()
    }
}
