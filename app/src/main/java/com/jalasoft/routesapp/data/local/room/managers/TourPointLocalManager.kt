package com.jalasoft.routesapp.data.local.room.managers

import com.jalasoft.routesapp.data.local.room.db.RoutesDB
import com.jalasoft.routesapp.data.local.room.interfaces.TourPointLocalRepository
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.model.remote.TourPointPath

class TourPointLocalManager(private val localRoutesDB: RoutesDB) : TourPointLocalRepository {

    override fun getAllTourPointsByCityId(cityId: String): List<TourPointPath> {
        val tourPointsCategoryWithTourPoints = localRoutesDB.tourPointCategoryDao().getAllTourPointsCategoryWithTourPointsByCityId(cityId)
        var tourPointPaths = mutableListOf<TourPointPath>()
        tourPointsCategoryWithTourPoints.forEach {
            val category = it.tourPointCategory.toTourPointCategory()
            it.tourPoints.forEach { tourPoint ->
                val tourPointPath = TourPointPath(tourPoint.idCity, tourPoint.name, tourPoint.address, tourPoint.destination.toAndroidLocation(), tourPoint.urlImage, category.icon, category, tourPoint.categoryName)
                tourPointPaths.add(tourPointPath)
            }
        }
        return tourPointPaths
    }

    override fun addLocalTourPoint(tourPoint: TourPointEntity) {
        localRoutesDB.tourPointDao().addTourPoint(tourPoint)
    }

    override fun addLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity) {
        localRoutesDB.tourPointCategoryDao().addTourPointCategory(tourPointCategory)
    }

    override fun updateLocalTourPoint(tourPoint: TourPointEntity) {
        localRoutesDB.tourPointDao().updateTourPoint(tourPoint)
    }

    override fun updateLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity) {
        localRoutesDB.tourPointCategoryDao().updateTourPointCategory(tourPointCategory)
    }
}
