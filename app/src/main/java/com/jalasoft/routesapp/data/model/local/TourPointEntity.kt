package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TourPointEntity(
    @PrimaryKey
    val id: String,
    val idCity: String,
    val name: String,
    val address: String,
    @Embedded(prefix = "dest")
    val destination: Location,
    val urlImage: String,
    val categoryIcon: String = "",
    val categoryName: String,
    val categoryId: String,
    val createAt: Long,
    val updateAt: Long
)
