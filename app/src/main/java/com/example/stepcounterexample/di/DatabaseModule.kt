package com.example.stepcounterexample.di

import android.content.Context
import androidx.room.Room
import com.example.stepcounterexample.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, AppDatabase::class.java, "log_database")
        .allowMainThreadQueries()
        .build()

    @Provides
    @Singleton
    fun provideDao(database: AppDatabase) = database.stepCounterDao()
}