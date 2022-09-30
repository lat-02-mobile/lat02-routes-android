package com.jalasoft.routesapp.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LineCategoriesEntity(
    @PrimaryKey
    val id: String,
    val nameEng: String,
    val nameEsp: String,
    val whiteIcon: String,
    val blackIcon: String
)
