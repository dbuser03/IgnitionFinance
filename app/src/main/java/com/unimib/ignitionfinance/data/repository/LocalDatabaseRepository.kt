package com.unimib.ignitionfinance.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface LocalDatabaseRepository<T> {
    suspend fun getById(id: String): Flow<Result<T?>>
    suspend fun getAll(): Flow<Result<List<T>>>
    suspend fun add(entity: T): Flow<Result<Unit>>
    suspend fun update(entity: T): Flow<Result<Unit>>
    suspend fun delete(entity: T): Flow<Result<Unit>>
    suspend fun deleteAll(): Flow<Result<Unit>>
}

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
    }

    override suspend fun getAll(): Flow<Result<List<T>>> = flow {
        try {
            val result = dao.getAllFn()
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun add(entity: T): Flow<Result<Unit>> = flow {
        try {
            val result = dao.addFn(entity)
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun update(entity: T): Flow<Result<Unit>> = flow {
        try {
            dao.updateFn(entity)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun delete(entity: T): Flow<Result<Unit>> = flow {
        try {
            dao.deleteFn(entity)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun deleteAll(): Flow<Result<Unit>> = flow {
        try {
            dao.deleteAllFn()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
