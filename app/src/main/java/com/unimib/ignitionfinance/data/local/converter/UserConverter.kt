package com.unimib.ignitionfinance.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unimib.ignitionfinance.data.remote.model.user.AuthData
import com.unimib.ignitionfinance.data.remote.model.user.DailyReturn
import com.unimib.ignitionfinance.data.remote.model.user.Product
import com.unimib.ignitionfinance.data.remote.model.user.Settings
import com.unimib.ignitionfinance.domain.simulation.model.SimulationResult
import kotlin.Pair
import kotlin.collections.List

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

    @TypeConverter
    fun fromDataset(dataset: List<DailyReturn>): String = gson.toJson(dataset)

    @TypeConverter
    fun toDataset(datasetString: String): List<DailyReturn>? =
        gson.fromJson(datasetString, object : TypeToken<List<DailyReturn>>() {}.type)

    @TypeConverter
    fun fromSimulationOutcome(outcome: Pair<List<SimulationResult>, Double?>?): String? {
        return outcome?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toSimulationOutcome(outcomeString: String?): Pair<List<SimulationResult>, Double?>? {
        return outcomeString?.let {
            gson.fromJson(it, object : TypeToken<Pair<List<SimulationResult>, Double?>>() {}.type)
        }
    }

}