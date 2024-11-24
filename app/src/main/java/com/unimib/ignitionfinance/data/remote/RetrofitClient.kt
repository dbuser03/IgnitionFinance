package com.unimib.ignitionfinance.data.remote

import com.unimib.ignitionfinance.data.remote.stock_api.StockApiService
import com.unimib.ignitionfinance.data.remote.inflation_api.InflationApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private const val STOCK_API_BASE_URL = "https://www.alphavantage.co/"
    private const val INFLATION_API_BASE_URL = "https://data-api.ecb.europa.eu/"

    val stockApiService: StockApiService = createRetrofit(STOCK_API_BASE_URL).create(StockApiService::class.java)
    val inflationApiService: InflationApiService = createRetrofit(INFLATION_API_BASE_URL).create(InflationApiService::class.java)
}
