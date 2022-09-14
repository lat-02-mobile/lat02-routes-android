package com.jalasoft.routesapp.data.local.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jalasoft.routesapp.data.local.room.dao.TourPointDao
import com.jalasoft.routesapp.data.model.local.TourPointCategory
import com.jalasoft.routesapp.data.model.local.TourPointEntity

@Database(entities = [TourPointEntity::class, TourPointCategory::class], version = 1, exportSchema = false) // [Line::class, LineRoute::class, LineCategories::class, TourPoint::class, TourPointCategory::class]
abstract class RoutesDB : RoomDatabase() {
    // abstract fun lineDao(): LineDao
    // abstract fun lineRouteDao(): LineRouteDao
    abstract fun tourPointDao(): TourPointDao

    companion object {
        @Volatile
        private var INSTANCE: RoutesDB? = null

        fun create(context: Context): RoutesDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context, RoutesDB::class.java, "RoutesDB")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
