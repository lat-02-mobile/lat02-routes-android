package com.jalasoft.routesapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jalasoft.routesapp.RoutesAppApplication
import com.jalasoft.routesapp.data.api.retrofit.IGmapsService
import com.jalasoft.routesapp.data.local.room.db.RoutesDB
import com.jalasoft.routesapp.data.local.room.interfaces.LocalDataBaseRepository
import com.jalasoft.routesapp.data.local.room.managers.LocalDataBaseManager
import com.jalasoft.routesapp.data.remote.interfaces.*
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
    fun provideCountryRepository(): CityRepository {
        return CityManager(FirebaseManager(FirebaseFirestore.getInstance()))
    }

    @Singleton
    @Provides
    fun provideRouteRepository(): RouteRepository {
        return RouteManager(FirebaseManager(FirebaseFirestore.getInstance()))
    }

    @Singleton
    @Provides
    fun providePlaceManager(retrofitService: IGmapsService): PlaceRepository {
        return PlaceManager(retrofitService)
    }

    @Singleton
    @Provides
    fun provideTourPointRepository(): TourPointRepository {
        return TourPointManager(FirebaseManager(FirebaseFirestore.getInstance()))
    }

    @Singleton
    @Provides
    fun provideLocalDataBaseRepository(): LocalDataBaseRepository {
        return LocalDataBaseManager(RoutesAppApplication.routesDB as RoutesDB)
    }

    @Singleton
    @Provides
    fun provideRoutesDB(): RoutesDB {
        return RoutesAppApplication.routesDB as RoutesDB
    }
}
