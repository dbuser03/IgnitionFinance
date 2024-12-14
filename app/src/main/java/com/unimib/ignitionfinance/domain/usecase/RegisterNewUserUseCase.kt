package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.model.user.AuthData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterNewUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun execute(email: String, password: String): Flow<Result<AuthData>> {
        return authRepository.createUserWithEmailAndPassword(email, password)
    }
}
