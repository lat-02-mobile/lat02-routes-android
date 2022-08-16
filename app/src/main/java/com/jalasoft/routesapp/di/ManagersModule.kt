package com.jalasoft.routesapp.di

import com.jalasoft.routesapp.data.remote.managers.AuthFirebaseManager
import com.jalasoft.routesapp.data.remote.managers.FirebaseManager
import com.jalasoft.routesapp.data.remote.managers.UserManager
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
}
