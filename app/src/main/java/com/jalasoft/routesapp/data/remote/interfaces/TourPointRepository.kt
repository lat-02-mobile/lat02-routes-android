package com.jalasoft.routesapp.data.remote.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.model.local.TourPointEntity

interface TourPointRepository {
    suspend fun getAllTourPointsToSaveLocally(context: Context): List<TourPointEntity>
    suspend fun getAllTourPointsCategoriesToSaveLocally(): List<TourPointsCategoryEntity>
    suspend fun searchForUpdatedTourPoints(context: Context, tourPointLastUpdated: Long): List<TourPointEntity>
    suspend fun searchForUpdatedTourPointsCategory(tourPointCategoryLastUpdated: Long): List<TourPointsCategoryEntity>
}
