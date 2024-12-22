package com.unimib.ignitionfinance.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.unimib.ignitionfinance.data.local.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun add(user: User)

    @Delete
    suspend fun delete(user: User)

    @Update
    suspend fun update(user: User)

    @Query("UPDATE users SET updated_at = :timestamp WHERE id = :userId")
    suspend fun updateTimestamp(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE updated_at > :timestamp")
    suspend fun getUsersUpdatedAfter(timestamp: Long): List<User>

    @Query("UPDATE users SET last_sync_timestamp = :timestamp WHERE id = :userId")
    suspend fun updateLastSyncTimestamp(userId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM users WHERE last_sync_timestamp < updated_at OR last_sync_timestamp IS NULL")
    suspend fun getUnsyncedUsers(): List<User>

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}