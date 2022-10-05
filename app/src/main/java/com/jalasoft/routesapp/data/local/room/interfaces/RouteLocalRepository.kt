package com.jalasoft.routesapp.data.local.room.interfaces

import com.jalasoft.routesapp.data.model.remote.LineInfo

interface RouteLocalRepository {
    fun getAllLinesByCityId(cityId: String): List<LineInfo>
}
