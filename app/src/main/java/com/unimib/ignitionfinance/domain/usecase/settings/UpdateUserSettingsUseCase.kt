package com.unimib.ignitionfinance.domain.usecase.settings

import android.content.Context
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.model.user.Settings
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
import android.util.Log
import kotlinx.coroutines.delay

class UpdateUserSettingsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    private val getUserSettingsUseCase: GetUserSettingsUseCase,
    @ApplicationContext private val context: Context
) {
    fun execute(updatedSettings: Settings): Flow<Result<Settings?>> = flow {
        try {
            Log.d("UpdateUserSettingsUseCase", "Starting settings update with: $updatedSettings")

            val currentUserResult = authRepository.getCurrentUser().first()
            val authData = currentUserResult.getOrNull()
                ?: throw IllegalStateException("Failed to get current user")

            val userId = authData.id.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("User ID is missing")

            val currentUser = localDatabaseRepository.getById(userId).first().getOrNull()
                ?: throw IllegalStateException("User not found in local database")

            Log.d("UpdateUserSettingsUseCase", "Current user found: $currentUser")

            val currentTime = System.currentTimeMillis()
            val updatedUser = currentUser.copy(
                settings = updatedSettings,
                updatedAt = currentTime
            )
            Log.d("UpdateUserSettingsUseCase", "Updated User: $updatedUser")

            localDatabaseRepository.update(updatedUser).first()
            Log.d("UpdateUserSettingsUseCase", "Local database updated")

            val syncQueueItem = createSyncQueueItem(updatedUser)
            syncQueueItemRepository.insert(syncQueueItem)
            Log.d("UpdateUserSettingsUseCase", "Sync queue item inserted: ${syncQueueItem.payload}")

            withContext(Dispatchers.IO) {
                SyncOperationScheduler.scheduleOneTime<User>(context)
            }
            Log.d("UpdateUserSettingsUseCase", "Worker scheduled")

            delay(500)

            val settings = getUserSettingsUseCase.execute().first().getOrNull()
            Log.d("UpdateUserSettingsUseCase", "Final settings: $settings")

            emit(Result.success(settings))

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("UpdateUserSettingsUseCase", "Error during update: ${e.message}", e)
            emit(Result.failure(e))
        }
    }

    private fun createSyncQueueItem(user: User): SyncQueueItem {
        Log.d("UpdateUserSettingsUseCase", "Creating sync queue item for user: ${user.id}")
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