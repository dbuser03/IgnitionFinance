package com.unimib.ignitionfinance.data.repository.implementation

import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocalDatabaseRepositoryImpl<T, DAO> @Inject constructor(
    private val dao: DAO,
    private val addFn: suspend DAO.(T) -> Unit,
    private val updateFn: suspend DAO.(T) -> Unit,
    private val deleteFn: suspend DAO.(T) -> Unit,
    private val getByIdFn: suspend DAO.(String) -> T?,
    private val getAllFn: suspend DAO.() -> List<T>,
    private val deleteAllFn: suspend DAO.() -> Unit,
    private val getUpdatedAfterFn: suspend DAO.(Long) -> List<T>,
    private val getUnsyncedFn: suspend DAO.() -> List<T>,
    private val updateLastSyncFn: suspend DAO.(String, Long) -> Unit
) : LocalDatabaseRepository<T> {

    override suspend fun getById(id: String): Flow<Result<T?>> =
        performDbOperation { dao.getByIdFn(id) }

    override suspend fun getAll(): Flow<Result<List<T>>> =
        performDbOperation { dao.getAllFn() }

    override suspend fun add(entity: T): Flow<Result<Unit>> =
        performDbOperation { dao.addFn(entity) }

    override suspend fun update(entity: T): Flow<Result<Unit>> =
        performDbOperation { dao.updateFn(entity) }

    override suspend fun delete(entity: T): Flow<Result<Unit>> =
        performDbOperation { dao.deleteFn(entity) }

    override suspend fun deleteAll(): Flow<Result<Unit>> =
        performDbOperation { dao.deleteAllFn() }

    override suspend fun getUpdatedAfter(timestamp: Long): Flow<Result<List<T>>> =
        performDbOperation { dao.getUpdatedAfterFn(timestamp) }

    override suspend fun getUnsyncedEntities(): Flow<Result<List<T>>> =
        performDbOperation { dao.getUnsyncedFn() }

    override suspend fun updateLastSyncTimestamp(id: String, timestamp: Long): Flow<Result<Unit>> =
        performDbOperation { dao.updateLastSyncFn(id, timestamp) }

    private fun <R> performDbOperation(action: suspend DAO.() -> R): Flow<Result<R>> = flow {
        try {
            val result = dao.action()
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}