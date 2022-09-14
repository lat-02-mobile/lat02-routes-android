package com.jalasoft.routesapp.data.model.local

import androidx.room.*

@Entity
data class LineRoute(
    @PrimaryKey
    val idLine: String,
    val name: String,
    @Embedded(prefix = "route")
    val routePoints: List<Location>,
    @Embedded(prefix = "start")
    val start: Location,
    @Embedded(prefix = "end")
    val end: Location,
    @Embedded(prefix = "stop")
    val stops: List<Location>
)
