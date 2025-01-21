package com.unimib.ignitionfinance.domain.usecase

import android.content.Context
import com.unimib.ignitionfinance.data.local.entity.SyncQueueItem
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.local.utils.SyncStatus
import com.unimib.ignitionfinance.data.model.user.Product
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.data.worker.SyncOperationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class UpdateProductListUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val localDatabaseRepository: LocalDatabaseRepository<User>,
    private val userMapper: UserMapper,
    private val userDataMapper: UserDataMapper,
    private val syncQueueItemRepository: SyncQueueItemRepository,
    @ApplicationContext private val context: Context
) {
    fun addProduct(product: Product): Flow<Result<Unit>> = flow {
        try {
            val currentUserResult = authRepository.getCurrentUser().first()
            val authData = currentUserResult.getOrNull()
                ?: throw IllegalStateException("Failed to get current user")

            val userId = authData.id.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("User ID is missing")

            val currentUser = localDatabaseRepository.getById(userId).first().getOrNull()
                ?: throw IllegalStateException("User not found in local database")

            if (currentUser.productList.any { it.ticker == product.ticker }) {
                throw IllegalStateException("Product with ticker ${product.ticker} already exists")
            }

            executeUpdate(currentUser) { user ->
                user.copy(
                    productList = user.productList + product,
                    updatedAt = System.currentTimeMillis(),
                    firstAdded = user.productList.isEmpty()
                )
            }.collect { emit(it) }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun removeProduct(productId: String): Flow<Result<Unit>> = flow {
        try {
            val currentUserResult = authRepository.getCurrentUser().first()
            val authData = currentUserResult.getOrNull()
                ?: throw IllegalStateException("Failed to get current user")

            val userId = authData.id.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("User ID is missing")

            val currentUser = localDatabaseRepository.getById(userId).first().getOrNull()
                ?: throw IllegalStateException("User not found in local database")

            if (!currentUser.productList.any { it.ticker == productId }) {
                throw IllegalStateException("Product with ticker $productId not found")
            }

            executeUpdate(currentUser) { user ->
                user.copy(
                    productList = user.productList.filter { it.ticker != productId },
                    updatedAt = System.currentTimeMillis()
                )
            }.collect { emit(it) }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    fun updateProduct(updatedProduct: Product): Flow<Result<Unit>> = flow {
        try {
            val currentUserResult = authRepository.getCurrentUser().first()
            val authData = currentUserResult.getOrNull()
                ?: throw IllegalStateException("Failed to get current user")

            val userId = authData.id.takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException("User ID is missing")

            val currentUser = localDatabaseRepository.getById(userId).first().getOrNull()
                ?: throw IllegalStateException("User not found in local database")

            if (!currentUser.productList.any { it.ticker == updatedProduct.ticker }) {
                throw IllegalStateException("Product with ticker ${updatedProduct.ticker} not found")
            }

            executeUpdate(currentUser) { user ->
                user.copy(
                    productList = user.productList.map {
                        if (it.ticker == updatedProduct.ticker) updatedProduct else it
                    },
                    updatedAt = System.currentTimeMillis()
                )
            }.collect { emit(it) }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun executeUpdate(currentUser: User, updateFunction: (User) -> User): Flow<Result<Unit>> = flow {
        try {
            coroutineScope {
                val updatedUser = updateFunction(currentUser)

                val localDbDeferred = async {
                    localDatabaseRepository.update(updatedUser).first()
                }

                val syncQueueItem = createSyncQueueItem(updatedUser)
                val syncQueueDeferred = async {
                    syncQueueItemRepository.insert(syncQueueItem)
                }

                localDbDeferred.await()
                syncQueueDeferred.await()

                withContext(Dispatchers.IO) {
                    SyncOperationScheduler.scheduleOneTime<User>(context)
                }

                emit(Result.success(Unit))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun createSyncQueueItem(user: User): SyncQueueItem {
        val userData = userMapper.mapUserToUserData(user)
        val document = userDataMapper.mapUserDataToDocument(userData)

        return SyncQueueItem(
            id = user.id,
            collection = "users",
            payload = document,
            operationType = "UPDATE",
            status = SyncStatus.PENDING
        )
    }
}