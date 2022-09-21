package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class RoutePointsHolder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idLineRoute: String,
    @Embedded(prefix = "route")
    val points: Location
)

data class LineRoutePoints(
    @Embedded val user: LineRouteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "idLineRoute"
    )
    val routePoints: List<RoutePointsHolder>
)
