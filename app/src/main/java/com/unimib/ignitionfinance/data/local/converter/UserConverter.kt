package com.unimib.ignitionfinance.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unimib.ignitionfinance.data.model.user.AuthData
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.data.model.user.Settings

class UserConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromAuthData(authData: AuthData): String = gson.toJson(authData)

    @TypeConverter
    fun toAuthData(authDataString: String): AuthData? = gson.fromJson(authDataString, object : TypeToken<AuthData>() {}.type)

    @TypeConverter
    fun fromSettings(settings: Settings): String = gson.toJson(settings)

    @TypeConverter
    fun toSettings(settingsString: String): Settings? = gson.fromJson(settingsString, object : TypeToken<Settings>() {}.type)

    @TypeConverter
    fun fromProductList(productList: List<Product>): String = gson.toJson(productList)

    @TypeConverter
    fun toProductList(productListString: String): List<Product>? =
        gson.fromJson(productListString, object : TypeToken<List<Product>>() {}.type)
}