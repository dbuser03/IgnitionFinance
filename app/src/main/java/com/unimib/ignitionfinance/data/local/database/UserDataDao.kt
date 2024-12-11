package com.unimib.ignitionfinance.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.unimib.ignitionfinance.data.local.entity.UserData

@Dao
interface UserDataDao {
    @Insert
    fun insert(user: UserData)

    @Delete
    fun delete(user: UserData)

    @Update
    fun update(user: UserData)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): UserData?
}