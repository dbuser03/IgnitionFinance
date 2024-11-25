package com.unimib.ignitionfinance.data.remote.exchange_api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeApiService {

    @GET("service/data/{dataflow}/{seriesKey}")
    suspend fun getDailyEuroToDollarExchangeRate(
        @Path("dataflow") dataflow: String = "ECB, EXR, 1.0",
        @Path("seriesKey") seriesKey: String = "D.USD.EUR.SP00.A", // Default to Euro to Dollar series
        @Query("format") format: String = "jsondata",
        @Query("lastNObservations") lastNObservations: Int = 1  // Fetch the latest observation
    ): Response<ExchangeApiResponseData>

    @GET("service/data/{dataflow}/{seriesKey}")
    suspend fun getDailyEuroToSwissFrancExchangeRate(
        @Path("dataflow") dataflow: String = "ECB, EXR, 1.0",
        @Path("seriesKey") seriesKey: String = "D.CHF.EUR.SP00.A", // Default to Euro to Swiss Franc series
        @Query("format") format: String = "jsondata",
        @Query("lastNObservations") lastNObservations: Int = 1  // Fetch the latest observation
    ): Response<ExchangeApiResponseData>
}
