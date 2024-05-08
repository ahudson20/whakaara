package com.app.whakaara.state

import com.whakaara.model.preferences.AppTheme
import com.whakaara.model.preferences.GradualSoundDuration
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.SettingsTime
import com.whakaara.model.preferences.TimeFormat
import com.whakaara.model.preferences.VibrationPattern

data class PreferencesState(
    val isReady: Boolean = false,
    val preferences: Preferences = Preferences(
        id = 0,
        timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
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
        timerSoundPath = "",
        gradualSoundDuration = GradualSoundDuration.GRADUAL_INCREASE_DURATION_NEVER,
        timerGradualSoundDuration = GradualSoundDuration.GRADUAL_INCREASE_DURATION_NEVER
    )
)
