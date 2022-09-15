package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jalasoft.routesapp.data.model.local.LineRouteEntity

interface LineRouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLineRoute(lineRoute: LineRouteEntity)

    @Query("SELECT * FROM linerouteentity")
    fun getAllLineRoutes(): List<LineRouteEntity>
}
