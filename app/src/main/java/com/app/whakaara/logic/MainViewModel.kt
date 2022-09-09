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

    init {
        getAllAlarms()
    }

    private fun getAllAlarms() = viewModelScope.launch {
        repository.allAlarms().collect { allAlarms ->
            alarms = allAlarms
        }
    }

    fun insert(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        println("inserting......: " + alarm.alarmId)
        repository.insert(alarm)
    }

    fun update(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(alarm)
    }

    fun delete(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        println("deleteing...... " + alarm.title)
        repository.delete(alarm)
    }
}