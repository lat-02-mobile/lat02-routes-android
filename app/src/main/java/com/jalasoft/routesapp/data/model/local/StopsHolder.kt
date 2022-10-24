package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity(primaryKeys = ["position", "idLineRoute"])
data class StopsHolder(
    val position: Int,
    val idLineRoute: String,
    @Embedded(prefix = "stop")
    var stop: Location
)

data class LineRouteStops(
    @Embedded val lineRoute: LineRouteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "idLineRoute"
    )
    val stops: List<StopsHolder>
)
