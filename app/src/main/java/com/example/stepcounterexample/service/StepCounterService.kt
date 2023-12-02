package com.example.stepcounterexample.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.stepcounterexample.R
import com.example.stepcounterexample.common.StepCounter
import com.example.stepcounterexample.data.CurrentLog
import com.example.stepcounterexample.data.StepCounterDayLog
import com.example.stepcounterexample.data.StepCounterHourLog
import com.example.stepcounterexample.data.StepCounterRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.TimeZone
import javax.inject.Inject

const val CHANNEL_ID = "STEP_COUNTER"
const val CHANNEL_NAME = "Foreground Channel"
const val FOREGROUND_ID = 2

@AndroidEntryPoint
class StepCounterService: Service(), SensorEventListener {
    private var running = false
    private var processing = false
    private var dayEqualFlag = false
    private var hourEqualFlag = false

    @Inject lateinit var stepCounter: StepCounter
    @Inject lateinit var stepCounterRepository: StepCounterRepository

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private lateinit var currentLogs: List<CurrentLog>

    override fun onBind(intent: Intent?): IBinder?{
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(running && !processing){
            coroutineScope.launch {
                processing = true
                currentLogs = stepCounterRepository.getAllCurrentLogs().first()
            }.invokeOnCompletion { cause ->
                if(cause == null){
                    if(currentLogs.isNotEmpty()){
                        stepCounter.setCurrentSteps(event!!.values[0])
                        dayEqualFlag = longToLocalDateTime(currentLogs[0].beginDay).equalDate(
                            LocalDateTime.now()
                        )
                        hourEqualFlag = longToLocalDateTime(currentLogs[0].beginHour).equalHour(
                            LocalDateTime.now()
                        )
                        val beginDay: Long =
                            if(!dayEqualFlag) { localDateTimeToLong(LocalDateTime.now()) }
                            else { currentLogs[0].beginDay }
                        val beginHour: Long =
                            if(!hourEqualFlag) { localDateTimeToLong(LocalDateTime.now()) }
                            else { currentLogs[0].beginHour }
                        val diffStepCounts =
                            if(stepCounter.initialLogFlag) { 0 }
                            else { stepCounter.currentSteps.toInt() - currentLogs[0].currentStepCounts.toInt() }
                        val hourTotalCounts =
                            if(!hourEqualFlag) { 0 }
                            else { currentLogs[0].hourTotalStepCounts + diffStepCounts }
                        val dayTotalCounts =
                            if(!dayEqualFlag) { 0 }
                            else { currentLogs[0].dayTotalStepCounts + diffStepCounts }
                        coroutineScope.launch {
                            stepCounterRepository.updateCurrentLog(
                                CurrentLog(
                                    currentStepCounts = stepCounter.currentSteps,
                                    beginDay = beginDay,
                                    beginHour = beginHour,
                                    currentTime = localDateTimeToLong(LocalDateTime.now()),
                                    dayTotalStepCounts = dayTotalCounts,
                                    hourTotalStepCounts = hourTotalCounts
                                )
                            )
                            if(!dayEqualFlag){
                                stepCounterRepository.insertDayLog(
                                    StepCounterDayLog(
                                        steps = currentLogs[0].dayTotalStepCounts,
                                        date = currentLogs[0].beginDay
                                    )
                                )
                            }
                            if(!hourEqualFlag){
                                stepCounterRepository.insertHourLog(
                                    StepCounterHourLog(
                                        steps = currentLogs[0].hourTotalStepCounts,
                                        date = currentLogs[0].beginDay,
                                        beginHour = longToLocalDateTime(currentLogs[0].beginHour).hour,
                                        endHour = longToLocalDateTime(currentLogs[0].beginHour).hour + 1
                                    )
                                )
                            }
                        }.invokeOnCompletion { nestedCause ->
                            if(nestedCause == null){
                                processing = false
                                stepCounter.setInitialLogFlag(false)
                            }else{
                                Log.e(
                                    TAG,
                                    "nested coroutine scope process is failed: onSensorChanged",
                                    nestedCause
                                )
                            }
                        }
                    }
                }else{
                    Log.e(TAG, "coroutine scope process is failed: onSensorChanged", cause)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        stepCounter.setInitialLogFlag(true)

        if(stepCounterRepository.getCountCurrentLogs() == 0){
            coroutineScope.launch {
                stepCounterRepository.insertCurrentLog(
                    CurrentLog(
                        currentStepCounts = stepCounter.currentSteps,
                        beginDay = localDateTimeToLong(LocalDateTime.now()),
                        beginHour = localDateTimeToLong(LocalDateTime.now()),
                        currentTime = localDateTimeToLong(LocalDateTime.now()),
                        dayTotalStepCounts = 0,
                        hourTotalStepCounts = 0
                    )
                )
            }
        }else {
            coroutineScope.launch {
                processing = true
                currentLogs = stepCounterRepository.getAllCurrentLogs().first()
            }.invokeOnCompletion { cause ->
                if (cause == null) {
                    if (currentLogs.isNotEmpty()) {
                        dayEqualFlag = longToLocalDateTime(currentLogs[0].beginDay).equalDate(
                            LocalDateTime.now()
                        )
                        hourEqualFlag = longToLocalDateTime(currentLogs[0].beginHour).equalHour(
                            LocalDateTime.now()
                        )
                        val hourTotalCounts =
                            if (!hourEqualFlag) {
                                0
                            } else {
                                currentLogs[0].hourTotalStepCounts
                            }
                        val dayTotalCounts =
                            if (!dayEqualFlag) {
                                0
                            } else {
                                currentLogs[0].dayTotalStepCounts
                            }
                        val beginDay: Long =
                            if (!dayEqualFlag) {
                                localDateTimeToLong(LocalDateTime.now())
                            } else {
                                currentLogs[0].beginDay
                            }
                        val beginHour: Long =
                            if (!hourEqualFlag) {
                                localDateTimeToLong(LocalDateTime.now())
                            } else {
                                currentLogs[0].beginHour
                            }
                        coroutineScope.launch {
                            stepCounterRepository.updateCurrentLog(
                                CurrentLog(
                                    currentStepCounts = stepCounter.currentSteps,
                                    beginDay = beginDay,
                                    beginHour = beginHour,
                                    currentTime = localDateTimeToLong(LocalDateTime.now()),
                                    dayTotalStepCounts = dayTotalCounts,
                                    hourTotalStepCounts = hourTotalCounts
                                )
                            )
                            if (!dayEqualFlag) {
                                stepCounterRepository.insertDayLog(
                                    StepCounterDayLog(
                                        steps = currentLogs[0].dayTotalStepCounts,
                                        date = currentLogs[0].beginDay
                                    )
                                )
                            }
                            if (!hourEqualFlag) {
                                stepCounterRepository.insertHourLog(
                                    StepCounterHourLog(
                                        steps = currentLogs[0].hourTotalStepCounts,
                                        date = currentLogs[0].beginDay,
                                        beginHour = longToLocalDateTime(currentLogs[0].beginHour).hour,
                                        endHour = longToLocalDateTime(currentLogs[0].beginHour).hour + 1
                                    )
                                )
                            }
                        }.invokeOnCompletion { nestedCause ->
                            if (nestedCause == null) {
                                processing = false
                            } else {
                                Log.e(
                                    TAG,
                                    "nested coroutine scope process is failed: onStartCommand",
                                    nestedCause
                                )
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "coroutine scope process is failed: onStartCommand", cause)
                }
            }
        }

        try{
            val sensorManager: SensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)

            running = true
            startForeground(FOREGROUND_ID, notification())
        }catch(e : Exception){
            Log.e(TAG, e.toString(), e)
        }

        return START_NOT_STICKY
    }

    private fun notification() : Notification {
        val title = "Step Counter"
        val contentText = "step counter is active"
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)

        return notification.build()
    }

    private fun longToLocalDateTime(epochSecond: Long): LocalDateTime {
        val timeZone = TimeZone.getDefault()
        val timeZoneOffset = timeZone.getOffset(System.currentTimeMillis())
        val zoneOffset = ZoneOffset.ofTotalSeconds(timeZoneOffset / 1000)
        return LocalDateTime.ofEpochSecond(epochSecond, 0, zoneOffset)
    }

    private fun localDateTimeToLong(localDateTime: LocalDateTime): Long{
        val timeZone = TimeZone.getDefault()
        val timeZoneOffset = timeZone.getOffset(System.currentTimeMillis())
        val zoneOffset = ZoneOffset.ofTotalSeconds(timeZoneOffset / 1000)
        return localDateTime.toEpochSecond(zoneOffset)
    }

    companion object {
        const val TAG = "StepCounterServiceKt"
    }

}

fun startNotifyForegroundService(context: Context){
    Log.d("StepCounterServiceKt", "startForeground")
    ContextCompat.startForegroundService(
        context,
        Intent(context, StepCounterService::class.java)
    )
}

fun stopNotifyForegroundService(context: Context){
    val targetIntent = Intent(context, StepCounterService::class.java)
    context.stopService(targetIntent)
}

fun LocalDateTime.equalDate(localDateTime: LocalDateTime): Boolean{
    return (this.dayOfYear == localDateTime.dayOfYear) && (this.year == localDateTime.year)
}

fun LocalDateTime.equalHour(localDateTime: LocalDateTime): Boolean{
    return this.equalDate(localDateTime) && (this.hour == localDateTime.hour)
}