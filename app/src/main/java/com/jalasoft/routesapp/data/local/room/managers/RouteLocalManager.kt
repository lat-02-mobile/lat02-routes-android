package com.jalasoft.routesapp.data.local.room.managers

import android.content.Context
import com.jalasoft.routesapp.data.local.room.db.RoutesDB
import com.jalasoft.routesapp.data.local.room.interfaces.RouteLocalRepository
import com.jalasoft.routesapp.data.model.local.*
import com.jalasoft.routesapp.data.model.remote.*
import java.util.*

class RouteLocalManager(private val localRoutesDB: RoutesDB) : RouteLocalRepository {

    override fun addLocalLine(line: LineEntity) {
        localRoutesDB.lineDao().addLine(line)
    }

    override fun addLocalLineCategory(lineCategory: LineCategoriesEntity) {
        localRoutesDB.lineCategoryDao().addLineCategory(lineCategory)
    }

    override fun addLocalLineRoute(lineRouteInfo: List<LineRouteInfo>) {
        for (item in lineRouteInfo) {
            val start = Location(item.start?.latitude ?: 0.0, item.start?.longitude ?: 0.0)
            val end = Location(item.end?.latitude ?: 0.0, item.end?.longitude ?: 0.0)
            val lineLocal = LineRouteEntity(item.id, item.idLine, item.name, item.averageVelocity, item.color, start, end, item.createAt, item.updateAt)
            localRoutesDB.lineRouteDao().addLineRoute(lineLocal)
            addLocalRoutePoints(item.id, item.routePoints)
            addLocalStops(item.id, item.stops)
        }
    }

    override fun addLocalRoutePoints(idLineRoute: String, routePoints: List<android.location.Location>) {
        for ((position, item) in routePoints.withIndex()) {
            val location = Location(item.latitude, item.longitude)
            val routePointsLocal = RoutePointsHolder(position, idLineRoute, location)
            localRoutesDB.lineRouteDao().addRoutePoints(routePointsLocal)
        }
    }

    override fun addLocalStops(idLineRoute: String, stops: List<android.location.Location>) {
        for ((position, item) in stops.withIndex()) {
            val location = Location(item.latitude, item.longitude)
            val stopsPoints = StopsHolder(position, idLineRoute, location)
            localRoutesDB.lineRouteDao().addStops(stopsPoints)
        }
    }

    override fun getAllLinesByCityId(cityId: String): List<LineInfo> {
        val routePointsStops = localRoutesDB.lineRouteDao().getAllLineRoutePointsStops()
        var lineInfoList = mutableListOf<LineInfo>()
        for (routePointStop in routePointsStops) {
            val lineRoute = routePointStop.lineRoute
            val line = routePointStop.line
            if (line.idCity != cityId) continue
            val routePoints = routePointStop.routePoints.map { it.points.toAndroidLocation() }
            val stops = routePointStop.stops.map { it.stop.toAndroidLocation() }
            val lineRouteInfoList = LineRouteInfo(
                lineRoute.id, lineRoute.name, lineRoute.idLine, routePoints,
                lineRoute.start.toAndroidLocation(), lineRoute.end.toAndroidLocation(),
                stops, lineRoute.color, lineRoute.averageVelocity
            )
            val lineRouteIndex = lineInfoList.indexOfFirst { it.id == line.idLine }
            if (lineRouteIndex > -1) {
                lineInfoList[lineRouteIndex].routePaths.add(lineRouteInfoList)
                continue
            }
            val category = localRoutesDB.lineCategoryDao().getCategoryByName(line.category)
            val currLang = Locale.getDefault().isO3Language
            var categoryName = if (currLang == "spa") category.nameEsp else category.nameEng
            lineInfoList.add(
                LineInfo(line.idLine, line.name, line.enable, categoryName, mutableListOf(lineRouteInfoList), line.createAt, line.updateAt)
            )
        }
        return lineInfoList
    }

    override fun getAllLocalLinesByCityId(cityId: String): List<LineEntity> {
        return localRoutesDB.lineDao().getAllLinesByCityId(cityId)
    }

    override fun getAllLineRoutePaths(context: Context, cityId: String): List<LineRoutePath> {
        val routePointsStops = localRoutesDB.lineRouteDao().getAllLineRoutePointsStops()
        val lineRoutePaths = mutableListOf<LineRoutePath>()
        for (routePointStop in routePointsStops) {
            val lineRoute = routePointStop.lineRoute
            val line = routePointStop.line
            if (cityId != line.idCity) continue
            val routePoints = routePointStop.routePoints.map { it.points.toAndroidLocation() }
            val stops = routePointStop.stops.map { it.stop.toAndroidLocation() }
            val category = localRoutesDB.lineCategoryDao().getCategoryByName(line.category)
            val newLineRoutePath = LineRoutePath(
                line.idLine, line.name, category.nameEsp, lineRoute.name, routePoints,
                lineRoute.start.toAndroidLocation(), lineRoute.end.toAndroidLocation(),
                stops, category.whiteIcon, category.blackIcon, lineRoute.color, lineRoute.averageVelocity
            )
            lineRoutePaths.add(newLineRoutePath)
        }
        return lineRoutePaths
    }

    override fun deleteAllRoutePointsHolder() {
        localRoutesDB.lineRouteDao().deleteAllRoutePointsHolder()
    }

    override fun deleteAllStopsHolder() {
        localRoutesDB.lineRouteDao().deleteAllStopsHolder()
    }

    override fun updateLocalLineCategory(lineCategory: LineCategoriesEntity) {
        localRoutesDB.lineCategoryDao().addLineCategory(lineCategory)
    }

    override fun updateLocalLines(line: LineEntity) {
        localRoutesDB.lineDao().addLine(line)
    }

    override fun updateLocalLineRoutes(lineRouteInfo: List<LineRouteInfo>) {
        for (item in lineRouteInfo) {
            val start = Location(item.start?.latitude ?: 0.0, item.start?.longitude ?: 0.0)
            val end = Location(item.end?.latitude ?: 0.0, item.end?.longitude ?: 0.0)
            val lineLocal = LineRouteEntity(item.id, item.idLine, item.name, item.averageVelocity, item.color, start, end, item.createAt, item.updateAt)
            localRoutesDB.lineRouteDao().addLineRoute(lineLocal)
            updateLocalRoutePoints(item.id, item.routePoints)
            updateLocalStops(item.id, item.stops)
        }
    }

    override fun updateLocalRoutePoints(idLineRoute: String, routePoints: List<android.location.Location>) {
        for ((position, item) in routePoints.withIndex()) {
            val location = Location(item.latitude, item.longitude)
            val routePointsLocal = RoutePointsHolder(position, idLineRoute, location)
            localRoutesDB.lineRouteDao().addRoutePoints(routePointsLocal)
        }
    }

    override fun updateLocalStops(idLineRoute: String, stops: List<android.location.Location>) {
        for ((position, item) in stops.withIndex()) {
            val location = Location(item.latitude, item.longitude)
            val stopsPoints = StopsHolder(position, idLineRoute, location)
            localRoutesDB.lineRouteDao().addStops(stopsPoints)
        }
    }
}
