package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LineRouteEntity(
    @PrimaryKey
    val id: String,
    val idLine: String,
    val name: String,
    @Embedded(prefix = "start")
    val start: Location,
    @Embedded(prefix = "end")
    val end: Location
)
