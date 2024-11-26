package com.unimib.ignitionfinance.data.remote

import org.junit.Assert.assertNotNull
import org.junit.Test

class RetrofitClientTest {

    @Test
    fun `test stockApiService is correctly initialized`() {
        val stockService = RetrofitClient.stockApiService
        assertNotNull(stockService)
    }

    @Test
    fun `test inflationApiService is correctly initialized`() {
        val inflationService = RetrofitClient.inflationApiService
        assertNotNull(inflationService)
    }

    @Test
    fun `test exchangeApiService is correctly initialized`() {
        val exchangeService = RetrofitClient.exchangeApiService
        assertNotNull(exchangeService)
    }
}

