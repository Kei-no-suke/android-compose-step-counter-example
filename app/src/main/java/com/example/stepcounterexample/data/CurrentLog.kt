package com.example.stepcounterexample.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currentLogs")
data class CurrentLog(
    @PrimaryKey
    val id: Int = 0,
    val currentStepCounts: Float,
    val beginDay: Long,
    val beginHour: Long,
    val currentTime: Long,
    val dayTotalStepCounts: Int,
    val hourTotalStepCounts: Int
)
