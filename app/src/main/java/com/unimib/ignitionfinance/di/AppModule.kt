package com.unimib.ignitionfinance.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.unimib.ignitionfinance.data.local.database.AppDatabase
import com.unimib.ignitionfinance.data.local.database.SyncQueueItemDao
import com.unimib.ignitionfinance.data.local.database.UserDao
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.remote.mapper.AuthMapper
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.remote.service.AuthService
import com.unimib.ignitionfinance.data.remote.service.FirestoreService
import com.unimib.ignitionfinance.data.repository.interfaces.AuthRepository
import com.unimib.ignitionfinance.data.repository.implementation.AuthRepositoryImpl
import com.unimib.ignitionfinance.data.repository.interfaces.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.implementation.FirestoreRepositoryImpl
import com.unimib.ignitionfinance.data.repository.interfaces.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.implementation.LocalDatabaseRepositoryImpl
import com.unimib.ignitionfinance.data.repository.implementation.SyncQueueItemRepositoryImpl
import com.unimib.ignitionfinance.data.repository.interfaces.SyncQueueItemRepository
import com.unimib.ignitionfinance.domain.usecase.AddUserToDatabaseUseCase
import com.unimib.ignitionfinance.domain.usecase.DeleteAllUsersUseCase
import com.unimib.ignitionfinance.domain.usecase.LoginUserUseCase
import com.unimib.ignitionfinance.domain.usecase.RegisterNewUserUseCase
import com.unimib.ignitionfinance.domain.usecase.ResetPasswordUseCase
import com.unimib.ignitionfinance.domain.usecase.RetrieveUserSettingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideGson(): Gson = Gson()

    @Provides
    fun provideAuthService(): AuthService {
        return AuthService()
    }

    @Provides
    fun provideFirestoreService(): FirestoreService {
        return FirestoreService()
    }

    @Provides
    fun provideAuthMapper(): AuthMapper {
        return AuthMapper
    }

    @Provides
    fun provideAuthRepository(
        authService: AuthService,
        authMapper: AuthMapper
    ): AuthRepository = AuthRepositoryImpl(authService, authMapper)

    @Provides
    fun provideFirestoreRepository(
        firestoreService: FirestoreService
    ): FirestoreRepository = FirestoreRepositoryImpl(firestoreService)

    @Provides
    fun provideSyncQueueItemRepository(syncQueueItemDao: SyncQueueItemDao): SyncQueueItemRepository {
        return SyncQueueItemRepositoryImpl(syncQueueItemDao)
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    fun provideSyncItemQueueDao(appDatabase: AppDatabase): SyncQueueItemDao {
        return appDatabase.syncQueueItemDao()
    }

    @Provides
    fun provideLocalDatabaseRepository(userDao: UserDao): LocalDatabaseRepository<User> {
        return LocalDatabaseRepositoryImpl(
            dao = userDao,
            addFn = UserDao::add,
            updateFn = UserDao::update,
            deleteFn = UserDao::delete,
            getByIdFn = UserDao::getUserById,
            getAllFn = UserDao::getAllUsers,
            deleteAllFn = UserDao::deleteAllUsers
        )
    }

    @Provides
    fun provideLoginUserUseCase(authRepository: AuthRepository): LoginUserUseCase {
        return LoginUserUseCase(authRepository)
    }

    @Provides
    fun provideResetPasswordUseCase(authRepository: AuthRepository): ResetPasswordUseCase {
        return ResetPasswordUseCase(authRepository)
    }

    @Provides
    fun provideAddUserToDatabaseUseCase(
        firestoreRepository: FirestoreRepository,
        userMapper: UserMapper,
        userDataMapper: UserDataMapper,
        localDatabaseRepository: LocalDatabaseRepository<User>
    ): AddUserToDatabaseUseCase {
        return AddUserToDatabaseUseCase(
            firestoreRepository,
            userMapper,
            userDataMapper,
            localDatabaseRepository
        )
    }

    @Provides
    fun provideRegisterNewUserUseCase(authRepository: AuthRepository): RegisterNewUserUseCase {
        return RegisterNewUserUseCase(authRepository)
    }

    @Provides
    fun provideDeleteAllUsersUseCase(
        localDatabaseRepository: LocalDatabaseRepository<User>,
        firestoreRepository: FirestoreRepository
    ): DeleteAllUsersUseCase {
        return DeleteAllUsersUseCase(localDatabaseRepository, firestoreRepository)
    }

    @Provides
    fun provideRetrieveUserSettingsUseCase(
        firestoreRepository: FirestoreRepository,
        authRepository: AuthRepository,
        userMapper: UserDataMapper
    ): RetrieveUserSettingsUseCase {
        return RetrieveUserSettingsUseCase(
            firestoreRepository,
            authRepository,
            userMapper
        )
    }

    @Provides
    fun provideUserMapper(): UserMapper {
        return UserMapper
    }

    @Provides
    fun provideUserDataMapper(): UserDataMapper {
        return UserDataMapper
    }

    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            name = "ignition_finance_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
}