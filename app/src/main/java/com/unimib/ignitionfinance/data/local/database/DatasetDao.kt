package com.unimib.ignitionfinance.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unimib.ignitionfinance.data.local.entity.Dataset

@Dao
interface DatasetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataset(dataset: Dataset)

    @Query("SELECT * FROM datasets ORDER BY dates DESC LIMIT 1")
    suspend fun getLatestDataset(): Dataset?
}
