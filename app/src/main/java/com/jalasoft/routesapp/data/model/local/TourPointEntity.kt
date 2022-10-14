package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TourPointEntity(
    val idCity: String,
    @PrimaryKey
    val name: String,
    val address: String,
    @Embedded(prefix = "dest")
    val destination: Location,
    val urlImage: String,
    val categoryName: String,
    val categoryId: String
)
