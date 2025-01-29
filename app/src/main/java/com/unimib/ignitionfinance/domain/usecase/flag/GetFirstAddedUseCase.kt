package com.unimib.ignitionfinance.domain.usecase.flag

import android.util.Log
import com.unimib.ignitionfinance.data.local.entity.User
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

class GetFirstAddedUseCase @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val firestoreRepository: FirestoreRepository,
    private val userDataMapper: UserDataMapper
) {
    fun execute(forceRefresh: Boolean = false): Flow<Result<Boolean>> = flow {
        Log.d(TAG, "Starting execution of GetFirstAddedUseCase")

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
                    ?.let { userDataMapper.mapDocumentToUserData(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching remote user: ${e.message}", e)
                null
            }
        } else {
            Log.d(TAG, "Device is offline, skipping remote fetch")
            null
        }

        if (remoteUser == null) {
            Log.d(TAG, "No remote user data found, proceeding with local firstAdded flag")
        } else {
            Log.d(TAG, "Remote user retrieved: $remoteUser")
        }

        val firstAdded = when {
            remoteUser != null &&
                    (remoteUser.updatedAt.toLong() >= (localUser.lastSyncTimestamp?.toLong() ?: 0)) &&
                    (remoteUser.updatedAt.toLong() >= localUser.updatedAt.toLong()) -> {
                Log.d(TAG, "Remote user is newer, updating local database")
                val updatedLocalUser = localUser.copy(
                    firstAdded = remoteUser.firstAdded,
                    updatedAt = remoteUser.updatedAt,
                    lastSyncTimestamp = System.currentTimeMillis()
                )
                localDatabaseRepository.update(updatedLocalUser).first()
                Log.d(TAG, "Local database updated with remote user data")
                remoteUser.firstAdded
            }
            else -> {
                Log.d(TAG, "Using local user firstAdded flag")
                localUser.firstAdded
            }
        }

        Log.d(TAG, "Emitting firstAdded flag: $firstAdded")
        emit(Result.success(firstAdded))
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
        private const val TAG = "GetFirstAddedUseCase"
    }
}