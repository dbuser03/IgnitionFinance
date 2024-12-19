package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddUserToDatabaseUseCase @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val syncQueueItemRepository: SyncQueueItemRepository // Aggiunta per gestire la coda di sincronizzazione
) {
    fun execute(collectionPath: String, user: User): Flow<Result<Pair<String?, Unit?>>> = flow {

        // Salva l'utente nel database locale
        val localResult = localDatabaseRepository.add(user).first()

        // Mappa l'utente a UserData e crea il documento
        val userData = userMapper.mapUserToUserData(user)
        val document = userDataMapper.mapUserDataToDocument(userData)
        val documentId = userData.authData.id

        // Crea un SyncQueueItem per l'operazione di aggiunta
        val syncQueueItem = SyncQueueItem(
            id = documentId,
            collection = collectionPath,
            payload = document,
            operationType = "ADD",
            status = SyncStatus.PENDING
        )

        // Aggiungi l'operazione alla coda di sincronizzazione
        syncQueueItemRepository.insert(syncQueueItem)

        // Emmetti il risultato, indicando che l'operazione è stata messa in coda
        emit(Result.success(Pair(null, localResult.getOrNull())))
    }
}
