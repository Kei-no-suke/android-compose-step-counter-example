package com.example.stepcounterexample.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CurrentLog::class, StepCounterDayLog::class, StepCounterHourLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun stepCounterDao(): StepCounterDao
}