package com.example.stepcounterexample.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stepCounterDayLogs")
data class StepCounterDayLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val steps: Int,
    val date: Long
)
