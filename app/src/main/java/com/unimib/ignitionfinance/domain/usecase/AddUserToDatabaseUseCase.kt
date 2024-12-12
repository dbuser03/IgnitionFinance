package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.model.UserData
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.LocalDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddUserToDatabaseUseCase @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val localDatabaseRepository: LocalDatabaseRepository<User>
) {
    fun execute(collectionPath: String, userData: UserData): Flow<Result<String?>> = flow {
        val documentId = userData.authData.id
        val user = userMapper.mapUserDataToUser(userData)

        try {
            val localSaveResult = localDatabaseRepository.add(user)
            localSaveResult.collect { saveResult ->
                if (saveResult.isSuccess) {
                    val dataMap = userDataMapper.mapUserDataToDocument(userData)
                    val firestoreResult = firestoreRepository.addDocument(collectionPath, dataMap, documentId)
                    firestoreResult.collect { result ->
                        when {
                            result.isSuccess -> {
                                emit(Result.success(documentId))
                            }
                            result.isFailure -> {
                                emit(Result.failure(result.exceptionOrNull() ?: Throwable("Unknown error in remote database")))
                            }
                        }
                    }
                } else {
                    emit(Result.failure(saveResult.exceptionOrNull() ?: Throwable("Unknown error in local database")))
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}