package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.*
import com.jalasoft.routesapp.data.model.local.FavoriteDestinationEntity

@Dao
interface FavoriteDestinationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity)

    @Query("SELECT * FROM favoritedestinationentity WHERE cityId=:cityId AND userId=:userId")
    fun getFavoriteDestinationsByCityId(cityId: String, userId: String): List<FavoriteDestinationEntity>

    @Update
    fun editFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity)

    @Delete
    fun deleteFavoriteDestination(favoriteDestinationEntity: FavoriteDestinationEntity)
}
