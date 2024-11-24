package com.unimib.ignitionfinance.data.remote.exchange_api

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeApiService {

    @Headers(
        "Accept: application/vnd.sdmx.data+json;version=1.0.0-wd",
        "Accept-Encoding: gzip, deflate"
    )
    @GET("service/data/EXR/{seriesKey}")
    suspend fun getDailyEuroToDollarExchangeRate(
        @Path("seriesKey") seriesKey: String = "D.USD.EUR.SP00.A", // Default to Euro to Dollar series
        @Query("format") format: String = "jsondata",
        @Query("lastNObservations") lastNObservations: Int = 1  // Fetch the latest observation
    ): ExchangeApiResponseData

    @Headers(
        "Accept: application/vnd.sdmx.data+json;version=1.0.0-wd",
        "Accept-Encoding: gzip, deflate"
    )
    @GET("service/data/EXR/{seriesKey}")
    suspend fun getDailyEuroToSwissFrancExchangeRate(
        @Path("seriesKey") seriesKey: String = "D.CHF.EUR.SP00.A", // Default to Euro to Swiss Franc series
        @Query("format") format: String = "jsondata",
        @Query("lastNObservations") lastNObservations: Int = 1  // Fetch the latest observation
    ): ExchangeApiResponseData
}
