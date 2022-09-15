package com.jalasoft.routesapp.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LineEntity(
    @PrimaryKey
    val idLine: String,
    val name: String,
    val idCity: String,
    val category: String,
    val enable: Boolean
)
