package com.unimib.ignitionfinance.domain.usecase

import com.google.firebase.firestore.DocumentSnapshot
import com.unimib.ignitionfinance.data.remote.mapper.UserMapper
import com.unimib.ignitionfinance.data.repository.FirestoreRepository
import com.unimib.ignitionfinance.data.model.Settings
import com.unimib.ignitionfinance.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RetrieveUserSettingsUseCase @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository,
    private val userMapper: UserMapper
) {
    fun execute(): Flow<Result<Settings>> = flow {
        try {
            val userId = authRepository.getCurrentUser()
            val documentFlow = firestoreRepository.getDocumentById("users", userId.toString())
            documentFlow.collect { result ->
                result.onSuccess { documentSnapshot ->
                    val userData = userMapper.mapToUserData(documentSnapshot as DocumentSnapshot?)
                    userData?.let {
                        emit(Result.success(it.settings))
                    } ?: emit(Result.failure(NullPointerException("User settings could not be mapped")))
                }
                result.onFailure { exception ->
                    emit(Result.failure(exception))
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
