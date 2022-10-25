package com.jalasoft.routesapp.data.source

import android.content.Context
import android.location.Location
import com.jalasoft.routesapp.data.local.room.interfaces.RouteLocalRepository
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.LineInfo
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.data.model.remote.LineRoutePath

class FakeRoutesLocalManager : RouteLocalRepository {
    override fun addLocalLine(line: LineEntity) {
    }

    override fun addLocalLineCategory(lineCategory: LineCategoriesEntity) {
    }

    override fun addLocalLineRoute(lineRouteInfo: List<LineRouteInfo>) {
    }

    override fun addLocalRoutePoints(idLineRoute: String, routePoints: List<Location>) {
    }

    override fun addLocalStops(idLineRoute: String, stops: List<Location>) {
    }

    override fun getAllLinesByCityId(cityId: String): List<LineInfo> {
        return FakeRoutesData.lineInfo
    }

    override fun getAllLocalLinesByCityId(cityId: String): List<LineEntity> {
        return FakeRoutesData.linesEntity
    }

    override fun getAllLineRoutePaths(context: Context, cityId: String): List<LineRoutePath> {
        return listOf()
    }

    override fun deleteAllRoutePointsHolder() {
    }

    override fun deleteAllStopsHolder() {
    }

    override fun updateLocalLineCategory(lineCategory: LineCategoriesEntity) {
    }

    override fun updateLocalLines(line: LineEntity) {
    }

    override fun updateLocalLineRoutes(lineRouteInfo: List<LineRouteInfo>) {
    }

    override fun updateLocalRoutePoints(idLineRoute: String, routePoints: List<Location>) {
    }

    override fun updateLocalStops(idLineRoute: String, stops: List<Location>) {
    }
}
