package com.unimib.ignitionfinance.domain.usecase

import android.util.Log
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddUserToDatabaseUseCase @Inject constructor(
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val syncQueueItemRepository: SyncQueueItemRepository
) {
    suspend fun execute(collectionPath: String, user: User): Result<Unit> {
        return try {
            Log.d("AddUserUseCase", "Adding user ${user.id} to local database")

            // Execute operations sequentially
            val localResult = localDatabaseRepository.add(user).first()

            Log.d("AddUserUseCase", "Local database result: $localResult")

            // Only proceed if local operation was successful
            if (localResult.isSuccess) {
                Log.d("AddUserUseCase", "Creating sync queue item for user ${user.id}")
                val userData = userMapper.mapUserToUserData(user)
                val document = userDataMapper.mapUserDataToDocument(userData)
                val documentId = userData.authData.id

                val syncQueueItem = SyncQueueItem(
                    id = documentId,
                    collection = collectionPath,
                    payload = document,
                    operationType = "ADD",
                    status = SyncStatus.PENDING
                )

                Log.d("AddUserUseCase", "Inserting sync queue item: $syncQueueItem")
                syncQueueItemRepository.insert(syncQueueItem)
                Log.d("AddUserUseCase", "Sync queue item inserted successfully")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AddUserUseCase", "Error in execute", e)
            Result.failure(e)
        }
    }
}