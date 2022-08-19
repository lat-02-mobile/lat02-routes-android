package com.jalasoft.routesapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jalasoft.routesapp.data.remote.managers.AuthFirebaseManager
import com.jalasoft.routesapp.data.remote.managers.FirebaseManager
import com.jalasoft.routesapp.data.remote.managers.UserManager
import com.jalasoft.routesapp.data.remote.managers.UserRepository
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
}
