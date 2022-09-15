package com.jalasoft.routesapp.data.remote.interfaces

import android.content.Context
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.remote.LinePath

interface RouteRepository {
    suspend fun getAllRouteLines(context: Context): List<LinePath>
    suspend fun getAllLinesToSaveLocally(context: Context): List<LineEntity>
    suspend fun getAllLinesCategoryToSaveLocally(): List<LineCategoriesEntity>
}
