package com.unimib.ignitionfinance.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.unimib.ignitionfinance.data.calculator.DailyReturnCalculator
import com.unimib.ignitionfinance.data.repository.interfaces.StockRepository
import com.unimib.ignitionfinance.domain.validation.DatasetValidator
import com.unimib.ignitionfinance.domain.validation.DatasetValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BuildWeightedDatasetUseCase @Inject constructor(
    private val fetchHistoricalDataUseCase: FetchHistoricalDataUseCase,
    private val stockRepository: StockRepository,
    private val dailyReturnCalculator: DailyReturnCalculator
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun execute(apiKey: String): Flow<Result<Unit>> = flow {
        try {
            // Step 1: Fetch historical data
            val historicalDataResult = fetchHistoricalDataUseCase.execute(apiKey).first()

            val historicalData = historicalDataResult.getOrElse {
                emit(Result.failure(it))
                return@flow
            }

            // Step 2: Validate the dataset
            val validationResult = DatasetValidator.validate(historicalData)

            when (validationResult) {
                is DatasetValidationResult.Success -> {
                    // Step 3: Calculate daily returns
                    val dailyReturns = dailyReturnCalculator.calculateDailyReturns(historicalData, emptyMap())

                    emit(Result.success(Unit))
                }

                is DatasetValidationResult.Failure -> {
                    // Step 4: Fetch S&P 500 historical data if validation fails
                    val sp500DataResult = stockRepository.fetchStockData("SPX", apiKey).first()

                    val sp500Data = sp500DataResult.getOrElse {
                        emit(Result.failure(it))
                        return@flow
                    }

                    // Step 5: Calculate daily returns for S&P 500
                    val dailyReturns = dailyReturnCalculator.calculateDailyReturns(mapOf("SPX" to sp500Data), emptyMap())

                    emit(Result.success(Unit))
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}