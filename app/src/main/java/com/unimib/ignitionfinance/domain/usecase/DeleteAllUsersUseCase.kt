package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteAllUsersUseCase @Inject constructor(
    private val userRepository: LocalDatabaseRepository<User>,
    private val firestoreRepository: FirestoreRepository
) {
    fun execute(): Flow<Result<Pair<Unit?, Unit?>>> = flow {
        val localResult = userRepository.deleteAll().first()
        val remoteResult = firestoreRepository.deleteAllDocuments("users").first()

        emit(Result.success(Pair(localResult.getOrNull(), remoteResult.getOrNull())))
    }
}