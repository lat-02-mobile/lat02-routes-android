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

    @Query(
        "SELECT category.id as id, category.nameEng, category.nameEsp, " +
            "category.whiteIcon, category.blackIcon " +
            "FROM linecategoriesentity AS category " +
            "WHERE category.nameEsp = :name OR category.nameEng = :name"
    )
    fun getCategoryByName(name: String): LineCategoriesEntity
}
