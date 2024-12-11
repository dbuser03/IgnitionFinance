package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.remote.mapper.UserMapper
import com.unimib.ignitionfinance.data.repository.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.LocalDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddUserToDatabaseUseCase @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val userMapper: UserMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<User>
) {
    fun execute(collectionPath: String, userData: UserData): Flow<Result<String?>> = flow {
        val documentId = userData.authData.id
        val dataMap = userMapper.mapUserToDocument(userData)

        val firestoreFlow = firestoreRepository.addDocument(collectionPath, dataMap, documentId)

        firestoreFlow.collect { firestoreResult ->
            if (firestoreResult.isSuccess) {
                val localSaveFlow = localDatabaseRepository.add(user)

                localSaveFlow.collect { localSaveResult ->
                    if (localSaveResult.isSuccess) {
                        emit(Result.success(documentId))
                    } else {
                        emit(Result.failure(localSaveResult.exceptionOrNull() ?: Throwable("Unknown error in local database")))
                    }
                }
            } else {
                emit(firestoreResult)
            }
        }
    }
}


