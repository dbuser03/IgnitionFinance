package com.unimib.ignitionfinance.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import com.unimib.ignitionfinance.data.local.entity.UserData

@Dao
interface UserDataDao {
    @Insert
    fun insertAll(user: UserData)
}