package com.jalasoft.routesapp.data.local.room.interfaces

import com.jalasoft.routesapp.data.model.local.TourPointEntity

interface LocalDataBaseRepository {
    fun addLocalTourPoint(tourPoint: TourPointEntity)
}
