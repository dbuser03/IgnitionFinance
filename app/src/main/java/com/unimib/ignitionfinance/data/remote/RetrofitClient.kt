package com.unimib.ignitionfinance.data.remote

import com.unimib.ignitionfinance.data.remote.service.ExchangeService
import com.unimib.ignitionfinance.data.remote.service.StockService
import com.unimib.ignitionfinance.data.remote.service.SearchStockService
import com.unimib.ignitionfinance.data.remote.service.InflationService
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
    private const val SEARCH_STOCK_API_BASE_URL = "https://www.alphavantage.co/"
    private const val INFLATION_API_BASE_URL = "https://data-api.ecb.europa.eu/"
    private const val EXCHANGE_API_BASE_URL = "http://data-api.ecb.europa.eu/"

    val stockService: StockService = createRetrofit(STOCK_API_BASE_URL).create(StockService::class.java)
    val searchStockService: SearchStockService = createRetrofit(SEARCH_STOCK_API_BASE_URL).create(SearchStockService::class.java)
    val inflationService: InflationService = createRetrofit(INFLATION_API_BASE_URL).create(InflationService::class.java)
    val exchangeService: ExchangeService = createRetrofit(EXCHANGE_API_BASE_URL).create(ExchangeService::class.java)
}
