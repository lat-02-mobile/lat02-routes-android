package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jalasoft.routesapp.data.model.local.LineRoute

interface LineRouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLineRoute(lineRoute: LineRoute)

    @Query("SELECT * FROM lineroute")
    fun getAllLineRoutes(): List<LineRoute>
}
