package com.unimib.ignitionfinance.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.unimib.ignitionfinance.data.local.entity.UserData

@Dao
interface UserDataDao {
    @Insert
    fun insert(user: UserData)

    @Delete
    fun delete(user: UserData)


}