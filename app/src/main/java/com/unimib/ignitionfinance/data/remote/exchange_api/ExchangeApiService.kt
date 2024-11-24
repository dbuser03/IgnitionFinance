package com.unimib.ignitionfinance.data.remote.exchange_api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeApiService {

    // Get daily Euro to Dollar exchange rate
    @GET("service/data/EXR/{seriesKey}")
    suspend fun getDailyEuroToDollarExchangeRate(
        @Path("seriesKey") seriesKey: String = "D.USD.EUR.SP00.A",
        @Query("format") format: String = "jsondata",
        @Query("lastNObservations") lastNObservations: Int = 1  // Fetch the most recent observation
    ): ExchangeApiResponseData

    // Get daily Euro to Swiss Franc exchange rate
    @GET("service/data/EXR/{seriesKey}")
    suspend fun getDailyEuroToSwissFrancExchangeRate(
        @Path("seriesKey") seriesKey: String = "D.CHF.EUR.SP00.A",
        @Query("format") format: String = "jsondata",
        @Query("lastNObservations") lastNObservations: Int = 1  // Fetch the most recent observation
    ): ExchangeApiResponseData
}
