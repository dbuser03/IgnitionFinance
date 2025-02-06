package com.unimib.ignitionfinance.data.repository.implementation

import android.util.Log
import com.unimib.ignitionfinance.data.remote.mapper.SearchStockMapper
import com.unimib.ignitionfinance.data.remote.service.SearchStockService
import com.unimib.ignitionfinance.data.remote.model.SearchStockData
import com.unimib.ignitionfinance.data.repository.interfaces.SearchStockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import com.google.gson.Gson
import com.google.gson.GsonBuilder

private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

class SearchStockRepositoryImpl @Inject constructor(
    private val searchStockService: SearchStockService,
    private val searchStockMapper: SearchStockMapper
) : SearchStockRepository {

    override suspend fun fetchSearchStockData(ticker: String, apiKey: String): Flow<Result<List<SearchStockData>>> = flow {
        try {
            Log.d("SearchStockRepository", "Fetching stock data for ticker: $ticker with API key: $apiKey")

            val response = searchStockService.getSearchStockData(ticker = ticker, apiKey = apiKey)

            Log.d("SearchStockRepository", "HTTP Response: ${response.code()} ${response.message()}")

            if (response.isSuccessful) {
                val searchStockData = response.body()

                val jsonResponse = gson.toJson(searchStockData)
                Log.d("SearchStockRepository", "API JSON Response:\n$jsonResponse")

                if (searchStockData?.bestMatches != null) {
                    Log.d("SearchStockRepository", "Response body: $searchStockData")
                    emit(Result.success(searchStockMapper.mapToDomain(searchStockData)))
                } else {
                    Log.e("SearchStockRepository", "Error: `bestMatches` is null in API response")
                    emit(Result.failure(Throwable("Error: `bestMatches` is null in API response for $ticker")))
                }
            } else {
                Log.e("SearchStockRepository", "API call failed with status: ${response.code()} - ${response.message()}")
                emit(Result.failure(Throwable("Failed to fetch search stock data: ${response.code()} - ${response.message()}")))
            }
        } catch (e: Exception) {
            Log.e("SearchStockRepository", "Exception during API call", e)
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}