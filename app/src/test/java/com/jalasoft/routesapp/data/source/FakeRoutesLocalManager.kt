package com.jalasoft.routesapp.data.source

import com.jalasoft.routesapp.data.local.room.interfaces.RouteLocalRepository
import com.jalasoft.routesapp.data.model.remote.LineInfo

class FakeRoutesLocalManager : RouteLocalRepository {

    override fun getAllLinesByCityId(cityId: String): List<LineInfo> {
        return FakeRoutesData.lineInfo
    }
}
