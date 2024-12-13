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
    fun execute(collectionPath: String, userData: UserData): Flow<Result<Pair<String?, String?>>> = flow {
        val documentId = userData.authData.id
        val user = userMapper.mapUserDataToUser(userData)
        var remoteResult: Result<String?> = Result.failure(Throwable("Remote operation not executed"))
        var localResult: Result<String?> = Result.failure(Throwable("Local operation not executed"))

        try {
            val dataMap = userDataMapper.mapUserDataToDocument(userData)

            localDatabaseRepository.add(user).collect { result ->
                localResult = if (result.isSuccess) {
                    Result.success(documentId)
                } else {
                    Result.failure(result.exceptionOrNull() ?: Throwable("Errore sconosciuto nel database locale"))
                }
            }

            firestoreRepository.addDocument(collectionPath, dataMap, documentId).collect { result ->
                remoteResult = if (result.isSuccess) {
                    Result.success(documentId)
                } else {
                    Result.failure(result.exceptionOrNull() ?: Throwable("Errore sconosciuto nel database remoto"))
                }
            }


            emit(Result.success(Pair(remoteResult.getOrNull(), localResult.getOrNull())))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

