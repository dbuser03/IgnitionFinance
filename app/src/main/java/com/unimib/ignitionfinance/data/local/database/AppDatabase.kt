package com.unimib.ignitionfinance.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.unimib.ignitionfinance.data.local.entity.UserData

@Database(entities = [UserData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDataDao(): UserDataDao
}