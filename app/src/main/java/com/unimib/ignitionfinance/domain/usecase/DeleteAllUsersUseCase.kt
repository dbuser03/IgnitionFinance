package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeleteAllUsersUseCase @Inject constructor(
    private val userRepository: LocalDatabaseRepository<User>,
    private val firestoreRepository: FirestoreRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(): Flow<Result<Unit>> {
        return userRepository.deleteAll()
            .flatMapConcat { localResult ->
                if (localResult.isSuccess) {
                    firestoreRepository.deleteAllDocuments("users")
                } else {
                    flowOf(Result.failure(localResult.exceptionOrNull()!!))
                }
            }
            .map { remoteResult ->
                remoteResult.fold(
                    onSuccess = { Result.success(Unit) },
                    onFailure = { Result.failure(it) }
                )
            }
            .catch { e ->
                emit(Result.failure(e))
            }
    }
}

