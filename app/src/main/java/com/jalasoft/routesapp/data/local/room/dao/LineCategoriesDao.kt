package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.*
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity

@Dao
interface LineCategoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addLineCategory(lineCategory: LineCategoriesEntity)

    @Query("SELECT * FROM linecategoriesentity")
    fun getAllLinesCategory(): List<LineCategoriesEntity>

    @Query(
        "SELECT category.id as id, category.nameEng, category.nameEsp, " +
            "category.whiteIcon, category.blackIcon, " +
            "category.createAt, category.updateAt " +
            "FROM linecategoriesentity AS category " +
            "WHERE category.nameEsp = :name OR category.nameEng = :name"
    )
    fun getCategoryByName(name: String): LineCategoriesEntity

    @Update(entity = LineCategoriesEntity::class)
    fun updateLinesCategory(linesCategoryEntity: LineCategoriesEntity)
}
