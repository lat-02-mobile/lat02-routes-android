package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class LineRouteEntity(
    @PrimaryKey
    val id: String,
    val idLine: String,
    val name: String,
    val averageVelocity: Double,
    val color: String,
    @Embedded(prefix = "start")
    val start: Location,
    @Embedded(prefix = "end")
    val end: Location
)

data class LineRoutePointsStopsLine(
    @Embedded val lineRoute: LineRouteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "idLineRoute"
    )
    val routePoints: List<RoutePointsHolder>,
    @Relation(
        parentColumn = "id",
        entityColumn = "idLineRoute"
    )
    val stops: List<StopsHolder>,
    @Relation(
        parentColumn = "idLine",
        entityColumn = "idLine"
    )
    val line: LineEntity
)
