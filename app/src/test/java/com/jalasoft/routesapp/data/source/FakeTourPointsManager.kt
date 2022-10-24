package com.jalasoft.routesapp.data.source

import android.content.Context
import com.jalasoft.routesapp.data.model.local.TourPointEntity
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.remote.interfaces.TourPointRepository

class FakeTourPointsManager : TourPointRepository {

    override suspend fun getAllTourPointsToSaveLocally(context: Context): List<TourPointEntity> {
        return FakeTourPointData.tourPointsEntity
    }

    override suspend fun getAllTourPointsCategoriesToSaveLocally(): List<TourPointsCategoryEntity> {
        return FakeTourPointData.tourPointsCategoryEntity
    }

    override suspend fun searchForUpdatedTourPoints(context: Context, tourPointLastUpdated: Long): List<TourPointEntity> {
        return FakeTourPointData.tourPointsEntity
    }

    override suspend fun searchForUpdatedTourPointsCategory(tourPointCategoryLastUpdated: Long): List<TourPointsCategoryEntity> {
        return FakeTourPointData.tourPointsCategoryEntity
    }
}
