package com.unimib.ignitionfinance.data.remote.stock_api

import retrofit2.http.GET
import retrofit2.http.Query

interface IndexApiResponse {

    // Use @GET with query parameters
    @GET("query")
    suspend fun getIndexData(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("outputsize") outputSize: String = "full",
        @Query("datatype") datatype: String = "json",
        @Query("apikey") apiKey: String
    ): IndexApiResponseData
}