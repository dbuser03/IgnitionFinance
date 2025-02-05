package com.unimib.ignitionfinance.domain.usecase.simulation

import android.util.Log
import com.unimib.ignitionfinance.data.local.entity.User
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
    private val localDatabaseRepository: LocalDatabaseRepository<User>
) {
    fun execute(dataset: List<DailyReturn>): Flow<Result<Unit>> = flow {
        try {
            Log.d("SaveDatasetUseCase", "Inizio salvataggio dataset locale")

            val currentUserResult = authRepository.getCurrentUser().first()
            val authData = currentUserResult.getOrNull()
                ?: throw IllegalStateException("Impossibile ottenere l'utente autenticato")

            val userId = authData.id.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("L'ID dell'utente risulta vuoto")

            val localUserResult = localDatabaseRepository.getById(userId).first()
            val user = localUserResult.getOrNull()
                ?: throw IllegalStateException("Utente non trovato nel database locale per l'ID: $userId")

            // Aggiorna solo il campo dataset mantenendo gli altri dati invariati
            val updatedUser = user.copy(
                dataset = user.dataset.toMutableList().apply {
                    clear() // Rimuove i vecchi dati
                    addAll(dataset) // Aggiunge i nuovi dati
                },
                updatedAt = System.currentTimeMillis()
            )

            localDatabaseRepository.update(updatedUser).first().getOrThrow()
            Log.d("SaveDatasetUseCase", "Salvataggio completato per l'utente: ${updatedUser.id}")

            emit(Result.success(Unit))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("SaveDatasetUseCase", "Errore durante il salvataggio del dataset: ${e.message}")
            emit(Result.failure(e))
        }
    }
}
