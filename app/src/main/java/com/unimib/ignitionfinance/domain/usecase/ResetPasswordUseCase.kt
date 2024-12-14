package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import kotlinx.coroutines.flow.Flow

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String): Flow<Result<Unit>> {
        return authRepository.resetPassword(email)
    }

}