package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.*
import com.jalasoft.routesapp.data.model.local.*

@Dao
interface LineRouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLineRoute(lineRoute: LineRouteEntity)

    @Transaction
    @Query("SELECT * FROM linerouteentity")
    fun getAllLineRoutes(): List<LineRoutePoints>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRoutePoints(routePointsHolder: RoutePointsHolder)

    @Transaction
    @Query("SELECT * FROM linerouteentity")
    fun getAllStopsByLineRoute(): List<LineRouteStops>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addStops(stopsHolder: StopsHolder)

    @Transaction
    @Query("SELECT * FROM linerouteentity")
    fun getAllLineRoutePointsStops(): List<LineRoutePointsStopsLine>

    @Transaction
    @Query("DELETE FROM routepointsholder")
    fun deleteAllRoutePointsHolder()

    @Transaction
    @Query("DELETE FROM stopsholder")
    fun deleteAllStopsHolder()
}
