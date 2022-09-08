package com.app.whakaara.logic

import android.app.Application
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
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    app: Application,
    private val repository: AlarmRepository
) : AndroidViewModel(app) {

    var alarms by mutableStateOf(emptyList<Alarm>())
    var alarm by mutableStateOf(Alarm(hour = 0, minute = 0, title = "", vibration = false))

    init {
        // TODO: here for testing, remove later.
        insert(Alarm(hour = 15, minute = 31, title = "hallo1", vibration = false))
        insert(Alarm(hour = 25, minute = 32, title = "hallo2", vibration = false))
        insert(Alarm(hour = 35, minute = 33, title = "hallo3", vibration = false))
        insert(Alarm(hour = 45, minute = 34, title = "hallo4", vibration = false))
        insert(Alarm(hour = 55, minute = 35, title = "hallo5", vibration = false))
        getAllAlarms()
    }

    private fun getAllAlarms() = viewModelScope.launch {
        repository.allAlarms().collect { allAlarms ->
            alarms = allAlarms
        }
    }

    fun insert(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(alarm)
    }

    fun update(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(alarm)
    }

    fun delete(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(alarm)
    }

    fun updateHour(hour: Int) {
        alarm = alarm.copy(
            hour = hour
        )
    }

    fun updateMinute(minute: Int) {
        alarm = alarm.copy(
            minute = minute
        )
    }

    fun updateTitle(title: String) {
        alarm = alarm.copy(
            title = title
        )
    }

    fun updateVibration(vibration: Boolean) {
        alarm = alarm.copy(
            vibration = vibration
        )
    }
}