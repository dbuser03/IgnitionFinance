package com.unimib.ignitionfinance.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unimib.ignitionfinance.data.local.entity.DatasetEntity

@Dao
interface DatasetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataset(dataset: DatasetEntity)

    @Query("SELECT * FROM datasets ORDER BY date DESC LIMIT 1")
    suspend fun getLatestDataset(): DatasetEntity?
}
