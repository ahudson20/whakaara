package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.whakaara.data.Alarm
import com.app.whakaara.data.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val app: Application,
    private val repository: AlarmRepository
) : AndroidViewModel(app) {

    var alarms by mutableStateOf(emptyList<Alarm>())

    init {
        getAllAlarms()
    }

    private fun getAllAlarms() = viewModelScope.launch {
        repository.allAlarms().collect { allAlarms ->
            alarms = allAlarms
        }
    }

    fun insert(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        createAlarm(alarm)
        repository.insert(alarm)
        showToast("Alarm set")
    }

    fun update(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(alarm)
    }

    fun delete(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        deleteAlarm(alarm)
        repository.delete(alarm)
        showToast("Alarm removed")
    }

    private fun createAlarm(
        alarm: Alarm,
    ) {
        val alarmManager =  app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, Receiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
        }
        val pendingIntent = PendingIntent.getBroadcast(app, 0, intent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, getTimeInMillis(alarm), pendingIntent)
    }

    private fun deleteAlarm(
        alarm: Alarm,
    ) {
        val alarmManager =  app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, Receiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
        }
        val pendingIntent = PendingIntent.getBroadcast(app, 0, intent, 0)
        if(pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun showToast(title: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(app.applicationContext, title, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getTimeInMillis(alarm: Alarm): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
        }.timeInMillis
    }
}