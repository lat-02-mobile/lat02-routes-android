package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.*
import com.jalasoft.routesapp.data.model.local.TourPointEntity

@Dao
interface TourPointDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTourPoint(tourPoint: TourPointEntity)

    @Query("SELECT * FROM tourpointentity")
    fun getAllTourPoints(): List<TourPointEntity>

    @Query(
        "SELECT * FROM tourpointentity " +
            "WHERE idCity = :cityId"
    )
    fun getAllTourPointsByCityId(cityId: String): List<TourPointEntity>

    @Update(entity = TourPointEntity::class)
    fun updateTourPoint(tourPointsEntity: TourPointEntity)
}
