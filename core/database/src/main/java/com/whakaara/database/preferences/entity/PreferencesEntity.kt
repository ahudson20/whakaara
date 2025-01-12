package com.whakaara.database.preferences.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.whakaara.model.preferences.AppTheme
import com.whakaara.model.preferences.GradualSoundDuration
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.SettingsTime
import com.whakaara.model.preferences.TimeFormat
import com.whakaara.model.preferences.VibrationPattern

@Entity(tableName = "preferences_table")
data class PreferencesEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var timeFormat: TimeFormat = TimeFormat.TWENTY_FOUR_HOURS,
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
    val timerSoundPath: String = "",
    val gradualSoundDuration: GradualSoundDuration,
    val timerGradualSoundDuration: GradualSoundDuration,
    val flashLight: Boolean = false
)

fun PreferencesEntity.asExternalModel() = Preferences(
    id = id,
    timeFormat = timeFormat,
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
    timerSoundPath = timerSoundPath,
    gradualSoundDuration = gradualSoundDuration,
    timerGradualSoundDuration = timerGradualSoundDuration,
    flashLight = flashLight
)

fun Preferences.asInternalModel() = PreferencesEntity(
    timeFormat = timeFormat,
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
    timerSoundPath = timerSoundPath,
    gradualSoundDuration = gradualSoundDuration,
    timerGradualSoundDuration = timerGradualSoundDuration,
    flashLight = flashLight
)
