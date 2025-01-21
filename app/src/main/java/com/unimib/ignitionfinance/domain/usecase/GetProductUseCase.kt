// ricordarsi di modificare il productid al posto del TICKER con il simbolo
package com.unimib.ignitionfinance.domain.usecase

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetProductUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val firestoreRepository: FirestoreRepository,
    private val userDataMapper: UserDataMapper
) {
    fun execute(productId: String): Flow<Result<Product?>> = flow {
        try {
            val currentUserResult = authRepository.getCurrentUser().first()
            val authData = currentUserResult.getOrNull()
                ?: throw IllegalStateException("Failed to get current user")

            val userId = authData.id.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("User ID is missing")

            val localUser = localDatabaseRepository.getById(userId).first().getOrNull()
                ?: throw IllegalStateException("User not found in local database")

            val remoteUserResult = firestoreRepository.getDocumentById("users", userId).firstOrNull()
            val remoteUser = remoteUserResult?.getOrNull()?.let { userDataMapper.mapDocumentToUserData(it) }

            val product = when {
                remoteUser != null && remoteUser.updatedAt > localUser.updatedAt -> {
                    val updatedLocalUser = localUser.copy(
                        productList = remoteUser.productList,
                        updatedAt = remoteUser.updatedAt
                    )
                    localDatabaseRepository.update(updatedLocalUser).first()
                    remoteUser.productList.find { it.ticker == productId }
                }
                else -> localUser.productList.find { it.ticker == productId }
            }

            emit(Result.success(product))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}