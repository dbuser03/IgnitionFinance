package com.unimib.ignitionfinance.domain.usecase

import com.google.firebase.firestore.DocumentSnapshot
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RetrieveUserSettingsUseCase @Inject constructor(
    private val firestoreRepository: FirestoreRepository,
    private val authRepository: AuthRepository,
    private val userDataMapper: UserDataMapper
) {
    fun execute(): Flow<Result<Settings>> = flow {
        try {
            val userId = authRepository.getCurrentUser()
            val documentFlow = firestoreRepository.getDocumentById("users", userId.toString())

            documentFlow.collect { result ->
                result.onSuccess { documentSnapshot ->
                    val userData = userDataMapper.mapDocumentToUserData(documentSnapshot as DocumentSnapshot?)
                    if (userData != null) {
                        emit(Result.success(userData.settings))
                    } else {
                        emit(Result.failure(NullPointerException("User data could not be mapped to settings")))
                    }
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