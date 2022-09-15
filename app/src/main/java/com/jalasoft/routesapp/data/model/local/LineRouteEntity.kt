package com.jalasoft.routesapp.data.model.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LineRouteEntity(
    @PrimaryKey
    val idLine: String,
    val name: String,
    @Embedded(prefix = "route")
    val routePoints: RoutePointsHolder,
    @Embedded(prefix = "start")
    val start: Location,
    @Embedded(prefix = "end")
    val end: Location,
    @Embedded(prefix = "stop")
    val stops: StopsHolder
)

class RoutePointsHolder {
    var routePoints: List<Location>? = null
}

class StopsHolder {
    var stops: List<Location>? = null
}
