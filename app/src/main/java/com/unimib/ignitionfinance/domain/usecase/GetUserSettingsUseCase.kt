package com.unimib.ignitionfinance.domain.usecase

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
        val currentUserResult = authRepository.getCurrentUser().first()
        val authData = currentUserResult.getOrNull()
            ?: throw IllegalStateException("Failed to get current user")

        val userId = authData.id.takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException("User ID is missing")

        val localUser = localDatabaseRepository.getById(userId).first().getOrNull()
            ?: throw IllegalStateException("User not found in local database")

        val remoteUser = try {
            firestoreRepository.getDocumentById("users", userId)
                .firstOrNull()
                ?.getOrNull()
                ?.let { UserDataMapper.mapDocumentToUserData(it) }
        } catch (e: Exception) {
            Log.e("GetUserSettingsUseCase", "Error fetching remote user: ${e.message}", e)
            null
        }

        val settingsToEmit = when {
            remoteUser != null && remoteUser.updatedAt > localUser.updatedAt -> {
                val updatedLocalUser = localUser.copy(
                    settings = remoteUser.settings,
                    updatedAt = remoteUser.updatedAt,
                    name = remoteUser.name,
                    surname = remoteUser.surname,
                    authData = remoteUser.authData
                )
                localDatabaseRepository.update(updatedLocalUser).first()
                remoteUser.settings
            }
            else -> localUser.settings
        }

        emit(Result.success(settingsToEmit))
    }.catch { e ->
        when (e) {
            is CancellationException -> throw e
            else -> {
                Log.e("GetUserSettingsUseCase", "Error in execute: ${e.message}", e)
                emit(Result.failure(e))
            }
        }
    }
}