package com.jalasoft.routesapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jalasoft.routesapp.data.remote.interfaces.CountryRepository
import com.jalasoft.routesapp.data.remote.managers.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagersModule {
    @Singleton
    @Provides
    fun provideUserManager(authManager: AuthFirebaseManager, firebaseManager: FirebaseManager): UserManager {
        return UserManager(authManager, firebaseManager)
    }

    @Singleton
    @Provides
    fun provideUserRepository(): UserRepository = UserManager(
        AuthFirebaseManager(FirebaseAuth.getInstance()),
        FirebaseManager(FirebaseFirestore.getInstance())
    )

    @Singleton
    @Provides
    fun provideCountryRepository(): CountryRepository {
        return CountryManager(FirebaseManager(FirebaseFirestore.getInstance()))
    }
}
