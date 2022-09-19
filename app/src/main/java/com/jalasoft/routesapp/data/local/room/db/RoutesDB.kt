package com.jalasoft.routesapp.data.local.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jalasoft.routesapp.data.local.room.dao.*
import com.jalasoft.routesapp.data.model.local.*

@Database(entities = [LineEntity::class, LineCategoriesEntity::class, LineRouteEntity::class, RoutePointsHolder::class, StopsHolder::class, TourPointEntity::class, TourPointsCategoryEntity::class], version = 1, exportSchema = false)
abstract class RoutesDB : RoomDatabase() {
    abstract fun lineDao(): LineDao
    abstract fun lineCategoryDao(): LineCategoriesDao
    abstract fun lineRouteDao(): LineRouteDao
    abstract fun tourPointDao(): TourPointDao
    abstract fun tourPointCategoryDao(): TourPointCategoryDao

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
