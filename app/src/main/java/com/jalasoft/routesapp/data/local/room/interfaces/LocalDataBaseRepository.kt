package com.jalasoft.routesapp.data.local.room.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.local.*
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo
import com.jalasoft.routesapp.data.model.remote.LineRoutePath

interface LocalDataBaseRepository {
    fun addLocalLine(line: LineEntity)
    fun addLocalLineCategory(lineCategory: LineCategoriesEntity)
    fun addLocalLineRoute(lineRouteInfo: List<LineRouteInfo>)
    fun addLocalRoutePoints(idLineRoute: String, routePoints: List<android.location.Location>)
    fun addLocalStops(idLineRoute: String, stops: List<android.location.Location>)
    fun addLocalTourPoint(tourPoint: TourPointEntity)
    fun addLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity)
    fun addLocalFavoriteDestination(lat: Double, lng: Double, name: String, context: Context)
    fun getFavoriteDestinationByCityAndUserId(context: Context): List<FavoriteDestinationEntity>
    fun deleteFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity)
    fun getAllLineRoutePaths(context: Context, cityId: String): List<LineRoutePath>
    fun deleteAllRoutePointsHolder()
    fun deleteAllStopsHolder()
}
