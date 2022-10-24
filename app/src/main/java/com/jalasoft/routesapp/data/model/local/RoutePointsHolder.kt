package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class RoutePointsHolder(
    @PrimaryKey
    val position: Int,
    val idLineRoute: String,
    @Embedded(prefix = "route")
    val points: Location
)

data class LineRoutePoints(
    @Embedded val lineRoute: LineRouteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "idLineRoute"
    )
    val routePoints: List<RoutePointsHolder>
)
