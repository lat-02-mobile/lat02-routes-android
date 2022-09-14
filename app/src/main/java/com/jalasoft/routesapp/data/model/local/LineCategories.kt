package com.jalasoft.routesapp.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LineCategories(
    @PrimaryKey
    val id: String,
    val nameEng: String,
    val nameEsp: String
)
