package com.unimib.ignitionfinance.data.worker

sealed class SyncOperationResult {
    data class Success(val itemId: String) : SyncOperationResult()
    data class Retry(val itemId: String, val attempts: Int) : SyncOperationResult()
    data class Error(val itemId: String, val error: Throwable) : SyncOperationResult()
}