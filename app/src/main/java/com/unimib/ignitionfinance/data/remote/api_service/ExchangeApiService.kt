package com.unimib.ignitionfinance.data.remote.api_service

import com.unimib.ignitionfinance.data.remote.api_response.exchange.ExchangeApiResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ExchangeApiService {

    @GET("service/data/{dataflow}/{seriesKey}")
    suspend fun getExchangeRate(
        @Path("dataflow") dataflow: String = "ECB,EXR,1.0",
        @Path("seriesKey") seriesKey: String,
        @Query("format") format: String = "jsondata",
        @Query("lastNObservations") lastNObservations: Int = 1
    ): Response<ExchangeApiResponseData>
}
