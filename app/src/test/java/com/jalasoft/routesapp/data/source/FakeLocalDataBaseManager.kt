package com.jalasoft.routesapp.data.source

import android.content.Context
import android.location.Location
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.model.local.*
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

    override fun addLocalFavoriteDestination(
        lat: Double,
        lng: Double,
        name: String,
        context: Context
    ) {
    }

    override fun getFavoriteDestinationByCityAndUserId(context: Context): List<FavoriteDestinationEntity> {
        return arrayListOf()
    }

    override fun deleteFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity) {
    }
}