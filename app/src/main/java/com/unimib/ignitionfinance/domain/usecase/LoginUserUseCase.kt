package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.model.AuthData
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String): Flow<Result<AuthData>> {
        return authRepository.signInWithEmailAndPassword(email, password)
    }
}