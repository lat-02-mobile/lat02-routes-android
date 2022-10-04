package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.local.room.interfaces.TourPointLocalRepository
import com.jalasoft.routesapp.data.model.remote.TourPointPath

class FakeTourPointsLocalManager : TourPointLocalRepository {
    override fun getAllTourPoints(): List<TourPointPath> {
        return FakeTourPointData.tourPoints
    }
}
