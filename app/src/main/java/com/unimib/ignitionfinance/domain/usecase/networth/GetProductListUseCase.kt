package com.unimib.ignitionfinance.domain.usecase.networth

import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.domain.utils.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetProductListUseCase @Inject constructor(
    private val networkUtils: NetworkUtils,
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val firestoreRepository: FirestoreRepository,
    private val userDataMapper: UserDataMapper
) {
    fun execute(forceRefresh: Boolean = false): Flow<Result<List<Product>>> = flow {

        val currentUserResult = authRepository.getCurrentUser().first()

        val authData = currentUserResult.getOrNull()
            ?: throw IllegalStateException("Failed to get current user")
        val userId = authData.id.takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException("User ID is missing")

        val localUser = localDatabaseRepository.getById(userId).first().getOrNull()
            ?: throw IllegalStateException("User not found in local database")

        val isOnline = networkUtils.isNetworkAvailable()

        val remoteUser = if (isOnline || forceRefresh) {
            try {
                firestoreRepository.getDocumentById("users", userId)
                    .firstOrNull()
                    ?.getOrNull()
                    ?.let { userDataMapper.mapDocumentToUserData(it) }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

        val productList = when {
            remoteUser != null &&
                    (remoteUser.updatedAt.toLong() >= (localUser.lastSyncTimestamp?.toLong() ?: 0)) &&
                    (remoteUser.updatedAt.toLong() >= localUser.updatedAt.toLong()) -> {
                val updatedLocalUser = localUser.copy(
                    productList = remoteUser.productList,
                    updatedAt = remoteUser.updatedAt,
                    lastSyncTimestamp = System.currentTimeMillis()
                )
                localDatabaseRepository.update(updatedLocalUser).first()
                remoteUser.productList
            }
            else -> {
                localUser.productList
            }
        }

        emit(Result.success(productList))
    }.catch { e ->
        when (e) {
            is CancellationException -> throw e
            else -> {
                emit(Result.failure(e))
            }
        }
    }
}