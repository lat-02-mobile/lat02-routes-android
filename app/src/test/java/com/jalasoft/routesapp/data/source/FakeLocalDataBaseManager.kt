package com.jalasoft.routesapp.data.source

import android.location.Location
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo

class FakeLocalDataBaseManager : LocalDataBaseRepository {
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

    override fun addLocalTourPoint(tourPoint: TourPointEntity) {
    }

    override fun addLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity) {
    }
}
