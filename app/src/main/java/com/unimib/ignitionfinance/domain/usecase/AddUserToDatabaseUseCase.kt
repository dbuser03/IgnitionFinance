package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.remote.mapper.UserMapper
import com.unimib.ignitionfinance.data.repository.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.LocalDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddUserToDatabaseUseCase @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val userMapper: UserMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<UserData>
) {
    suspend fun execute(collectionPath: String, userData: UserData): Flow<Result<String?>> = flow {
        val documentId = userData.authData.id
        val dataMap = userMapper.mapUserToDocument(userData)

        // Iniziamo l'operazione su Firestore
        val firestoreFlow = firestoreRepository.addDocument(collectionPath, dataMap, documentId)

        // Raccogliamo il risultato di Firestore
        firestoreFlow.collect { firestoreResult ->
            if (firestoreResult.isSuccess) {
                // Se Firestore ha avuto successo, salviamo l'utente nel DB locale
                val localSaveFlow = localDatabaseRepository.add(userData)

                // Emettiamo il risultato dell'inserimento nel DB locale
                emitAll(localSaveFlow)
            } else {
                // Se Firestore fallisce, emettiamo il risultato di fallimento
                emit(firestoreResult)
            }
        }
    }
}
