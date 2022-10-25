package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(primaryKeys = ["position", "idLineRoute"])
data class RoutePointsHolder(
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
