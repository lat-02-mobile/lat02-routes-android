package com.jalasoft.routesapp.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SyncHistoryEntity(
    @PrimaryKey
    val cityId: String,
    val lineCategoryLastUpdated: Long,
    val linesLastUpdated: Long,
    val lineRoutesLastUpdated: Long,
    val TourPointCategoryLastUpdated: Long,
    val TourPointLastUpdated: Long
)
