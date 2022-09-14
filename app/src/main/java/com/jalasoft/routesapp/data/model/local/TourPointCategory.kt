package com.jalasoft.routesapp.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TourPointCategory(
    @PrimaryKey
    val id: String,
    val descriptionEng: String,
    val descriptionEsp: String
)
