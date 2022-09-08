package com.jalasoft.routesapp.data.source

import android.content.Context
import com.jalasoft.routesapp.data.model.remote.TourPointPath
import com.jalasoft.routesapp.data.remote.interfaces.TourPointRepository

class FakeTourPointsManager : TourPointRepository {
    override suspend fun getAllTourPoints(context: Context): List<TourPointPath> {
        return FakeTourPointData.tourPoints
    }
}
