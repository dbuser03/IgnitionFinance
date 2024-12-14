package com.unimib.ignitionfinance.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.data.model.user.Settings

class UserConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromAuthData(authData: AuthData): String {
        return gson.toJson(authData)
    }

    @TypeConverter
    fun toAuthData(authDataString: String): AuthData {
        val type = object : TypeToken<AuthData>() {}.type
        return gson.fromJson(authDataString, type)
    }

    @TypeConverter
    fun fromSettings(settings: Settings): String {
        return gson.toJson(settings)
    }

    @TypeConverter
    fun toSettings(settingsString: String): Settings {
        val type = object : TypeToken<Settings>() {}.type
        return gson.fromJson(settingsString, type)
    }
}