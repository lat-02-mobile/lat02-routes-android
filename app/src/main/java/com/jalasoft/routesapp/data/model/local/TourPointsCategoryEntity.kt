package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.jalasoft.routesapp.data.model.remote.TourPointsCategory

@Entity
data class TourPointsCategoryEntity(
    @PrimaryKey
    val id: String,
    val icon: String,
    val descriptionEng: String,
    val descriptionEsp: String
) {
    fun toTourPointCategory(): TourPointsCategory {
        return TourPointsCategory(id, icon, descriptionEng, descriptionEsp)
    }
}

data class TourPointCategoryWithTourPoints(
    @Embedded val tourPointCategory: TourPointsCategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val tourPoints: List<TourPointEntity>
)
