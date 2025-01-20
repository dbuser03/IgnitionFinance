package com.unimib.ignitionfinance.domain.usecase

import android.util.Log
import com.google.gson.Gson
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetUserSettingsUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val firestoreRepository: FirestoreRepository
) {
    fun execute(): Flow<Result<Settings>> = flow {
        try {
            val currentUserResult = authRepository.getCurrentUser().first()

            currentUserResult.onSuccess { authData ->
                val userId = authData.id
                if (userId.isEmpty()) {
                    throw IllegalStateException("User ID is missing")
                }

                val localUser = localDatabaseRepository.getById(userId).first().getOrNull()
                    ?: throw IllegalStateException("User not found in local database")

                try {
                    val remoteUserResult =
                        firestoreRepository.getDocumentById("users", userId).firstOrNull()
                    val remoteUser = remoteUserResult?.getOrNull()
                    val jsonRemote = Gson().toJson(remoteUser)
                    Log.d("UserUpdate", "Remote User Data as JSON: $jsonRemote")

                    if (remoteUser != null) {
                        val remoteUserData = UserDataMapper.mapDocumentToUserData(remoteUser)

                        if (remoteUserData != null && remoteUserData.updatedAt > localUser.updatedAt) {

                            val updatedLocalUser = localUser.copy(
                                settings = remoteUserData.settings,
                                updatedAt = remoteUserData.updatedAt,
                                name = remoteUserData.name,
                                surname = remoteUserData.surname,
                                authData = remoteUserData.authData
                            )
                            val json = Gson().toJson(updatedLocalUser)
                            Log.d("UserUpdate", "Local User Data as JSON: $json")

                            localDatabaseRepository.update(updatedLocalUser).first()

                            emit(Result.success(remoteUserData.settings))
                        } else {
                            emit(Result.success(localUser.settings))
                        }
                    } else {
                        emit(Result.success(localUser.settings))
                    }
                } catch (_: Exception) {
                    emit(Result.success(localUser.settings))
                }
            }

            currentUserResult.onFailure { exception ->
                emit(Result.failure(exception))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}