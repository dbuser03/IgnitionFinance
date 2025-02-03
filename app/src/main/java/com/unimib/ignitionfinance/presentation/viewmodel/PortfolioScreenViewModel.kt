package com.unimib.ignitionfinance.presentation.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import java.math.RoundingMode

@RequiresApi(Build.VERSION_CODES.O)
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

    private val _state = MutableStateFlow(PortfolioScreenState())
    val state: StateFlow<PortfolioScreenState> = _state

    init {
        getCash()
        getProducts()
        getFirstAdded()
        fetchExchangeRates()
        fetchHistoricalData(BuildConfig.ALPHAVANTAGE_API_KEY)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun processHistoricalData(): List<ProductPerformance> {
        val historicalDataList = state.value.historicalData
        val products = state.value.products

        val performances = mutableListOf<ProductPerformance>()

        val isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val purchaseFormatter1 = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val purchaseFormatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        fun parsePurchaseDate(dateStr: String): LocalDate? {
            return try {
                LocalDate.parse(dateStr, purchaseFormatter1)
            } catch (_: Exception) {
                try {
                    LocalDate.parse(dateStr, purchaseFormatter2)
                } catch (_: Exception) {
                    null
                }
            }
        }

        products.forEachIndexed { index, product ->
            val originalPurchaseDateStr = product.purchaseDate
            val purchaseDate = parsePurchaseDate(originalPurchaseDateStr)
            if (purchaseDate == null) {
                return@forEachIndexed
            }

            val historicalData = historicalDataList.getOrNull(index) ?: return@forEachIndexed

            val dateStockList = historicalData.mapNotNull { entry ->
                try {
                    val date = LocalDate.parse(entry.key, isoFormatter)
                    Pair(date, entry.value)
                } catch (_: Exception) {
                    null
                }
            }
            if (dateStockList.isEmpty()) return@forEachIndexed

            val closestEntry = dateStockList.minByOrNull { (date, _) ->
                abs(ChronoUnit.DAYS.between(date, purchaseDate))
            } ?: return@forEachIndexed

            val lastEntry = dateStockList.maxByOrNull { (date, _) -> date } ?: return@forEachIndexed

            val purchasePrice: BigDecimal = closestEntry.second.close
            val currentPrice: BigDecimal = lastEntry.second.close

            val percentageChange = if (purchasePrice.compareTo(BigDecimal.ZERO) != 0) {
                (currentPrice.subtract(purchasePrice))
                    .divide(purchasePrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal(100))
            } else {
                BigDecimal.ZERO
            }

            val performance = ProductPerformance(
                ticker = product.ticker,
                purchaseDate = closestEntry.first.format(isoFormatter),
                purchasePrice = purchasePrice,
                currentDate = lastEntry.first.format(isoFormatter),
                currentPrice = currentPrice,
                percentageChange = percentageChange
            )
            performances.add(performance)
        }

        return performances
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateProductPerformances() {
        viewModelScope.launch {
            try {
                val performances = processHistoricalData()

                _state.update { currentState ->
                    currentState.copy(
                        productPerformances = performances,
                        productPerformancesState = UiState.Success(performances)
                    )
                }
            } catch (e: Exception) {
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

    @RequiresApi(Build.VERSION_CODES.O)
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
                        fetchHistoricalData(BuildConfig.ALPHAVANTAGE_API_KEY)
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

    @RequiresApi(Build.VERSION_CODES.O)
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
                        fetchHistoricalData(BuildConfig.ALPHAVANTAGE_API_KEY)
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

    @RequiresApi(Build.VERSION_CODES.O)
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
                        fetchHistoricalData(BuildConfig.ALPHAVANTAGE_API_KEY)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchHistoricalData(apiKey: String) {
        viewModelScope.launch {
            _state.update { it.copy(historicalDataState = UiState.Loading) }

            fetchHistoricalDataUseCase.execute(apiKey)
                .catch { exception ->
                    _state.update {
                        it.copy(
                            historicalDataState = UiState.Error(
                                exception.localizedMessage ?: "Failed to fetch historical data"
                            )
                        )
                    }
                }
                .collect { result ->
                    _state.update { currentState ->
                        when {
                            result.isSuccess -> {
                                val historicalData = result.getOrNull()

                                currentState.copy(
                                    historicalData = historicalData ?: emptyList(),
                                    historicalDataState = UiState.Success(historicalData ?: emptyList())
                                ).also {
                                    updateProductPerformances()
                                }
                            }
                            result.isFailure -> {
                                currentState.copy(
                                    historicalDataState = UiState.Error(
                                        result.exceptionOrNull()?.localizedMessage
                                            ?: "Failed to fetch historical data"
                                    )
                                )
                            }
                            else -> {
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