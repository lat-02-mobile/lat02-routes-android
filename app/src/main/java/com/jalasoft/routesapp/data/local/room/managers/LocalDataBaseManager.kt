package com.jalasoft.routesapp.data.local.room.managers

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.jalasoft.routesapp.data.local.room.db.RoutesDB
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.model.local.*
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.data.model.remote.LineRoutePath
import com.jalasoft.routesapp.util.PreferenceManager
import com.jalasoft.routesapp.util.helpers.DateHelper

class LocalDataBaseManager(private val localRoutesDB: RoutesDB) : LocalDataBaseRepository {
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
        for (item in routePoints) {
            val location = Location(item.latitude, item.longitude)
            val routePointsLocal = RoutePointsHolder(0, idLineRoute, location)
            localRoutesDB.lineRouteDao().addRoutePoints(routePointsLocal)
        }
    }

    override fun addLocalStops(idLineRoute: String, stops: List<android.location.Location>) {
        for (item in stops) {
            val location = Location(item.latitude, item.longitude)
            val stopsPoints = StopsHolder(0, idLineRoute, location)
            localRoutesDB.lineRouteDao().addStops(stopsPoints)
        }
    }

    override fun addLocalTourPoint(tourPoint: TourPointEntity) {
        localRoutesDB.tourPointDao().addTourPoint(tourPoint)
    }

    override fun addLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity) {
        localRoutesDB.tourPointCategoryDao().addTourPointCategory(tourPointCategory)
    }

    override fun addLocalFavoriteDestination(lat: Double, lng: Double, name: String, context: Context) {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: ""
        val destination = Location(lat, lng)
        val dateStr = DateHelper.getCurrentDate()
        val date = DateHelper.convertDateToDouble(dateStr)
        val favoriteDestination = FavoriteDestinationEntity(0, name, destination, currentCityId, userId, date)
        localRoutesDB.favoriteDestinationDao().addFavoriteDestination(favoriteDestination)
    }

    override fun getFavoriteDestinationByCityAndUserId(context: Context): List<FavoriteDestinationEntity> {
        val currentCityId = PreferenceManager.getCurrentCityID(context)
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: ""
        return localRoutesDB.favoriteDestinationDao().getFavoriteDestinationsByCityId(currentCityId, userId)
    }

    override fun editFavoriteDestination(favDest: FavoriteDestinationEntity) {
        localRoutesDB.favoriteDestinationDao().editFavoriteDestination(favDest)
    }

    override fun deleteFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity) {
        localRoutesDB.favoriteDestinationDao().deleteFavoriteDestination(favoriteDestinationEntity)
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
}
