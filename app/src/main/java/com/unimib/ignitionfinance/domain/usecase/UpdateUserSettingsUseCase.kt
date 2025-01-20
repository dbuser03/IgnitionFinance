package com.unimib.ignitionfinance.domain.usecase

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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

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
            val currentUserResult = authRepository.getCurrentUser().first()

            currentUserResult.onSuccess { authData ->
                val userId = authData.id
                if (userId.isEmpty()) {
                    throw IllegalStateException("User ID is missing")
                }

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
                    val syncQueueDeferred = async {
                        val syncQueueItem = createSyncQueueItem(updatedUser)
                        syncQueueItemRepository.insert(syncQueueItem)
                    }

                    localUpdateDeferred.await()
                    syncQueueDeferred.await()

                    val updatedSettingsResult = getUserSettingsUseCase.execute().first()
                    updatedSettingsResult.onSuccess { settings ->
                        withContext(Dispatchers.IO) {
                            SyncOperationScheduler.scheduleOneTime<User>(context)
                        }
                        emit(Result.success(settings))
                    }.onFailure { exception ->
                        emit(Result.failure(exception))
                    }
                }
            }

            currentUserResult.onFailure { exception ->
                emit(Result.failure(exception))
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