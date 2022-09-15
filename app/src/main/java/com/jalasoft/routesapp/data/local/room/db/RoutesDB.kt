package com.jalasoft.routesapp.data.local.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jalasoft.routesapp.data.local.room.dao.LineCategoriesDao
import com.jalasoft.routesapp.data.local.room.dao.LineDao
import com.jalasoft.routesapp.data.local.room.dao.TourPointCategoryDao
import com.jalasoft.routesapp.data.local.room.dao.TourPointDao
import com.jalasoft.routesapp.data.model.local.LineCategoriesEntity
import com.jalasoft.routesapp.data.model.local.LineEntity
import com.jalasoft.routesapp.data.model.local.TourPointsCategoryEntity
import com.jalasoft.routesapp.data.model.local.TourPointEntity

@Database(entities = [LineEntity::class, LineCategoriesEntity::class, TourPointEntity::class, TourPointsCategoryEntity::class], version = 1, exportSchema = false)
abstract class RoutesDB : RoomDatabase() {
    abstract fun lineDao(): LineDao
    abstract fun lineCategoryDao(): LineCategoriesDao
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
