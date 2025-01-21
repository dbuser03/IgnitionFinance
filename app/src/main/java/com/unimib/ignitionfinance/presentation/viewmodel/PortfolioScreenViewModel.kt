package com.unimib.ignitionfinance.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.domain.usecase.AddProductToDatabaseUseCase
import com.unimib.ignitionfinance.domain.usecase.GetProductListUseCase
import com.unimib.ignitionfinance.domain.usecase.UpdateProductListUseCase
import com.unimib.ignitionfinance.domain.usecase.cash.GetUserCashUseCase
import com.unimib.ignitionfinance.domain.usecase.cash.UpdateUserCashUseCase
import com.unimib.ignitionfinance.domain.usecase.flag.GetFirstAddedUseCase
import com.unimib.ignitionfinance.domain.usecase.flag.UpdateFirstAddedUseCase
import com.unimib.ignitionfinance.presentation.viewmodel.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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

    private val _cash = MutableStateFlow<String?>("0")
    val cash: StateFlow<String?> = _cash

    private val _cashState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val cashState: StateFlow<UiState<String>> = _cashState

    private val _firstAddedState = MutableStateFlow<UiState<Boolean>>(UiState.Loading)
    val fistAddedState: StateFlow<UiState<Boolean>> = _firstAddedState

    private val _firstAdded = MutableStateFlow<Boolean?>(false)
    val firstAdded: StateFlow<Boolean?> = _firstAdded

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _productsState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val productsState: StateFlow<UiState<List<Product>>> = _productsState

    private fun getCash() {
        viewModelScope.launch {
            _cashState.value = UiState.Loading
            getUserCashUseCase.execute()
                .collect { result ->
                    _cashState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { cash ->
                                _cash.value = cash
                                UiState.Success(cash)
                            } ?: UiState.Error("Cash not found")
                        }
                        result.isFailure -> UiState.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Failed to load cash"
                        )
                        else -> UiState.Idle
                    }
                }
        }
    }

    fun updateCash(newCash: String) {
        viewModelScope.launch {
            _cashState.value = UiState.Loading

            updateUserCashUseCase.execute(newCash)
                .catch { exception ->
                    Log.e("PortfolioViewModel", "Error updating cash: ${exception.localizedMessage}")
                    _cashState.value = UiState.Error(
                        exception.localizedMessage ?: "Failed to update cash"
                    )
                }
                .collect { result ->
                    _cashState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { cash ->
                                _cash.value = cash
                                UiState.Success(cash)
                            } ?: UiState.Error("Failed to update cash")
                        }
                        result.isFailure -> UiState.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Failed to update cash"
                        )
                        else -> UiState.Idle
                    }
                }
        }
    }

    private fun getProducts() {
        viewModelScope.launch {
            _productsState.value = UiState.Loading
            getProductListUseCase.execute()
                .collect { result ->
                    _productsState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { productList ->
                                _products.value = productList
                                UiState.Success(productList)
                            } ?: UiState.Error("Products not found")
                        }
                        result.isFailure -> UiState.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Failed to load products"
                        )
                        else -> UiState.Idle
                    }
                }
        }
    }

    fun addNewProduct(product: Product) {
        viewModelScope.launch {
            _productsState.value = UiState.Loading
            addProductToDatabaseUseCase.handleProductStorage(product)
                .catch { exception ->
                    Log.e("PortfolioViewModel", "Error handling product storage: ${exception.localizedMessage}")
                    _productsState.value = UiState.Error(
                        exception.localizedMessage ?: "Failed to handle product storage"
                    )
                }
                .collect { result ->
                    if (result.isSuccess) {
                        getProducts()
                    } else {
                        _productsState.value = UiState.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Failed to handle product storage"
                        )
                    }
                }
        }
    }

    fun removeProduct(productId: String) {
        viewModelScope.launch {
            _productsState.value = UiState.Loading
            updateProductListUseCase.removeProduct(productId)
                .catch { exception ->
                    Log.e("PortfolioViewModel", "Error removing product: ${exception.localizedMessage}")
                    _productsState.value = UiState.Error(
                        exception.localizedMessage ?: "Failed to remove product"
                    )
                }
                .collect { result ->
                    if (result.isSuccess) {
                        getProducts() // Refresh the product list
                    } else {
                        _productsState.value = UiState.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Failed to remove product"
                        )
                    }
                }
        }
    }

    fun updateProduct(updatedProduct: Product) {
        viewModelScope.launch {
            _productsState.value = UiState.Loading
            updateProductListUseCase.updateProduct(updatedProduct)
                .catch { exception ->
                    Log.e("PortfolioViewModel", "Error updating product: ${exception.localizedMessage}")
                    _productsState.value = UiState.Error(
                        exception.localizedMessage ?: "Failed to update product"
                    )
                }
                .collect { result ->
                    if (result.isSuccess) {
                        getProducts() // Refresh the product list
                    } else {
                        _productsState.value = UiState.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Failed to update product"
                        )
                    }
                }
        }
    }

    fun getFirstAdded(){
        viewModelScope.launch {
            _firstAddedState.value = UiState.Loading
            getFirstAddedUseCase.execute()
                .collect { result ->
                    _firstAddedState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { firstAdded ->
                                _firstAdded.value = firstAdded
                                UiState.Success(firstAdded)
                            } ?: UiState.Error("FirstAdded not found")
                        }
                        result.isFailure -> UiState.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Failed to load firstAdded"
                        )
                        else -> UiState.Idle
                    }
                }
        }
    }

    fun updateFirstAdded(newFirstAdded: Boolean){
        viewModelScope.launch {
            _firstAddedState.value = UiState.Loading

            updateFirstAddedUseCase.execute(newFirstAdded)
                .catch { exception ->
                    _firstAddedState.value = UiState.Error(
                        exception.localizedMessage ?: "Failed to update cash"
                    )
                }
                .collect { result ->
                    _firstAddedState.value = when {
                        result.isSuccess -> {
                            result.getOrNull()?.let { firstAdded ->
                                _firstAdded.value = firstAdded
                                UiState.Success(firstAdded)
                            } ?: UiState.Error("Failed to update cash")
                        }
                        result.isFailure -> UiState.Error(
                            result.exceptionOrNull()?.localizedMessage ?: "Failed to update cash"
                        )
                        else -> UiState.Idle
                    }
                }
        }
    }

}