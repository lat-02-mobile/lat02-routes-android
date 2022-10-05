package com.jalasoft.routesapp.data.local.room.interfaces

import com.jalasoft.routesapp.data.model.remote.TourPointPath

interface TourPointLocalRepository {
    fun getAllTourPointsByCityId(cityId: String): List<TourPointPath>
}
