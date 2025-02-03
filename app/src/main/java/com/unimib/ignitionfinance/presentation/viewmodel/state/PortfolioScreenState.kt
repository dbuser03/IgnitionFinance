package com.unimib.ignitionfinance.presentation.viewmodel.state

import com.unimib.ignitionfinance.data.model.ExchangeData
import com.unimib.ignitionfinance.data.model.StockData
import com.unimib.ignitionfinance.data.model.user.Product

data class PortfolioScreenState(
    val cash: String = "0",
    val cashState: UiState<String> = UiState.Idle,
    val products: List<Product> = emptyList(),
    val productsState: UiState<List<Product>> = UiState.Idle,
    val isFirstAdded: Boolean = false,
    val firstAddedState: UiState<Boolean> = UiState.Idle,
    val usdExchange: ExchangeData? = null,
    val usdExchangeState: UiState<ExchangeData?> = UiState.Idle,
    val chfExchange: ExchangeData? = null,
    val chfExchangeState: UiState<ExchangeData?> = UiState.Idle,
    val expandedCardIndex: Int = -1,
    val historicalData: List<Pair<String, Map<String, StockData>>> = emptyList(),
    val historicalDataState: UiState<List<Pair<String, Map<String, StockData>>>> = UiState.Idle,
    val productPerformances: List<ProductPerformance> = emptyList(),
    val productPerformancesState: UiState<List<ProductPerformance>> = UiState.Idle
)