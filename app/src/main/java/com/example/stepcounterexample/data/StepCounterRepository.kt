package com.example.stepcounterexample.data

import javax.inject.Inject

class StepCounterRepository @Inject constructor(
    private val stepCounterDao: StepCounterDao
){
    suspend fun insertHourLog(stepCounterHourLog: StepCounterHourLog) =
        stepCounterDao.insertHourLog(stepCounterHourLog)

    suspend fun deleteHourLog(stepCounterHourLog: StepCounterHourLog) =
        stepCounterDao.deleteHourLog(stepCounterHourLog)

    suspend fun insertDayLog(stepCounterDayLog: StepCounterDayLog) =
        stepCounterDao.insertDayLog(stepCounterDayLog)

    suspend fun deleteDayLog(stepCounterDayLog: StepCounterDayLog) =
        stepCounterDao.deleteDayLog(stepCounterDayLog)

    suspend fun insertCurrentLog(currentLog: CurrentLog) =
        stepCounterDao.insertCurrentLog(currentLog)

    suspend fun updateCurrentLog(currentLog: CurrentLog) =
        stepCounterDao.updateCurrentLog(currentLog)

    fun getAllHourLogs() = stepCounterDao.getAllHourLogs()

    fun getAllDayLogs() = stepCounterDao.getAllDayLogs()

    fun getAllCurrentLogs() = stepCounterDao.getAllCurrentLogs()

    fun getCountCurrentLogs() = stepCounterDao.getCountCurrentLogs()
}