package com.unimib.ignitionfinance.data.local.converter

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.math.BigDecimal

class DatasetConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromDateList(datesList: List<String> ): String = gson.toJson(datesList)

    @TypeConverter
    fun toDateList (datesListString: String ): List<String>? =
        gson.fromJson(datesListString, object : TypeToken<List<String>>() {}.type)

    @TypeConverter
    fun fromWeightedReturnsList(weightedReturnsList: List<BigDecimal> ): String = gson.toJson(weightedReturnsList)

    @TypeConverter
    fun toWeightedReturnsList(weightedReturnsString: String): List<BigDecimal>? =
        gson.fromJson(weightedReturnsString, object : TypeToken<List<BigDecimal>>() {}.type)

}