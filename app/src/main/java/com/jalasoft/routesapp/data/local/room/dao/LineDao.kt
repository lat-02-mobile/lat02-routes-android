package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jalasoft.routesapp.data.model.local.LineEntity

@Dao
interface LineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLine(line: LineEntity)

    @Query("SELECT * FROM lineentity")
    fun getAllLines(): List<LineEntity>
}
