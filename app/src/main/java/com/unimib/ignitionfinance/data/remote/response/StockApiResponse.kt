package com.unimib.ignitionfinance.data.remote.response

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

// Main API response
data class StockApiResponseData(
    @SerializedName("Meta Data")
    val metaData: MetaData,
    @SerializedName("Time Series (Daily)")
    val timeSeries: Map<String, TimeSeriesData>
)

// MetaData represents the metadata returned by the API
data class MetaData(
    @SerializedName("1. Information") val information: String,
    @SerializedName("2. Symbol") val symbol: String,
    @SerializedName("3. Last Refreshed") val lastRefreshed: String,
    @SerializedName("4. Output Size") val outputSize: String,
    @SerializedName("5. Time Zone") val timeZone: String
)

// TimeSeriesData represents each day's data
data class TimeSeriesData(
    @SerializedName("1. open") val open: BigDecimal,
    @SerializedName("2. high") val high: BigDecimal,
    @SerializedName("3. low") val low: BigDecimal,
    @SerializedName("4. close") val close: BigDecimal,
    @SerializedName("5. volume") val volume: Long
)
