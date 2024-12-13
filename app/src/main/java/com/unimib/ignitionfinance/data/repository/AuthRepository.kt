package com.unimib.ignitionfinance.data.repository

import com.unimib.ignitionfinance.data.model.AuthData
import com.unimib.ignitionfinance.data.remote.mapper.AuthMapper
import com.unimib.ignitionfinance.data.remote.service.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface AuthRepository {
    suspend fun signInWithEmailAndPassword(email: String, password: String): Flow<Result<AuthData>>
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Flow<Result<AuthData>>
    suspend fun resetPassword(email: String): Flow<Result<Unit>>
    suspend fun signOut(): Flow<Result<Unit>>
    suspend fun getCurrentUser(): Flow<Result<AuthData>>
}

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val authMapper: AuthMapper
) : AuthRepository {

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Flow<Result<AuthData>> = flow {
        try {
            val firebaseUser = authService.signInWithEmailAndPassword(email, password)
            val authData = firebaseUser?.let { authMapper.mapToUser(it) }
            if (authData != null) {
                emit(Result.success(authData))
            } else {
                emit(Result.failure(Throwable("Error: Failed to sign in user")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun createUserWithEmailAndPassword(email: String, password: String): Flow<Result<AuthData>> = flow {
        try {
            val firebaseUser = authService.createUserWithEmailAndPassword(email, password)
            val authData = firebaseUser?.let { authMapper.mapToUser(it) }
            if (authData != null) {
                emit(Result.success(authData))
            } else {
                emit(Result.failure(Throwable("Error: Failed to create user")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun resetPassword(email: String): Flow<Result<Unit>> = flow {
        try {
            authService.resetPassword(email)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun signOut(): Flow<Result<Unit>> = flow {
        try {
            authService.signOut()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getCurrentUser(): Flow<Result<AuthData>> = flow {
        try {
            val firebaseUser = authService.getCurrentUser()
            if (firebaseUser != null) {
                val authData = authMapper.mapToUser(firebaseUser)
                emit(Result.success(authData))
            } else {
                emit(Result.failure(Throwable("Error: User not logged in")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

}
