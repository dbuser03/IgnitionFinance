package com.unimib.ignitionfinance.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.unimib.ignitionfinance.data.local.converter.UserConverter
import com.unimib.ignitionfinance.data.local.entity.User

@Database(entities = [User::class], version = 1)
@TypeConverters(UserConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
