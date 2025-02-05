package com.unimib.ignitionfinance.domain.usecase.simulation

import android.content.Context
import android.util.Log
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.model.user.DailyReturn
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class SaveDatasetUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val userMapper: UserMapper,
    @ApplicationContext private val context: Context
) {
    fun execute(dataset: List<DailyReturn>): Flow<Result<Unit>> = flow {
        try {
            Log.d("SaveDatasetUseCase", "Starting dataset save...")

            // 1. Ottieni l'utente corrente dall'AuthRepository
            val currentUserResult = authRepository.getCurrentUser().first()
            val authData = currentUserResult.getOrNull()
                ?: throw IllegalStateException("Failed to get current user")
            Log.d("SaveDatasetUseCase", "Auth data received: ${authData.id}")

            // 2. Verifica che l'ID dell'utente sia presente
            val userId = authData.id.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("User ID is missing")
            Log.d("SaveDatasetUseCase", "User ID: $userId")

            // 3. Ottieni l'utente dal database locale
            val currentUser = localDatabaseRepository.getById(userId).first().getOrNull()
                ?: throw IllegalStateException("User not found in local database for ID: $userId")
            Log.d("SaveDatasetUseCase", "User found in local DB: ${currentUser.id}")

            // 4. Mappa l'entità User in UserData
            val currentUserData = userMapper.mapUserToUserData(currentUser)

            // 5. Aggiorna il dataset e il timestamp
            val updatedUserData = currentUserData.copy(
                dataset = dataset,
                updatedAt = System.currentTimeMillis()
            )
            Log.d("SaveDatasetUseCase", "Updated user dataset size: ${updatedUserData.dataset.size}")

            // 6. Mappa di nuovo in entità User
            val updatedUser = userMapper.mapUserDataToUser(updatedUserData)

            // 7. Aggiorna l'utente nel database locale
            localDatabaseRepository.update(updatedUser).first()
            Log.d("SaveDatasetUseCase", "Local DB update completed for user: ${updatedUser.id}")

            emit(Result.success(Unit))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("SaveDatasetUseCase", "Error saving dataset: ${e.message}")
            emit(Result.failure(e))
        }
    }
}
