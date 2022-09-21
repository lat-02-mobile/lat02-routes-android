package com.jalasoft.routesapp.data.local.room.interfaces

import com.jalasoft.routesapp.data.model.local.*
import com.jalasoft.routesapp.data.model.remote.LineRouteInfo

interface LocalDataBaseRepository {
    fun addLocalLine(line: LineEntity)
    fun addLocalLineCategory(lineCategory: LineCategoriesEntity)
    fun addLocalLineRoute(lineRouteInfo: List<LineRouteInfo>)
    fun addLocalRoutePoints(idLineRoute: String, routePoints: List<android.location.Location>)
    fun addLocalStops(idLineRoute: String, stops: List<android.location.Location>)
    fun addLocalTourPoint(tourPoint: TourPointEntity)
    fun addLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity)
    fun addLocalFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity)
}
