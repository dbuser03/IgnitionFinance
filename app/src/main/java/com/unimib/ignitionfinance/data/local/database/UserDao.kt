package com.unimib.ignitionfinance.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.unimib.ignitionfinance.data.local.entity.User

@Dao
interface UserDao {
    @Insert
    fun add(user: User)

    @Delete
    fun delete(user: User)

    @Update
    fun update(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}