package com.jalasoft.routesapp.data.remote.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.model.remote.TourPointPath

interface TourPointRepository {
    suspend fun getAllTourPoints(context: Context): List<TourPointPath>
    suspend fun getAllTourPointsToSaveLocally(context: Context): List<TourPointEntity>
    suspend fun getAllTourPointsCategoriesToSaveLocally(): List<TourPointsCategoryEntity>
}
