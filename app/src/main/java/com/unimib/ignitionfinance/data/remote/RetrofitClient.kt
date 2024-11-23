package com.unimib.ignitionfinance.data.remote

import com.unimib.ignitionfinance.data.remote.stock_api.StockApiService
import com.unimib.ignitionfinance.data.remote.inflation_api.InflationApiService
//import com.unimib.ignitionfinance.data.remote.exchangerate_api.ExchangeRateApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Funzione generica per creare un Retrofit con il baseUrl
    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Base URLs per le diverse API
    private const val STOCK_API_BASE_URL = "https://api.stockexample.com/"  // Cambia con la base URL di Stock API
    private const val INFLATION_API_BASE_URL = "https://api.inflationexample.com/"  // Cambia con la base URL di Inflation API
    private const val EXCHANGE_RATE_API_BASE_URL = "https://api.exchangerateexample.com/"  // Cambia con la base URL di Exchange Rate API

    // Crea i servizi per ogni API
    val stockApiService: StockApiService = createRetrofit(STOCK_API_BASE_URL).create(StockApiService::class.java)
    val inflationApiService: InflationApiService = createRetrofit(INFLATION_API_BASE_URL).create(InflationApiService::class.java)
    //val exchangeRateApiService: ExchangeRateApiService = createRetrofit(EXCHANGE_RATE_API_BASE_URL).create(ExchangeRateApiService::class.java)
}
