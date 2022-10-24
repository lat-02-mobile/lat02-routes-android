package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.*
import com.jalasoft.routesapp.data.model.local.LineEntity

@Dao
interface LineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLine(line: LineEntity)

    @Query("SELECT * FROM lineentity")
    fun getAllLines(): List<LineEntity>

    @Query("SELECT * FROM lineentity WHERE idCity = :cityId")
    fun getAllLinesByCityId(cityId: String): List<LineEntity>

    @Update(entity = LineEntity::class)
    fun updateLines(linesEntity: LineEntity)
}
