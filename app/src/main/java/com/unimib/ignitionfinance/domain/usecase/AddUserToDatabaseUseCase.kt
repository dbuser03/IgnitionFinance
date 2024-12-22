package com.unimib.ignitionfinance.domain.usecase

import android.content.Context
import android.util.Log
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.worker.SyncOperationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddUserToDatabaseUseCase @Inject constructor(
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    @ApplicationContext private val context: Context
) {
    fun executeNewUser(collectionPath: String, user: User): Flow<Result<Unit?>> = flow {
        val syncQueueItem = createSyncQueueItem(user, collectionPath)

        try {
            coroutineScope {
                val localDbDeferred = async {
                    localDatabaseRepository.add(user).first()
                }

                val syncQueueDeferred = async {
                    syncQueueItemRepository.insert(syncQueueItem)
                }

                val localResult = localDbDeferred.await()
                syncQueueDeferred.await()

                withContext(Dispatchers.IO) {
                    SyncOperationScheduler.scheduleOneTime(context)
                }

                emit(Result.success(localResult.getOrNull()))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.catch { e ->
        if (e is CancellationException) throw e
        emit(Result.failure(e))
    }

    fun executeExistingUser(user: Map<String, Any>?): Flow<Result<Unit?>> = flow {
        try {

            val id = (user?.get("authData") as? Map<*, *>)?.get("id") as? String
                ?: throw IllegalArgumentException("User ID is missing or invalid")
            Log.d("LoginScreen", id)

            val localUser = localDatabaseRepository.getById(id).first()
            Log.d("LoginScreen", "$localUser")

            val updatedUserData = UserDataMapper.mapDocumentToUserData(user)
            Log.d("LoginScreen", "$updatedUserData")

            val updatedUser = UserMapper.mapUserDataToUser(updatedUserData)
            Log.d("LoginScreen", "$updatedUser")

            if (localUser.getOrNull() != null) {
                Log.d("LoginScreen", "Local user is success")
                localDatabaseRepository.update(updatedUser).first()
            } else {
                Log.d("LoginScreen", "Local user is failure")
                localDatabaseRepository.add(updatedUser).first()
            }

            emit(Result.success(Unit))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun createSyncQueueItem(user: User, collectionPath: String): SyncQueueItem {
        val userData = userMapper.mapUserToUserData(user)
        val document = userDataMapper.mapUserDataToDocument(userData)
        val documentId = userData.authData.id

        return SyncQueueItem(
            id = documentId,
            collection = collectionPath,
            payload = document,
            operationType = "ADD",
            status = SyncStatus.PENDING
        )
    }
}