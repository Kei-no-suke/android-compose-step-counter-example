package com.example.stepcounterexample.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stepCounterHourLogs")
data class StepCounterHourLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val steps: Int,
    val date: Long,
    val beginHour: Int,
    val endHour: Int
)
