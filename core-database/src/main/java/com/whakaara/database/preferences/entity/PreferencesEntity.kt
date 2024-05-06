package com.whakaara.database.preferences.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.whakaara.model.preferences.AppTheme
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.SettingsTime
import com.whakaara.model.preferences.VibrationPattern

@Entity(tableName = "preferences_table")
data class PreferencesEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var is24HourFormat: Boolean = true,
    var isVibrateEnabled: Boolean = true,
    var isSnoozeEnabled: Boolean = true,
    var deleteAfterGoesOff: Boolean = false,
    var autoSilenceTime: SettingsTime = SettingsTime.TEN,
    var snoozeTime: SettingsTime = SettingsTime.TEN,
    var alarmSoundPath: String = "",
    var vibrationPattern: VibrationPattern = VibrationPattern.CLICK,
    var appTheme: AppTheme = AppTheme.MODE_AUTO,
    var shouldShowOnboarding: Boolean = false,
    var isVibrationTimerEnabled: Boolean = true,
    var timerVibrationPattern: VibrationPattern = VibrationPattern.CLICK,
    var filteredAlarmList: Boolean = false,
    var upcomingAlarmNotification: Boolean = true,
    var upcomingAlarmNotificationTime: SettingsTime = SettingsTime.TEN,
    val dynamicTheme: Boolean = false,
    val autoRestartTimer: Boolean = true,
    val timerSoundPath: String = ""
)

fun PreferencesEntity.asExternalModel() =
    Preferences(
        id = id,
        is24HourFormat = is24HourFormat,
        isVibrateEnabled = isVibrateEnabled,
        isSnoozeEnabled = isSnoozeEnabled,
        deleteAfterGoesOff = deleteAfterGoesOff,
        autoSilenceTime = autoSilenceTime,
        snoozeTime = snoozeTime,
        alarmSoundPath = alarmSoundPath,
        vibrationPattern = vibrationPattern,
        appTheme = appTheme,
        shouldShowOnboarding = shouldShowOnboarding,
        isVibrationTimerEnabled = isVibrationTimerEnabled,
        timerVibrationPattern = timerVibrationPattern,
        filteredAlarmList = filteredAlarmList,
        upcomingAlarmNotification = upcomingAlarmNotification,
        upcomingAlarmNotificationTime = upcomingAlarmNotificationTime,
        dynamicTheme = dynamicTheme,
        autoRestartTimer = autoRestartTimer,
        timerSoundPath = timerSoundPath
    )

fun Preferences.asInternalModel() =
    PreferencesEntity(
        is24HourFormat = is24HourFormat,
        isVibrateEnabled = isVibrateEnabled,
        isSnoozeEnabled = isSnoozeEnabled,
        deleteAfterGoesOff = deleteAfterGoesOff,
        autoSilenceTime = autoSilenceTime,
        snoozeTime = snoozeTime,
        alarmSoundPath = alarmSoundPath,
        vibrationPattern = vibrationPattern,
        appTheme = appTheme,
        shouldShowOnboarding = shouldShowOnboarding,
        isVibrationTimerEnabled = isVibrationTimerEnabled,
        timerVibrationPattern = timerVibrationPattern,
        filteredAlarmList = filteredAlarmList,
        upcomingAlarmNotification = upcomingAlarmNotification,
        upcomingAlarmNotificationTime = upcomingAlarmNotificationTime,
        dynamicTheme = dynamicTheme,
        autoRestartTimer = autoRestartTimer,
        timerSoundPath = timerSoundPath
    )
