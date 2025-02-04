package com.unimib.ignitionfinance.domain.usecase

import android.content.Context
import android.util.Log
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.worker.SyncOperationScheduler
import com.unimib.ignitionfinance.data.model.user.DailyReturn
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class SaveDatasetUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    @ApplicationContext private val context: Context
) {
    fun execute(dataset: List<DailyReturn>): Flow<Result<Unit?>> = flow {
        try {
            Log.d("SaveDatasetUseCase", "Starting dataset save...")

            // Get current user
            val currentUserResult = authRepository.getCurrentUser().first()
            val authData = currentUserResult.getOrNull()
                ?: throw IllegalStateException("Failed to get current user")
            Log.d("SaveDatasetUseCase", "Auth data received: ${authData.id}")

            val userId = authData.id.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("User ID is missing")
            Log.d("SaveDatasetUseCase", "User ID: $userId")

            val currentUser = localDatabaseRepository.getById(userId).first().getOrNull()
                ?: throw IllegalStateException("User not found in local database for ID: $userId")
            Log.d("SaveDatasetUseCase", "User found in local DB: ${currentUser.id}")

            // Update user's dataset and timestamp
            val updatedUser = currentUser.copy(
                dataset = dataset,
                updatedAt = System.currentTimeMillis()
            )
            Log.d("SaveDatasetUseCase", "Updated user dataset size: ${updatedUser.dataset.size}")

            // Update local database
            localDatabaseRepository.update(updatedUser).first()
            Log.d("SaveDatasetUseCase", "Local DB update completed for user: ${updatedUser.id}")

            // Create sync queue item
            val syncQueueItem = createSyncQueueItem(updatedUser)
            syncQueueItemRepository.insert(syncQueueItem)
            Log.d("SaveDatasetUseCase", "SyncQueueItem inserted: ${syncQueueItem.id}, status: ${syncQueueItem.status}")

            // Schedule sync operation
            withContext(Dispatchers.IO) {
                SyncOperationScheduler.scheduleOneTime<User>(context)
                Log.d("SaveDatasetUseCase", "Sync operation scheduled for user: ${updatedUser.id}")
            }

            emit(Result.success(Unit))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("SaveDatasetUseCase", "Error saving dataset: ${e.message}")
            emit(Result.failure(e))
        }
    }

    private fun createSyncQueueItem(user: User): SyncQueueItem {
        val userData = userMapper.mapUserToUserData(user)
        val document = userDataMapper.mapUserDataToDocument(userData)
        Log.d("SaveDatasetUseCase", "Creating SyncQueueItem for user: ${user.id}")
        return SyncQueueItem(
            id = user.id,
            collection = "users",
            payload = document,
            operationType = "UPDATE",
            status = SyncStatus.PENDING
        )
    }
}
