package com.jalasoft.routesapp.data.local.room.interfaces

import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.model.remote.TourPointPath

interface TourPointLocalRepository {
    fun getAllTourPointsByCityId(cityId: String): List<TourPointPath>
    fun getAllLocalTourPointsByCityId(cityId: String): List<TourPointPath>
    fun addLocalTourPoint(tourPoint: TourPointEntity)
    fun addLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity)
    fun updateLocalTourPoint(tourPoint: TourPointEntity)
    fun updateLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity)
}
