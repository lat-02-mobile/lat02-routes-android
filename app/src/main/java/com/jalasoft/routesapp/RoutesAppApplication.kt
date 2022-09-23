package com.jalasoft.routesapp

import android.app.Application
import android.content.res.Resources
import com.jalasoft.routesapp.data.local.room.db.RoutesDB
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RoutesAppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        resource = resources
        routesDB = RoutesDB.create(this)
    }
    companion object {
        var resource: Resources? = null
        var routesDB: RoutesDB? = null
    }
}
