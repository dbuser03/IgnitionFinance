package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.repository.AuthRepository

data class RegisterNewUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) {
        repository.createUserWithEmailAndPassword(email, password)
    }
}

