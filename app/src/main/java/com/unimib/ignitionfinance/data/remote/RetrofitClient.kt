package com.unimib.ignitionfinance.data.remote

import com.unimib.ignitionfinance.data.remote.service.ExchangeApiService
import com.unimib.ignitionfinance.data.remote.service.StockApiService
import com.unimib.ignitionfinance.data.remote.service.InflationApiService
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
    private const val EXCHANGE_API_BASE_URL = "http://data-api.ecb.europa.eu/"

    val stockApiService: StockApiService = createRetrofit(STOCK_API_BASE_URL).create(StockApiService::class.java)
    val inflationApiService: InflationApiService = createRetrofit(INFLATION_API_BASE_URL).create(InflationApiService::class.java)
    val exchangeApiService: ExchangeApiService = createRetrofit(EXCHANGE_API_BASE_URL).create(ExchangeApiService::class.java)
}
