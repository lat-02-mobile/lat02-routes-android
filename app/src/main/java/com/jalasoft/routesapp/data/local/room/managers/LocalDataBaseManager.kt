package com.jalasoft.routesapp.data.local.room.managers

import com.jalasoft.routesapp.data.local.room.db.RoutesDB
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity

class LocalDataBaseManager(private val localRoutesDB: RoutesDB) : LocalDataBaseRepository {
    override fun addLocalLine(line: LineEntity) {
        localRoutesDB.lineDao().addLine(line)
    }

    override fun addLocalLineCategory(lineCategory: LineCategoriesEntity) {
        localRoutesDB.lineCategoryDao().addLineCategory(lineCategory)
    }

    override fun addLocalTourPoint(tourPoint: TourPointEntity) {
        localRoutesDB.tourPointDao().addTourPoint(tourPoint)
    }

    override fun addLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity) {
        localRoutesDB.tourPointCategoryDao().addTourPointCategory(tourPointCategory)
    }
}
