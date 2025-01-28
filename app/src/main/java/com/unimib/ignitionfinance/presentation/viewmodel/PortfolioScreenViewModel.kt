package com.unimib.ignitionfinance.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.domain.usecase.AddProductToDatabaseUseCase
import com.unimib.ignitionfinance.domain.usecase.GetProductListUseCase
import com.unimib.ignitionfinance.domain.usecase.UpdateProductListUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.GetUserCashUseCase
import com.unimib.ignitionfinance.domain.usecase.networth.UpdateUserCashUseCase
import com.unimib.ignitionfinance.domain.usecase.flag.GetFirstAddedUseCase
import com.unimib.ignitionfinance.domain.usecase.flag.UpdateFirstAddedUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.PortfolioScreenState
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioScreenViewModel @Inject constructor(
    private val getUserCashUseCase: GetUserCashUseCase,
    private val updateUserCashUseCase: UpdateUserCashUseCase,
    private val getProductListUseCase: GetProductListUseCase,
    private val updateProductListUseCase: UpdateProductListUseCase,
    private val addProductToDatabaseUseCase: AddProductToDatabaseUseCase,
    private val getFirstAddedUseCase: GetFirstAddedUseCase,
    private val updateFirstAddedUseCase: UpdateFirstAddedUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioScreenState())
    val state: StateFlow<PortfolioScreenState> = _state

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

    private fun getProducts() {
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
            _state.update { it.copy(productsState = UiState.Loading) }
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
                    if (result.isSuccess) {
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
}