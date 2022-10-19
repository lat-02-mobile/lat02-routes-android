package com.jalasoft.routesapp.data.local.room.managers

import com.jalasoft.routesapp.data.local.room.db.RoutesDB
import com.jalasoft.routesapp.data.local.room.interfaces.RouteLocalRepository
import com.jalasoft.routesapp.data.model.remote.*
import java.util.*

class RouteLocalManager(private val localRoutesDB: RoutesDB) : RouteLocalRepository {

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
}
