package com.unimib.ignitionfinance.domain.usecase

import android.util.Log
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetFirstAddedUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>
) {
    fun execute(): Flow<Result<Boolean>> = flow {
        try {
            val currentUserResult = authRepository.getCurrentUser().first()
            val authData = currentUserResult.getOrNull()
                ?: throw IllegalStateException("Failed to get current user")

            val userId = authData.id.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("User ID is missing")

            val localUser = localDatabaseRepository.getById(userId).first().getOrNull()
                ?: throw IllegalStateException("User not found in local database")

            emit(Result.success(localUser.firstAdded))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("GetFirstAddedUseCase", "Error in execute: ${e.message}", e)
            emit(Result.failure(e))
        }
    }
}