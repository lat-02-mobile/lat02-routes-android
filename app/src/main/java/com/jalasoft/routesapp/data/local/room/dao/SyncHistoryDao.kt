package com.jalasoft.routesapp.data.local.room.dao

import androidx.room.*
import com.jalasoft.routesapp.data.model.local.SyncHistoryEntity

@Dao
interface SyncHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSyncHistoryDao(syncHistoryEntity: SyncHistoryEntity)

    @Query("SELECT * FROM synchistoryentity WHERE cityId = :cityId")
    fun getAllSyncHisotry(cityId: String): List<SyncHistoryEntity>

    @Update(entity = SyncHistoryEntity::class)
    fun updateSyncHistoryEntity(syncHistoryEntity: SyncHistoryEntity)
}
