package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.jalasoft.routesapp.data.model.local.FavoriteDestinationEntity

@Dao
interface FavoriteDestinationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity)
}