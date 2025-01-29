package com.unimib.ignitionfinance.presentation.viewmodel.state

import com.unimib.ignitionfinance.data.model.ExchangeData
import com.unimib.ignitionfinance.data.model.user.Product

data class PortfolioScreenState(
    val cash: String = "0",
    val products: List<Product> = emptyList(),
    val isFirstAdded: Boolean = false,
    val usdExchange: ExchangeData? = null,
    val chfExchange: ExchangeData? = null,
    val cashState: UiState<String> = UiState.Idle,
    val productsState: UiState<List<Product>> = UiState.Idle,
    val firstAddedState: UiState<Boolean> = UiState.Idle,
    val usdExchangeState: UiState<ExchangeData?> = UiState.Idle,
    val chfExchangeState: UiState<ExchangeData?> = UiState.Idle
)