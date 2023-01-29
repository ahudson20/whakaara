package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.whakaara.MainActivity
import com.app.whakaara.data.Alarm
import com.app.whakaara.data.AlarmRepository
import com.app.whakaara.utils.PendingIntentUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OptIn(ExperimentalLayoutApi::class)
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

    fun create(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        createAlarm(alarm)
        repository.insert(alarm)
        showToast("Alarm set")
    }

    private fun update(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(alarm)
    }

    fun delete(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        deleteAlarm(alarm)
        repository.delete(alarm)
        showToast("Alarm removed")
    }

    fun disable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        deleteAlarm(alarm)
        update(alarm.copy(isEnabled = false))
        showToast("Alarm cancelled")
    }

    fun enable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        createAlarm(alarm)
        update(alarm.copy(isEnabled = true))
        showToast("Alarm enabled")
    }

    private fun createAlarm(
        alarm: Alarm,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    app.applicationContext.startActivity(intent)
                }
            } else {
                val intent = Intent(app, Receiver::class.java).apply {
                    // setting unique action allows for differentiation when deleting.
                    this.action = alarm.alarmId.toString()
                    putExtra("title", "Alarm")
                    putExtra("subtitle", generateSubTitle(alarm))
                    putExtra("alarmId", alarm.alarmId)
                }
                val pendingIntent = PendingIntentUtils.getBroadcast(app, 0, intent, 0)

                val mainActivityIntent = Intent(app.applicationContext, MainActivity::class.java)
                val testPendingIntent = PendingIntentUtils.getBroadcast(app, 1, mainActivityIntent, 0)

                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(getTimeInMillis(alarm), testPendingIntent),
                    pendingIntent
                )
            }
        } else {
            val alarmManager =  app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(app, Receiver::class.java).apply {
                // setting unique action allows for differentiation when deleting.
                this.action = alarm.alarmId.toString()
                putExtra("title", "Alarm")
                putExtra("subtitle", generateSubTitle(alarm))
                putExtra("alarmId", alarm.alarmId)
            }
            val pendingIntent = PendingIntentUtils.getBroadcast(app, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, getTimeInMillis(alarm), pendingIntent)
        }
    }

    private fun deleteAlarm(
        alarm: Alarm,
    ) {
        val alarmManager =  app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, Receiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
        }
        val pendingIntent = PendingIntentUtils.getBroadcast(app, 0, intent, 0)

        // no need to check if not null anymore?
        alarmManager.cancel(pendingIntent)
    }

    private fun showToast(title: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(app.applicationContext, title, Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateSubTitle(alarm: Alarm): String {
        val subTitle = StringBuilder()
        val hour24 = (alarm.hour % 12).toString()
        val minute = if (alarm.minute < 10) "0" + alarm.minute.toString() else alarm.minute.toString()
        val postFix = if (alarm.hour < 12)  "AM" else "PM"

        subTitle.append(SimpleDateFormat("EE", Locale.ENGLISH).format(System.currentTimeMillis())).append(" ")
        subTitle.append("$hour24:$minute $postFix")
        return subTitle.toString()
    }

    private fun getTimeInMillis(alarm: Alarm): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
        }

        /** check if time has already elapsed, set for following day **/
        if (cal.timeInMillis < System.currentTimeMillis()) {
            cal.add(Calendar.DATE, 1)
            showToast("Alarm set for following day")
        }

        return cal.timeInMillis
    }
}