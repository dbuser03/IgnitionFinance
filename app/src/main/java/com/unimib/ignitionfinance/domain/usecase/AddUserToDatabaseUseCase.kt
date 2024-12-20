package com.unimib.ignitionfinance.domain.usecase

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
    fun execute(collectionPath: String, user: User): Flow<Result<Pair<String?, Unit?>>> = flow {

        val localResult = localDatabaseRepository.add(user).first()

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

        syncQueueItemRepository.insert(syncQueueItem)

        emit(Result.success(Pair(null, localResult.getOrNull())))
    }
}
