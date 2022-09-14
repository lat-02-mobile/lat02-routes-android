package com.jalasoft.routesapp.data.local.room.managers

import com.jalasoft.routesapp.data.local.room.dao.TourPointDao
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository

class LocalDataBaseManager(private val tourPointDao: TourPointDao) : LocalDataBaseRepository {
    override fun addLocalTourPoint(tourPoint: TourPointEntity) {
        tourPointDao.addTourPoint(tourPoint)
    }
}
