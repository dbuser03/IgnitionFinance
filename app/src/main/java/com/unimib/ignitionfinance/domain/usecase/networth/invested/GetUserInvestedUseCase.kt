package com.unimib.ignitionfinance.domain.usecase.networth.invested

import android.util.Log
import com.unimib.ignitionfinance.BuildConfig
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.cancellation.CancellationException

class GetUserInvestedUseCase @Inject constructor(
    private val getProductListUseCase: GetProductListUseCase
) {
    fun execute(): Flow<Result<Double>> = flow {
        Log.d(TAG, "Starting execution of GetUserInvestedUseCase")

        val productListResult = getProductListUseCase.execute(BuildConfig.ALPHAVANTAGE_API_KEY).first()
        Log.d(TAG, "Product list result received")

        val productList = productListResult.getOrNull()
            ?: throw IllegalStateException("Failed to get product list").also {
                Log.e(TAG, "Failed to get product list")
            }

        var totalAmount = 0.0
        productList.forEach { product ->
            val amount = product.amount.toDoubleOrNull() ?: 0.0
            totalAmount += amount
            Log.d(TAG, "Added amount $amount from product ${product.ticker}, running total: $totalAmount")
        }

        Log.d(TAG, "Emitting total invested amount: $totalAmount")
        emit(Result.success(totalAmount))
    }.catch { e ->
        when (e) {
            is CancellationException -> throw e.also {
                Log.e(TAG, "Flow cancelled: ${e.message}")
            }
            else -> {
                Log.e(TAG, "Error in execute: ${e.message}", e)
                emit(Result.failure(e))
            }
        }
    }

    companion object {
        private const val TAG = "GetUserInvestedUseCase"
    }
}
