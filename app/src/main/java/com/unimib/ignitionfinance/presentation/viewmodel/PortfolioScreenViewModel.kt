package com.unimib.ignitionfinance.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.BuildConfig
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.domain.usecase.*
import com.unimib.ignitionfinance.domain.usecase.networth.*
import com.unimib.ignitionfinance.domain.usecase.flag.*
import com.unimib.ignitionfinance.presentation.viewmodel.state.PortfolioScreenState
import com.unimib.ignitionfinance.presentation.viewmodel.state.ProductPerformance
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class PortfolioScreenViewModel @Inject constructor(
    private val getUserCashUseCase: GetUserCashUseCase,
    private val updateUserCashUseCase: UpdateUserCashUseCase,
    private val getProductListUseCase: GetProductListUseCase,
    private val updateProductListUseCase: UpdateProductListUseCase,
    private val addProductToDatabaseUseCase: AddProductToDatabaseUseCase,
    private val getFirstAddedUseCase: GetFirstAddedUseCase,
    private val updateFirstAddedUseCase: UpdateFirstAddedUseCase,
    private val fetchExchangeUseCase: FetchExchangeUseCase,
    private val fetchHistoricalDataUseCase: FetchHistoricalDataUseCase
) : ViewModel() {

    private val TAG = "PortfolioViewModel"
    private val _state = MutableStateFlow(PortfolioScreenState())
    val state: StateFlow<PortfolioScreenState> = _state

    init {
        getCash()
        getProducts()
        getFirstAdded()
        fetchExchangeRates()
        fetchHistoricalData(BuildConfig.ALPHAVANTAGE_API_KEY)
    }

    private fun processHistoricalData(): List<ProductPerformance> {
        Log.d(TAG, "Starting processHistoricalData")
        val historicalData = state.value.historicalData
        val products = state.value.products

        Log.d(TAG, "Historical data size: ${historicalData.size}")
        Log.d(TAG, "Products size: ${products.size}")

        return products.mapNotNull { product ->
            Log.d(TAG, "Processing product: ${product.ticker}")

            val productHistory = historicalData.firstOrNull()
            if (productHistory == null) {
                Log.e(TAG, "No historical data found for processing")
                return@mapNotNull null
            }

            val dates = productHistory.keys.sorted()
            Log.d(TAG, "Available dates: ${dates.size}")

            if (dates.isEmpty()) {
                Log.e(TAG, "No dates available in historical data")
                return@mapNotNull null
            }

            val productPurchaseDate = product.purchaseDate
            Log.d(TAG, "Product purchase date: $productPurchaseDate")

            val purchaseDate = dates.minByOrNull { date ->
                abs(date.compareTo(productPurchaseDate))
            }
            if (purchaseDate == null) {
                Log.e(TAG, "Could not find nearest purchase date")
                return@mapNotNull null
            }
            Log.d(TAG, "Nearest purchase date found: $purchaseDate")

            val currentDate = dates.last()
            Log.d(TAG, "Current date: $currentDate")

            val purchaseData = productHistory[purchaseDate]
            if (purchaseData == null) {
                Log.e(TAG, "No data found for purchase date: $purchaseDate")
                return@mapNotNull null
            }

            val currentData = productHistory[currentDate]
            if (currentData == null) {
                Log.e(TAG, "No data found for current date: $currentDate")
                return@mapNotNull null
            }

            Log.d(TAG, "Successfully created performance data for ${product.ticker}")
            Log.d(TAG, "Purchase price: ${purchaseData.close}, Current price: ${currentData.close}")

            ProductPerformance(
                ticker = product.ticker,
                purchaseDate = purchaseDate,
                purchasePrice = purchaseData.close,
                currentDate = currentDate,
                currentPrice = currentData.close,
                percentageChange = currentData.percentageChange
            )
        }
    }

    private fun updateProductPerformances() {
        Log.d(TAG, "Starting updateProductPerformances")
        viewModelScope.launch {
            try {
                val performances = processHistoricalData()
                Log.d(TAG, "Processed performances size: ${performances.size}")
                performances.forEach { performance ->
                    Log.d(TAG, "Performance for ${performance.ticker}: " +
                            "Purchase: ${performance.purchasePrice} on ${performance.purchaseDate}, " +
                            "Current: ${performance.currentPrice} on ${performance.currentDate}, " +
                            "Change: ${performance.percentageChange}%")
                }

                _state.update { currentState ->
                    currentState.copy(
                        productPerformances = performances,
                        productPerformancesState = UiState.Success(performances)
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in updateProductPerformances", e)
                _state.update { currentState ->
                    currentState.copy(
                        productPerformancesState = UiState.Error(
                            e.localizedMessage ?: "Failed to process historical data"
                        )
                    )
                }
            }
        }
    }

    private fun fetchExchangeRates() {
        viewModelScope.launch {
            _state.update { it.copy(
                usdExchangeState = UiState.Loading,
                chfExchangeState = UiState.Loading
            )}

            fetchExchangeUseCase.execute("D.USD.EUR.SP00.A")
                .catch { exception ->
                    _state.update { it.copy(
                        usdExchangeState = UiState.Error(
                            exception.localizedMessage ?: "Failed to fetch USD exchange rate"
                        )
                    )}
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val exchange = result.getOrNull()
                                currentState.copy(
                                    usdExchange = exchange,
                                    usdExchangeState = UiState.Success(exchange)
                                )
                            }
                            result.isFailure -> currentState.copy(
                                usdExchangeState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to fetch USD exchange rate"
                                )
                            )
                            else -> currentState.copy(usdExchangeState = UiState.Idle)
                        }
                    }
                }

            fetchExchangeUseCase.execute("D.CHF.EUR.SP00.A")
                .catch { exception ->
                    _state.update { it.copy(
                        chfExchangeState = UiState.Error(
                            exception.localizedMessage ?: "Failed to fetch CHF exchange rate"
                        )
                    )}
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val exchange = result.getOrNull()
                                currentState.copy(
                                    chfExchange = exchange,
                                    chfExchangeState = UiState.Success(exchange)
                                )
                            }
                            result.isFailure -> currentState.copy(
                                chfExchangeState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to fetch CHF exchange rate"
                                )
                            )
                            else -> currentState.copy(chfExchangeState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun getCash() {
        viewModelScope.launch {
            _state.update { it.copy(cashState = UiState.Loading) }
            getUserCashUseCase.execute()
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val cash = result.getOrNull()
                                currentState.copy(
                                    cash = cash ?: "0",
                                    cashState = UiState.Success(cash ?: "0")
                                )
                            }
                            result.isFailure -> currentState.copy(
                                cashState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to load cash"
                                )
                            )
                            else -> currentState.copy(cashState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun updateCash(newCash: String) {
        viewModelScope.launch {
            _state.update { it.copy(cashState = UiState.Loading) }
            updateUserCashUseCase.execute(newCash)
                .catch { exception ->
                    Log.e("PortfolioViewModel", "Error updating cash: ${exception.localizedMessage}")
                    _state.update {
                        it.copy(
                            cashState = UiState.Error(
                                exception.localizedMessage ?: "Failed to update cash"
                            )
                        )
                    }
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val cash = result.getOrNull()
                                currentState.copy(
                                    cash = cash ?: currentState.cash,
                                    cashState = UiState.Success(cash ?: currentState.cash)
                                ).also {
                                    updateFirstAdded(true)
                                }
                            }
                            result.isFailure -> currentState.copy(
                                cashState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to update cash"
                                )
                            )
                            else -> currentState.copy(cashState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun getProducts() {
        viewModelScope.launch {
            _state.update { it.copy(productsState = UiState.Loading) }
            getProductListUseCase.execute()
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val productList = result.getOrNull()
                                currentState.copy(
                                    products = productList ?: emptyList(),
                                    productsState = UiState.Success(productList ?: emptyList())
                                )
                            }
                            result.isFailure -> currentState.copy(
                                productsState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to load products"
                                )
                            )
                            else -> currentState.copy(productsState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun addNewProduct(product: Product) {
        viewModelScope.launch {
            _state.update { it.copy(productsState = UiState.Loading) }
            addProductToDatabaseUseCase.handleProductStorage(product)
                .catch { exception ->
                    Log.e("PortfolioViewModel", "Error handling product storage: ${exception.localizedMessage}")
                    _state.update {
                        it.copy(
                            productsState = UiState.Error(
                                exception.localizedMessage ?: "Failed to handle product storage"
                            )
                        )
                    }
                }
                .collect { result ->
                    if (result.isSuccess) {
                        getProducts()
                        BuildConfig.ALPHAVANTAGE_API_KEY
                    } else {
                        _state.update {
                            it.copy(
                                productsState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to handle product storage"
                                )
                            )
                        }
                    }
                }
        }
    }

    fun removeProduct(productId: String) {
        viewModelScope.launch {
            _state.update { it.copy(productsState = UiState.Loading) }
            updateProductListUseCase.removeProduct(productId)
                .catch { exception ->
                    Log.e("PortfolioViewModel", "Error removing product: ${exception.localizedMessage}")
                    _state.update {
                        it.copy(
                            productsState = UiState.Error(
                                exception.localizedMessage ?: "Failed to remove product"
                            )
                        )
                    }
                }
                .collect { result ->
                    if (result.isSuccess) {
                        getProducts()
                        BuildConfig.ALPHAVANTAGE_API_KEY
                    } else {
                        _state.update {
                            it.copy(
                                productsState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to remove product"
                                )
                            )
                        }
                    }
                }
        }
    }

    fun updateProduct(updatedProduct: Product) {
        viewModelScope.launch {
            val currentProducts = _state.value.products.toMutableList()
            val index = currentProducts.indexOfFirst { it.ticker == updatedProduct.ticker }
            if (index != -1) {
                currentProducts[index] = updatedProduct
                _state.update { it.copy(products = currentProducts) }
            }

            updateProductListUseCase.updateProduct(updatedProduct)
                .catch { exception ->
                    Log.e("PortfolioViewModel", "Error updating product: ${exception.localizedMessage}")
                    _state.update {
                        it.copy(
                            productsState = UiState.Error(
                                exception.localizedMessage ?: "Failed to update product"
                            )
                        )
                    }
                }
                .collect { result ->
                    if (result.isFailure) {
                        getProducts()
                    } else {
                        _state.update {
                            it.copy(
                                productsState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to update product"
                                )
                            )
                        }
                    }
                }
        }
    }

    fun getFirstAdded() {
        viewModelScope.launch {
            _state.update { it.copy(firstAddedState = UiState.Loading) }
            getFirstAddedUseCase.execute()
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val firstAdded = result.getOrNull()
                                currentState.copy(
                                    isFirstAdded = firstAdded == true,
                                    firstAddedState = UiState.Success(firstAdded == true)
                                )
                            }
                            result.isFailure -> currentState.copy(
                                firstAddedState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to load firstAdded"
                                )
                            )
                            else -> currentState.copy(firstAddedState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun updateFirstAdded(newFirstAdded: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(firstAddedState = UiState.Loading) }
            updateFirstAddedUseCase.execute(newFirstAdded)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            firstAddedState = UiState.Error(
                                exception.localizedMessage ?: "Failed to update firstAdded"
                            )
                        )
                    }
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val firstAdded = result.getOrNull()
                                currentState.copy(
                                    isFirstAdded = firstAdded ?: currentState.isFirstAdded,
                                    firstAddedState = UiState.Success(
                                        firstAdded ?: currentState.isFirstAdded
                                    )
                                )
                            }
                            result.isFailure -> currentState.copy(
                                firstAddedState = UiState.Error(
                                    result.exceptionOrNull()?.localizedMessage
                                        ?: "Failed to update firstAdded"
                                )
                            )
                            else -> currentState.copy(firstAddedState = UiState.Idle)
                        }
                    }
                }
        }
    }

    fun calculateUsdAmount(euroAmount: String): String {
        return try {
            val amount = euroAmount.toDoubleOrNull() ?: 0.0
            val exchangeRate = _state.value.usdExchange?.exchangeRate ?: 1.0
            String.format(Locale.US, "%.2f", amount * exchangeRate)
        } catch (_: Exception) {
            "0.00"
        }
    }

    fun calculateChfAmount(euroAmount: String): String {
        return try {
            val amount = euroAmount.toDoubleOrNull() ?: 0.0
            val exchangeRate = _state.value.chfExchange?.exchangeRate ?: 1.0
            String.format(Locale.US, "%.2f", amount * exchangeRate)
        } catch (_: Exception) {
            "0.00"
        }
    }

    fun fetchHistoricalData(apiKey: String) {
        Log.d(TAG, "Starting fetchHistoricalData")
        viewModelScope.launch {
            _state.update { it.copy(historicalDataState = UiState.Loading) }

            fetchHistoricalDataUseCase.execute(apiKey)
                .catch { exception ->
                    Log.e(TAG, "Error fetching historical data", exception)
                    _state.update {
                        it.copy(
                            historicalDataState = UiState.Error(
                                exception.localizedMessage ?: "Failed to fetch historical data"
                            )
                        )
                    }
                }
                .collect { result ->
                    Log.d(TAG, "Historical data fetch result received")
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val historicalData = result.getOrNull()
                                Log.d(TAG, "Historical data fetch success. Data size: ${historicalData?.size}")
                                historicalData?.forEach { data ->
                                    Log.d(TAG, "Historical data entries: ${data.keys.size}")
                                }

                                currentState.copy(
                                    historicalData = historicalData ?: emptyList(),
                                    historicalDataState = UiState.Success(historicalData ?: emptyList())
                                ).also {
                                    updateProductPerformances()
                                }
                            }
                            result.isFailure -> {
                                Log.e(TAG, "Historical data fetch failed", result.exceptionOrNull())
                                currentState.copy(
                                    historicalDataState = UiState.Error(
                                        result.exceptionOrNull()?.localizedMessage
                                            ?: "Failed to fetch historical data"
                                    )
                                )
                            }
                            else -> {
                                Log.d(TAG, "Historical data fetch idle")
                                currentState.copy(historicalDataState = UiState.Idle)
                            }
                        }
                    }
                }
        }
    }

    fun toggleCardExpansion(index: Int) {
        _state.update {
            it.copy(
                expandedCardIndex = if (it.expandedCardIndex == index) -1 else index
            )
        }
    }

    fun getPerformanceForProduct(ticker: String): ProductPerformance? {
        return state.value.productPerformances.find { it.ticker == ticker }
    }
}