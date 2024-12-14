package com.unimib.ignitionfinance.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.data.model.user.Settings
import javax.inject.Inject

inline fun <reified T> Gson.toJsonString(data: T): String = this.toJson(data)
inline fun <reified T> Gson.fromJsonString(json: String): T? = try {
    this.fromJson(json, object : TypeToken<T>() {}.type)
} catch (_: Exception) {
    null
}

class UserConverter @Inject constructor(private val gson: Gson) {

    @TypeConverter
    fun fromAuthData(authData: AuthData): String = gson.toJsonString(authData)

    @TypeConverter
    fun toAuthData(authDataString: String): AuthData? = gson.fromJsonString(authDataString)

    @TypeConverter
    fun fromSettings(settings: Settings): String = gson.toJsonString(settings)

    @TypeConverter
    fun toSettings(settingsString: String): Settings? = gson.fromJsonString(settingsString)
}
