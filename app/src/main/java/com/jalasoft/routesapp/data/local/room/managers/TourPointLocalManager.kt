package com.jalasoft.routesapp.data.local.room.managers

import com.jalasoft.routesapp.data.local.room.db.RoutesDB
import com.jalasoft.routesapp.data.local.room.interfaces.TourPointLocalRepository
import com.jalasoft.routesapp.data.model.remote.TourPointPath

class TourPointLocalManager(private val localRoutesDB: RoutesDB) : TourPointLocalRepository {

    override fun getAllTourPointsByCityId(cityId: String): List<TourPointPath> {
        val tourPoints = localRoutesDB.tourPointDao().getAllTourPointsByCityId(cityId)
        return tourPoints.map {
            TourPointPath(it.idCity, it.name, it.address, it.destination.toAndroidLocation(), it.urlImage, it.category, it.categoryIcon)
        }
    }
}
