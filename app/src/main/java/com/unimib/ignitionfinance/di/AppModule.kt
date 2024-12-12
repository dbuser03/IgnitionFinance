package com.unimib.ignitionfinance.di

import com.unimib.ignitionfinance.data.local.database.UserDao
import com.unimib.ignitionfinance.data.local.entity.User
import com.unimib.ignitionfinance.data.local.mapper.UserMapper
import com.unimib.ignitionfinance.data.remote.mapper.AuthMapper
import com.unimib.ignitionfinance.data.remote.mapper.UserDataMapper
import com.unimib.ignitionfinance.data.remote.service.AuthService
import com.unimib.ignitionfinance.data.remote.service.FirestoreService
import com.unimib.ignitionfinance.data.repository.AuthRepository
import com.unimib.ignitionfinance.data.repository.AuthRepositoryImpl
import com.unimib.ignitionfinance.data.repository.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.FirestoreRepositoryImpl
import com.unimib.ignitionfinance.data.repository.LocalDatabaseRepository
import com.unimib.ignitionfinance.data.repository.LocalDatabaseRepositoryImpl
import com.unimib.ignitionfinance.domain.usecase.AddUserToDatabaseUseCase
import com.unimib.ignitionfinance.domain.usecase.LoginUserUseCase
import com.unimib.ignitionfinance.domain.usecase.RegisterNewUserUseCase
import com.unimib.ignitionfinance.domain.usecase.RetrieveUserSettingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideUserMapper(): UserDataMapper {
        return UserDataMapper
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

    fun provideLocalDatabaseRepository(userDao: UserDao): LocalDatabaseRepository<User> {
        return LocalDatabaseRepositoryImpl(
            dao = userDao,
            addFn = UserDao::add,
            updateFn = UserDao::update,
            deleteFn = UserDao::delete,
            getByIdFn = UserDao::getUserById,
            getAllFn = UserDao::getAllUsers
        )
    }


    @Provides
    fun provideLoginUserUseCase(authRepository: AuthRepository): LoginUserUseCase {
        return LoginUserUseCase(authRepository)
    }

    @Provides
    fun provideAddUserToDatabaseUseCase(firestoreRepository: FirestoreRepository, userMapper: UserMapper, userDataMapper: UserDataMapper, localDatabaseRepository: LocalDatabaseRepository<User>): AddUserToDatabaseUseCase {
        return AddUserToDatabaseUseCase(firestoreRepository, userMapper, userDataMapper, localDatabaseRepository)
    }

    @Provides
    fun provideRegisterNewUserUseCase(authRepository: AuthRepository): RegisterNewUserUseCase {
        return RegisterNewUserUseCase(authRepository)
    }

    @Provides
    fun provideRetrieveUserSettingsUseCase(firestoreRepository: FirestoreRepository, authRepository: AuthRepository, userMapper: UserDataMapper): RetrieveUserSettingsUseCase {
        return RetrieveUserSettingsUseCase(firestoreRepository, authRepository, userMapper)
    }
}
