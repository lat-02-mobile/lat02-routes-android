package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jalasoft.routesapp.data.model.local.Line

interface LineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLine(line: Line)

    @Query("SELECT * FROM line")
    fun getAllLines(): List<Line>
}
