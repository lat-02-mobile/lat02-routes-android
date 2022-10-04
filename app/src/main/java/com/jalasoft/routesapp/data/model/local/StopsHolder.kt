package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class StopsHolder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
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
