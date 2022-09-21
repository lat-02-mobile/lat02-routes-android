package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity

@Dao
interface LineCategoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLineCategory(lineCategory: LineCategoriesEntity)

    @Query("SELECT * FROM linecategoriesentity")
    fun getAllLinesCategory(): List<LineCategoriesEntity>
}
