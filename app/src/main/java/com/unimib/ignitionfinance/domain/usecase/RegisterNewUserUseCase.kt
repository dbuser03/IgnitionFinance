package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.repository.AuthRepository
import com.unimib.ignitionfinance.data.model.AuthData
import kotlinx.coroutines.flow.Flow

class RegisterNewUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String): Flow<Result<AuthData>> {
        return authRepository.createUserWithEmailAndPassword(email, password)
    }
}
