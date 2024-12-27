package com.unimib.ignitionfinance.domain.usecase

import android.content.Context
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.worker.SyncOperationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class UpdateUserSettingsUseCase @Inject constructor(
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    @ApplicationContext private val context: Context
) {
    fun execute(userId: String, updatedSettings: Settings): Flow<Result<Unit?>> = flow {
        try {
            coroutineScope {
                val currentUser = localDatabaseRepository.getById(userId).first().getOrNull()
                    ?: throw IllegalStateException("User not found in local database")

                val currentTime = System.currentTimeMillis()
                val updatedUser = currentUser.copy(
                    settings = updatedSettings,
                    updatedAt = currentTime
                )

                val localUpdateDeferred = async {
                    localDatabaseRepository.update(updatedUser).first()
                }

                val syncQueueItem = createSyncQueueItem(updatedUser)
                val syncQueueDeferred = async {
                    syncQueueItemRepository.insert(syncQueueItem)
                }

                val localResult = localUpdateDeferred.await()
                syncQueueDeferred.await()

                withContext(Dispatchers.IO) {
                    SyncOperationScheduler.scheduleOneTime<User>(context)
                }

                emit(Result.success(localResult.getOrNull()))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun createSyncQueueItem(user: User): SyncQueueItem {
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