package com.jalasoft.routesapp.data.local.room.interfaces

import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.model.local.TourPointEntity

interface LocalDataBaseRepository {
    fun addLocalLine(line: LineEntity)
    fun addLocalLineCategory(lineCategory: LineCategoriesEntity)
    fun addLocalTourPoint(tourPoint: TourPointEntity)
    fun addLocalTourPointCategory(tourPointCategory: TourPointsCategoryEntity)
}
