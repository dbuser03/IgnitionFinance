package com.unimib.ignitionfinance.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.unimib.ignitionfinance.data.model.AuthData
import com.unimib.ignitionfinance.data.model.Settings

@Entity(tableName = "users")
data class UserData (
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "surname") val surname: String?,
    @ColumnInfo(name = "authData") val authData: AuthData?,
    @ColumnInfo(name = "settings") val settings: Settings?
)