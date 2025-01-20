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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.delay

class UpdateUserCashUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    private val getUserCashUseCase: GetUserCashUseCase,
    @ApplicationContext private val context: Context
) {
    fun execute(updatedCash: String): Flow<Result<String?>> = flow {
        try {
            Log.d("UpdateUserCashUseCase", "Starting cash update with: $updatedCash")

            val currentUserResult = authRepository.getCurrentUser().first()
            val authData = currentUserResult.getOrNull()
                ?: throw IllegalStateException("Failed to get current user")

            val userId = authData.id.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("User ID is missing")

            val currentUser = localDatabaseRepository.getById(userId).first().getOrNull()
                ?: throw IllegalStateException("User not found in local database")

            Log.d("UpdateUserCashUseCase", "Current user found: $currentUser")

            val currentTime = System.currentTimeMillis()
            val updatedUser = currentUser.copy(
                cash = updatedCash,
                updatedAt = currentTime
            )
            Log.d("UpdateUserCashUseCase", "Updated User: $updatedUser")

            localDatabaseRepository.update(updatedUser).first()
            Log.d("UpdateUserCashUseCase", "Local database updated")

            val syncQueueItem = createSyncQueueItem(updatedUser)
            syncQueueItemRepository.insert(syncQueueItem)
            Log.d("UpdateUserCashUseCase", "Sync queue item inserted: ${syncQueueItem.payload}")

            withContext(Dispatchers.IO) {
                SyncOperationScheduler.scheduleOneTime<User>(context)
            }
            Log.d("UpdateUserCashUseCase", "Worker scheduled")

            delay(500)

            val cash = getUserCashUseCase.execute().first().getOrNull()
            Log.d("UpdateUserCashUseCase", "Final cash: $cash")

            emit(Result.success(cash))

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("UpdateUserCashUseCase", "Error during update: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

    private fun createSyncQueueItem(user: User): SyncQueueItem {
        Log.d("UpdateUserCashUseCase", "Creating sync queue item for user: ${user.id}")
        val userData = userMapper.mapUserToUserData(user)
        val document = userDataMapper.mapUserDataToDocument(userData)

        return SyncQueueItem(
            id = user.id,
            collection = "users",
            payload = document,
            operationType = "UPDATE",
            status = SyncStatus.PENDING
        )
    }
}