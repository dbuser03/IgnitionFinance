package com.unimib.ignitionfinance.domain.usecase.settings

import android.util.Log
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.model.user.Settings
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
        Log.d("GetUserSettingsUseCase", "Starting execution of GetUserSettingsUseCase")

        val currentUserResult = authRepository.getCurrentUser().first()
        Log.d("GetUserSettingsUseCase", "AuthRepository currentUserResult: $currentUserResult")

        val authData = currentUserResult.getOrNull()
            ?: throw IllegalStateException("Failed to get current user").also {
                Log.e("GetUserSettingsUseCase", "Failed to get current user")
            }

        val userId = authData.id.takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException("User ID is missing").also {
                Log.e("GetUserSettingsUseCase", "User ID is missing")
            }

        Log.d("GetUserSettingsUseCase", "Retrieved User ID: $userId")

        val localUser = localDatabaseRepository.getById(userId).first().getOrNull()
            ?: throw IllegalStateException("User not found in local database").also {
                Log.e("GetUserSettingsUseCase", "User not found in local database for ID: $userId")
            }

        Log.d("GetUserSettingsUseCase", "Local user retrieved: $localUser")

        val remoteUser = try {
            firestoreRepository.getDocumentById("users", userId)
                .firstOrNull()
                ?.getOrNull()
                ?.let { UserDataMapper.mapDocumentToUserData(it) }
        } catch (e: Exception) {
            Log.e("GetUserSettingsUseCase", "Error fetching remote user (possibly offline): ${e.message}", e)
            null
        }

        if (remoteUser == null) {
            Log.d("GetUserSettingsUseCase", "No remote user data found, proceeding with local settings")
        } else {
            Log.d("GetUserSettingsUseCase", "Remote user retrieved: $remoteUser")
        }

        val settingsToEmit = when {
            remoteUser != null && remoteUser.updatedAt > localUser.updatedAt -> {
                Log.d("GetUserSettingsUseCase", "Remote user is newer, updating local database")
                val updatedLocalUser = localUser.copy(
                    settings = remoteUser.settings,
                    updatedAt = remoteUser.updatedAt,
                )
                localDatabaseRepository.update(updatedLocalUser).first()
                Log.d("GetUserSettingsUseCase", "Local database updated with remote user data: $updatedLocalUser")
                remoteUser.settings
            }
            else -> {
                Log.d("GetUserSettingsUseCase", "Using local user settings")
                localUser.settings
            }
        }

        Log.d("GetUserSettingsUseCase", "Emitting settings: $settingsToEmit")
        emit(Result.success(settingsToEmit))
    }.catch { e ->
        when (e) {
            is CancellationException -> throw e.also {
                Log.e("GetUserSettingsUseCase", "Flow cancelled: ${e.message}")
            }
            else -> {
                Log.e("GetUserSettingsUseCase", "Error in execute: ${e.message}", e)
                emit(Result.failure(e))
            }
        }
    }
}