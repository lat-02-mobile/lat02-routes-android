package com.jalasoft.routesapp.data.local.room.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.LineInfo
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.data.model.remote.LineRoutePath

interface RouteLocalRepository {
    fun addLocalLine(line: LineEntity)
    fun addLocalLineCategory(lineCategory: LineCategoriesEntity)
    fun addLocalLineRoute(lineRouteInfo: List<LineRouteInfo>)
    fun addLocalRoutePoints(idLineRoute: String, routePoints: List<android.location.Location>)
    fun addLocalStops(idLineRoute: String, stops: List<android.location.Location>)
    fun getAllLinesByCityId(cityId: String): List<LineInfo>
    fun getAllLocalLinesByCityId(cityId: String): List<LineEntity>
    fun getAllLineRoutePaths(context: Context, cityId: String): List<LineRoutePath>
    fun deleteAllRoutePointsHolder()
    fun deleteAllStopsHolder()
    fun updateLocalLineCategory(lineCategory: LineCategoriesEntity)
    fun updateLocalLines(line: LineEntity)
    fun updateLocalLineRoutes(lineRouteInfo: List<LineRouteInfo>)
    fun updateLocalRoutePoints(idLineRoute: String, routePoints: List<android.location.Location>)
    fun updateLocalStops(idLineRoute: String, stops: List<android.location.Location>)
}
