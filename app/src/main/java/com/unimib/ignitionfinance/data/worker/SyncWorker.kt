package com.unimib.ignitionfinance.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.*

@HiltWorker
class SyncWorker<T> @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    private val firestoreRepository: FirestoreRepository,
    private val localRepository: LocalDatabaseRepository<T>
) : CoroutineWorker(context, workerParams) {
    companion object {
        private const val TAG = "SyncWorker"
    }

    override suspend fun doWork(): Result = coroutineScope {
        try {
            Log.d(TAG, "Starting sync work")
            cleanupStuckSyncingItems()

            val currentTime = System.currentTimeMillis()
            val pendingItems = syncQueueItemRepository.getPendingItems(currentTime)
            Log.d(TAG, "Found ${pendingItems.size} pending items ready for processing")

            if (pendingItems.isEmpty()) {
                Log.d(TAG, "No pending items, completing successfully")
                return@coroutineScope Result.success()
            }

            val results = processBatches(pendingItems)
            Log.d(TAG, "Processed ${results.size} items")

            val errorCount = results.count { it is SyncOperationResult.Error }
            val successCount = results.count { it is SyncOperationResult.Success }
            val retryCount = results.count { it is SyncOperationResult.Retry }

            Log.d(TAG, "Sync results - Success: $successCount, Errors: $errorCount, Retries: $retryCount")

            handleFailedItems()

            return@coroutineScope when {
                errorCount > 0 -> {
                    Log.w(TAG, "Some operations failed, scheduling retry")
                    Result.retry()
                }
                else -> {
                    Log.d(TAG, "All operations completed successfully")
                    Result.success()
                }
            }
        } catch (e: Exception) {
            handleWorkerError(e)
        }
    }

    private suspend fun cleanupStuckSyncingItems() {
        val stuckSyncingItems = syncQueueItemRepository.getByStatus(SyncStatus.SYNCING)
        Log.d(TAG, "Found ${stuckSyncingItems.size} stuck items")

        val currentTime = System.currentTimeMillis()
        stuckSyncingItems.forEach { item ->
            if (currentTime - item.createdAt > SyncOperationScheduler.SYNC_TIMEOUT_MS) {
                Log.w(TAG, "Item ${item.id} stuck in SYNCING state, marking as ABANDONED")
                val nextAttemptTime = calculateNextAttemptTime(item.attempts + 1)
                syncQueueItemRepository.updateStatusAndIncrementAttempts(
                    item.id,
                    SyncStatus.ABANDONED,
                    nextAttemptTime
                )
            }
        }
    }

    private suspend fun handleFailedItems() {
        val failedItems = syncQueueItemRepository.getFailedItems(SyncOperationScheduler.MAX_RETRIES)
        Log.d(TAG, "Found ${failedItems.size} permanently failed items")
    }

    private suspend fun processBatches(items: List<SyncQueueItem>): List<SyncOperationResult> {
        Log.d(TAG, "Processing ${items.size} items in batches of ${SyncOperationScheduler.BATCH_SIZE}")
        return items.chunked(SyncOperationScheduler.BATCH_SIZE).flatMap { batch ->
            Log.d(TAG, "Processing batch of ${batch.size} items")
            batch.map { item ->
                processItem(item).also { result ->
                    Log.d(TAG, "Item ${item.id} processed with result: $result")
                }
            }.also {
                delay(SyncOperationScheduler.BATCH_DELAY_MS)
            }
        }
    }

    private suspend fun processItem(item: SyncQueueItem): SyncOperationResult {
        Log.d(TAG, "Processing item ${item.id} of type ${item.operationType}")

        syncQueueItemRepository.updateStatusAndIncrementAttempts(
            item.id,
            SyncStatus.SYNCING,
            System.currentTimeMillis()
        )

        return try {
            val result = when (item.operationType) {
                "ADD" -> performAddOperation(item)
                "UPDATE" -> performUpdateOperation(item)
                "DELETE" -> performDeleteOperation(item)
                else -> throw IllegalArgumentException("Unknown operation type: ${item.operationType}")
            }

            Log.d(TAG, "Operation ${item.operationType} completed successfully for item ${item.id}")

            syncQueueItemRepository.updateStatusAndIncrementAttempts(
                item.id,
                SyncStatus.SUCCEEDED,
                System.currentTimeMillis()
            )

            localRepository.updateLastSyncTimestamp(item.id).first().fold(
                onSuccess = {
                    Log.d(TAG, "Updated last sync timestamp for entity ${item.id}")
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to update last sync timestamp for entity ${item.id}", error)
                }
            )

            syncQueueItemRepository.delete(item)
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error processing item ${item.id}", e)
            handleItemError(item, e)
        }
    }

    private suspend fun performAddOperation(item: SyncQueueItem): SyncOperationResult {
        Log.d(TAG, "Performing ADD operation for item ${item.id}")

        val addResult = firestoreRepository.addDocument(
            collectionPath = item.collection,
            data = item.payload,
            documentId = item.id
        ).first()

        return addResult.fold(
            onSuccess = {
                Log.d(TAG, "ADD operation successful for item ${item.id}")
                SyncOperationResult.Success(item.id)
            },
            onFailure = { error ->
                Log.e(TAG, "ADD operation failed for item ${item.id}", error)
                throw error
            }
        )
    }

    private suspend fun performUpdateOperation(item: SyncQueueItem): SyncOperationResult {
        Log.d(TAG, "Performing UPDATE operation for item ${item.id}")

        val updateResult = firestoreRepository.updateDocument(
            collectionPath = item.collection,
            data = item.payload,
            documentId = item.id
        ).first()

        return updateResult.fold(
            onSuccess = {
                Log.d(TAG, "UPDATE operation successful for item ${item.id}")
                SyncOperationResult.Success(item.id)
            },
            onFailure = { error ->
                Log.e(TAG, "UPDATE operation failed for item ${item.id}", error)
                throw error
            }
        )
    }

    private suspend fun performDeleteOperation(item: SyncQueueItem): SyncOperationResult {
        Log.d(TAG, "Performing DELETE operation for item ${item.id}")

        val deleteResult = firestoreRepository.deleteDocument(
            collectionPath = item.collection,
            documentId = item.id
        ).first()

        return deleteResult.fold(
            onSuccess = {
                Log.d(TAG, "DELETE operation successful for item ${item.id}")
                SyncOperationResult.Success(item.id)
            },
            onFailure = { error ->
                Log.e(TAG, "DELETE operation failed for item ${item.id}", error)
                throw error
            }
        )
    }

    private suspend fun handleItemError(item: SyncQueueItem, error: Throwable): SyncOperationResult {
        Log.e(TAG, "Error handling item ${item.id} (attempt ${item.attempts + 1}/${SyncOperationScheduler.MAX_RETRIES})", error)

        return when {
            item.attempts < SyncOperationScheduler.MAX_RETRIES -> {
                val nextAttemptTime = calculateNextAttemptTime(item.attempts + 1)
                Log.d(TAG, "Scheduling retry for item ${item.id} at $nextAttemptTime")
                syncQueueItemRepository.updateStatusAndIncrementAttempts(
                    item.id,
                    SyncStatus.PENDING,
                    nextAttemptTime
                )
                SyncOperationResult.Retry(item.id, item.attempts + 1)
            }
            else -> {
                Log.e(TAG, "Max retries exceeded for item ${item.id}, marking as FAILED")
                syncQueueItemRepository.updateStatusAndIncrementAttempts(
                    item.id,
                    SyncStatus.FAILED,
                    System.currentTimeMillis()
                )
                SyncOperationResult.Error(item.id, error)
            }
        }
    }

    private fun calculateNextAttemptTime(attempts: Int): Long {
        val backoffMs = SyncOperationScheduler.INITIAL_BACKOFF_DELAY_MS * (1 shl (attempts - 1))
        return System.currentTimeMillis() + backoffMs
    }

    private fun handleWorkerError(error: Throwable): Result {
        Log.e(TAG, "Critical worker error", error)
        return Result.retry()
    }
}