package com.jalasoft.routesapp.data.remote.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.remote.TourPointPath

interface TourPointRepository {
    suspend fun getAllTourPoints(context: Context): List<TourPointPath>
}
