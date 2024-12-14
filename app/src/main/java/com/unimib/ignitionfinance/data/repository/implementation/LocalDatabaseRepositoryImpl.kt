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
    private val deleteAllFn: suspend DAO.() -> Unit
) : LocalDatabaseRepository<T> {

    override suspend fun getById(id: String): Flow<Result<T?>> = flow {
        try {
            val result = dao.getByIdFn(id)
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAll(): Flow<Result<List<T>>> = flow {
        try {
            val result = dao.getAllFn()
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun add(entity: T): Flow<Result<Unit>> = flow {
        try {
            val result = dao.addFn(entity)
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun update(entity: T): Flow<Result<Unit>> = flow {
        try {
            dao.updateFn(entity)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun delete(entity: T): Flow<Result<Unit>> = flow {
        try {
            dao.deleteFn(entity)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteAll(): Flow<Result<Unit>> = flow {
        try {
            dao.deleteAllFn()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}