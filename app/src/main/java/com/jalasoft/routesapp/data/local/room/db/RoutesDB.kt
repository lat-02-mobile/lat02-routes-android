package com.jalasoft.routesapp.data.local.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jalasoft.routesapp.data.local.room.dao.*
import com.jalasoft.routesapp.data.model.local.*

@Database(entities = [LineEntity::class, LineCategoriesEntity::class, LineRouteEntity::class, RoutePointsHolder::class, StopsHolder::class, TourPointEntity::class, TourPointsCategoryEntity::class, FavoriteDestinationEntity::class], version = 3, exportSchema = false)
abstract class RoutesDB : RoomDatabase() {
    abstract fun lineDao(): LineDao
    abstract fun lineCategoryDao(): LineCategoriesDao
    abstract fun lineRouteDao(): LineRouteDao
    abstract fun tourPointDao(): TourPointDao
    abstract fun tourPointCategoryDao(): TourPointCategoryDao
    abstract fun favoriteDestinationDao(): FavoriteDestinationDao

    companion object {
        @Volatile
        private var INSTANCE: RoutesDB? = null

        fun create(context: Context): RoutesDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context, RoutesDB::class.java, "RoutesDB")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `FavoriteDestinationEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `destlatitude` DOUBLE NOT NULL, `destlongitude` DOUBLE NOT NULL, `cityId` TEXT NOT NULL, `userId` TEXT NOT NULL, `createdAt` DOUBLE NOT NULL)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `LineEntity` ADD COLUMN createAt INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `LineEntity` ADD COLUMN updateAt INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `LineRouteEntity` ADD COLUMN createAt INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `LineRouteEntity` ADD COLUMN updateAt INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `LineCategoriesEntity` ADD COLUMN createAt INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `LineCategoriesEntity` ADD COLUMN updateAt INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `TourPointEntity` ADD COLUMN createAt INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `TourPointEntity` ADD COLUMN updateAt INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `TourPointCategoryEntity` ADD COLUMN createAt INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `TourPointCategoryEntity` ADD COLUMN updateAt INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
