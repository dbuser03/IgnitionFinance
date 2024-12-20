package com.unimib.ignitionfinance.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.*

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    private val firestoreRepository: FirestoreRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            cleanupStuckSyncingItems()

            val pendingItems = syncQueueItemRepository.getByStatus(SyncStatus.PENDING)

            if (pendingItems.isEmpty()) {
                return@coroutineScope Result.success()
            }

            val results = processBatches(pendingItems)

            return@coroutineScope when {
                results.any { it is SyncOperationResult.Error } -> Result.retry()
                else -> Result.success()
            }
        } catch (e: Exception) {
            handleWorkerError(e)
        }
    }

    private suspend fun cleanupStuckSyncingItems() {
        val stuckSyncingItems = syncQueueItemRepository.getByStatus(SyncStatus.SYNCING)
        val currentTime = System.currentTimeMillis()
        stuckSyncingItems.forEach { item ->
            if (currentTime - item.createdAt > SyncOperationScheduler.SYNC_TIMEOUT_MS) {
                syncQueueItemRepository.updateStatusAndIncrementAttempts(
                    item.id,
                    SyncStatus.ABANDONED
                )
            }
        }
    }

    private suspend fun processBatches(items: List<SyncQueueItem>): List<SyncOperationResult> =
        items.chunked(SyncOperationScheduler.BATCH_SIZE).flatMap { batch ->
            batch.map { item ->
                processItem(item)
            }.also {
                delay(SyncOperationScheduler.BATCH_DELAY_MS)
            }
        }

    private suspend fun processItem(item: SyncQueueItem): SyncOperationResult {
        syncQueueItemRepository.updateStatusAndIncrementAttempts(
            item.id,
            SyncStatus.SYNCING
        )

        return try {
            val result = when (item.operationType) {
                "ADD" -> performAddOperation(item)
                "UPDATE" -> performUpdateOperation(item)
                "DELETE" -> performDeleteOperation(item)
                else -> throw IllegalArgumentException("Unknown operation type: ${item.operationType}")
            }

            syncQueueItemRepository.delete(item)
            result
        } catch (e: Exception) {
            handleItemError(item, e)
        }
    }

    private suspend fun performAddOperation(item: SyncQueueItem): SyncOperationResult {
        val addResult = firestoreRepository.addDocument(
            collectionPath = item.collection,
            data = item.payload,
            documentId = item.id
        ).first()

        return addResult.fold(
            onSuccess = {
                SyncOperationResult.Success(item.id)
            },
            onFailure = {
                throw it
            }
        )
    }

    private suspend fun performUpdateOperation(item: SyncQueueItem): SyncOperationResult {
        val updateResult = firestoreRepository.updateDocument(
            collectionPath = item.collection,
            data = item.payload,
            documentId = item.id
        ).first()

        return updateResult.fold(
            onSuccess = {
                SyncOperationResult.Success(item.id)
            },
            onFailure = {
                throw it
            }
        )
    }

    private suspend fun performDeleteOperation(item: SyncQueueItem): SyncOperationResult {
        val deleteResult = firestoreRepository.deleteDocument(
            collectionPath = item.collection,
            documentId = item.id
        ).first()

        return deleteResult.fold(
            onSuccess = {
                SyncOperationResult.Success(item.id)
            },
            onFailure = {
                throw it
            }
        )
    }

    private suspend fun handleItemError(item: SyncQueueItem, error: Throwable): SyncOperationResult {
        return when {
            item.attempts < SyncOperationScheduler.MAX_RETRIES -> {
                syncQueueItemRepository.updateStatusAndIncrementAttempts(
                    item.id,
                    SyncStatus.PENDING
                )
                SyncOperationResult.Retry(item.id, item.attempts + 1)
            }
            else -> {
                syncQueueItemRepository.updateStatusAndIncrementAttempts(
                    item.id,
                    SyncStatus.FAILED
                )
                SyncOperationResult.Error(item.id, error)
            }
        }
    }

    private fun handleWorkerError(error: Throwable): Result {
        Log.e("SyncWorker", "Sync worker error", error)

        return Result.retry()
    }
}