package com.unimib.ignitionfinance.domain.usecase

import android.content.Context
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
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
    private val firestoreRepository: FirestoreRepository,
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

    fun executeExistingUser(collectionPath: String, id: String): Flow<Result<Unit?>> = flow {
        try {
            coroutineScope {
                val remoteUserDeferred = async {
                    firestoreRepository.getDocumentById(id, collectionPath).first().getOrNull()
                }

                val localUserDeferred = async {
                    localDatabaseRepository.getById(id).first()
                }

                val remoteUser = remoteUserDeferred.await()
                val localUser = localUserDeferred.await()

                if (remoteUser != null) {
                    val updatedUserData = UserDataMapper.mapDocumentToUserData(remoteUser)
                    val updatedUser = UserMapper.mapUserDataToUser(updatedUserData)

                    if (localUser.isSuccess) {
                        localDatabaseRepository.update(updatedUser).first()
                    } else {
                        localDatabaseRepository.add(updatedUser).first()
                    }

                    emit(Result.success(Unit))
                } else {
                    emit(Result.failure(Exception("User not found in Firestore")))
                }
            }
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