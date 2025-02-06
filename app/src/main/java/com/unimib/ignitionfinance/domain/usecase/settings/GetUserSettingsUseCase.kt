package com.unimib.ignitionfinance.domain.usecase.settings

import android.util.Log
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.remote.model.user.Settings
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.domain.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetUserSettingsUseCase @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val firestoreRepository: FirestoreRepository
) {
    fun execute(forceRefresh: Boolean = false): Flow<Result<Settings>> = flow {
        Log.d(TAG, "Starting execution of GetUserSettingsUseCase")

        val currentUserResult = authRepository.getCurrentUser().first()
        Log.d(TAG, "AuthRepository currentUserResult: $currentUserResult")

        val authData = currentUserResult.getOrNull()
            ?: throw IllegalStateException("Failed to get current user").also {
                Log.e(TAG, "Failed to get current user")
            }

        val userId = authData.id.takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException("User ID is missing").also {
                Log.e(TAG, "User ID is missing")
            }

        Log.d(TAG, "Retrieved User ID: $userId")

        val localUser = localDatabaseRepository.getById(userId).first().getOrNull()
            ?: throw IllegalStateException("User not found in local database").also {
                Log.e(TAG, "User not found in local database for ID: $userId")
            }

        Log.d(TAG, "Local user retrieved: $localUser")

        val isOnline = networkUtils.isNetworkAvailable()

        val remoteUser = if (isOnline || forceRefresh) {
            try {
                firestoreRepository.getDocumentById("users", userId)
                    .firstOrNull()
                    ?.getOrNull()
                    ?.let { UserDataMapper.mapDocumentToUserData(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching remote user: ${e.message}", e)
                null
            }
        } else {
            Log.d(TAG, "Device is offline, skipping remote fetch")
            null
        }

        if (remoteUser == null) {
            Log.d(TAG, "No remote user data found, proceeding with local settings")
        } else {
            Log.d(TAG, "Remote user retrieved: $remoteUser")
        }

        val settingsToEmit = when {
            remoteUser != null &&
                    (remoteUser.updatedAt >= (localUser.lastSyncTimestamp ?: 0)) &&
                    (remoteUser.updatedAt >= localUser.updatedAt) -> {
                Log.d(TAG, "Remote user is newer, updating local database")
                val updatedLocalUser = localUser.copy(
                    settings = remoteUser.settings,
                    updatedAt = remoteUser.updatedAt,
                    lastSyncTimestamp = System.currentTimeMillis()
                )
                localDatabaseRepository.update(updatedLocalUser).first()
                Log.d(TAG, "Local database updated with remote user data: $updatedLocalUser")
                remoteUser.settings
            }
            else -> {
                Log.d(TAG, "Using local user settings")
                localUser.settings
            }
        }

        Log.d(TAG, "Emitting settings: $settingsToEmit")
        emit(Result.success(settingsToEmit))
    }.catch { e ->
        when (e) {
            is CancellationException -> throw e.also {
                Log.e(TAG, "Flow cancelled: ${e.message}")
            }
            else -> {
                Log.e(TAG, "Error in execute: ${e.message}", e)
                emit(Result.failure(e))
            }
        }
    }

    companion object {
        private const val TAG = "GetUserSettingsUseCase"
    }
}