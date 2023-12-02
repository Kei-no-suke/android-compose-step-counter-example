package com.example.stepcounterexample.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StepCounterDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHourLog(stepCounterHourLog: StepCounterHourLog)

    @Delete
    suspend fun deleteHourLog(stepCounterHourLog: StepCounterHourLog)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDayLog(stepCounterDayLog: StepCounterDayLog)

    @Delete
    suspend fun deleteDayLog(stepCounterDayLog: StepCounterDayLog)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCurrentLog(currentLog: CurrentLog)

    @Update
    suspend fun updateCurrentLog(currentLog: CurrentLog)

    @Query("SELECT * FROM stepCounterHourLogs")
    fun getAllHourLogs(): Flow<List<StepCounterHourLog>>

    @Query("SELECT * FROM stepCounterDayLogs")
    fun getAllDayLogs(): Flow<List<StepCounterDayLog>>

    @Query("SELECT * FROM currentLogs")
    fun getAllCurrentLogs(): Flow<List<CurrentLog>>

    @Query("SELECT COUNT(*) FROM currentLogs")
    fun getCountCurrentLogs(): Int
}