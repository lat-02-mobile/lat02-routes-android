package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteDestinationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var name: String,
    @Embedded(prefix = "dest")
    val destination: Location,
    val cityId: String,
    val userId: String,
    val createdAt: Double
)
