package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.model.user.Product
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.cancellation.CancellationException

class GetUserInvestedUseCase @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase
) {
    fun execute(): Flow<Result<Double>> = flow {
        try {
            val productListResult = getProductListUseCase.execute().first()
            val productList = productListResult.getOrNull()
                ?: throw IllegalStateException("Failed to get product list")

            var totalAmount = 0.0
            productList.forEach { product ->
                val amount = product.amount.toDoubleOrNull() ?: 0.0
                totalAmount += amount
            }

            emit(Result.success(totalAmount))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}