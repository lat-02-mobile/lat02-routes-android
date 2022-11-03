package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.local.room.interfaces.TourPointLocalRepository
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.model.remote.TourPointPath

class FakeTourPointsLocalManager : TourPointLocalRepository {

    override fun getAllTourPointsByCityId(cityId: String): List<TourPointPath> {
        return FakeTourPointData.tourPoints
    }

    override fun getAllLocalTourPointsByCityId(cityId: String): List<TourPointPath> {
        return FakeTourPointData.tourPoints
    }

    override fun addLocalTourPoint(tourPoint: TourPointEntity) {
    }

    override fun addLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity) {
    }

    override fun updateLocalTourPoint(tourPoint: TourPointEntity) {
    }

    override fun updateLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity) {
    }
}
