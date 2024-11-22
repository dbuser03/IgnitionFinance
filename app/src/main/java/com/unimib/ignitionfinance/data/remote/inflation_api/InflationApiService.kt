package com.unimib.ignitionfinance.data.remote.inflation_api

import retrofit2.http.GET
import retrofit2.http.Query

interface IndexApiService {

    // Use @GET with query parameters
    @GET("query")
    suspend fun getIndexData(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("outputsize") outputSize: String = "full",
        @Query("datatype") datatype: String = "json",
        @Query("apikey") apiKey: String
    ): InflationApiResponseData
}