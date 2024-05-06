package com.whakaara.test

import com.whakaara.model.alarm.Alarm
import com.whakaara.model.preferences.AppTheme
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.SettingsTime
import com.whakaara.model.preferences.VibrationPattern
import java.util.Calendar
import java.util.UUID

object MockUtil {
    fun mockAlarm() =
        Alarm(
            alarmId = UUID.fromString("alarmId"),
            date = Calendar.getInstance(),
            title = "title",
            subTitle = "10:03 AM",
            vibration = true,
            isEnabled = true,
            isSnoozeEnabled = true,
            deleteAfterGoesOff = false,
            repeatDaily = false,
            daysOfWeek = mutableListOf()
        )

    fun mockAlarmList() = listOf(mockAlarm())

    fun mockPreferences() =
        Preferences(
            id = 0,
            is24HourFormat = true,
            isVibrateEnabled = true,
            isSnoozeEnabled = true,
            deleteAfterGoesOff = false,
            autoSilenceTime = SettingsTime.TEN,
            snoozeTime = SettingsTime.TEN,
            alarmSoundPath = "",
            vibrationPattern = VibrationPattern.CLICK,
            appTheme = AppTheme.MODE_AUTO,
            shouldShowOnboarding = false,
            isVibrationTimerEnabled = true,
            timerVibrationPattern = VibrationPattern.CLICK,
            filteredAlarmList = false,
            upcomingAlarmNotification = true,
            upcomingAlarmNotificationTime = SettingsTime.TEN,
            dynamicTheme = false,
            autoRestartTimer = true,
            timerSoundPath = ""
        )
}
