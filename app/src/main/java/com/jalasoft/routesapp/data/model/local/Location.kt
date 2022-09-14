package com.jalasoft.routesapp.data.model.local

import androidx.room.Entity

@Entity
data class Location(
    var latitude: Double,
    var longitude: Double
)
