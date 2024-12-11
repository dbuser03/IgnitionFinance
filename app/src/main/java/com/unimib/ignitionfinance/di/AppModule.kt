package com.unimib.ignitionfinance.di

import com.unimib.ignitionfinance.data.remote.mapper.AuthMapper
import com.unimib.ignitionfinance.data.remote.mapper.UserMapper
import com.unimib.ignitionfinance.data.remote.service.AuthService
import com.unimib.ignitionfinance.data.remote.service.FirestoreService
import com.unimib.ignitionfinance.data.repository.AuthRepository
import com.unimib.ignitionfinance.data.repository.AuthRepositoryImpl
import com.unimib.ignitionfinance.data.repository.FirestoreRepository
import com.unimib.ignitionfinance.data.repository.FirestoreRepositoryImpl
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
    fun provideUserMapper(): UserMapper {
        return UserMapper
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
    fun provideLoginUserUseCase(authRepository: AuthRepository): LoginUserUseCase {
        return LoginUserUseCase(authRepository)
    }

    @Provides
    fun provideAddUserToDatabaseUseCase(firestoreRepository: FirestoreRepository, userMapper: UserMapper): AddUserToDatabaseUseCase {
        return AddUserToDatabaseUseCase(firestoreRepository, userMapper)
    }

    @Provides
    fun provideRegisterNewUserUseCase(authRepository: AuthRepository): RegisterNewUserUseCase {
        return RegisterNewUserUseCase(authRepository)
    }

    @Provides
    fun provideRetrieveUserSettingsUseCase(firestoreRepository: FirestoreRepository, authRepository: AuthRepository, userMapper: UserMapper): RetrieveUserSettingsUseCase {
        return RetrieveUserSettingsUseCase(firestoreRepository, authRepository, userMapper)
    }
}
