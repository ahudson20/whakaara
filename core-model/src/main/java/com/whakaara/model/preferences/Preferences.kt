package com.whakaara.model.preferences

data class Preferences(
    var id: Int,
    var is24HourFormat: Boolean,
    var isVibrateEnabled: Boolean,
    var isSnoozeEnabled: Boolean,
    var deleteAfterGoesOff: Boolean,
    var autoSilenceTime: SettingsTime,
    var snoozeTime: SettingsTime,
    var alarmSoundPath: String,
    var vibrationPattern: VibrationPattern,
    var appTheme: AppTheme,
    var shouldShowOnboarding: Boolean,
    var isVibrationTimerEnabled: Boolean,
    var timerVibrationPattern: VibrationPattern,
    var filteredAlarmList: Boolean,
    var upcomingAlarmNotification: Boolean,
    var upcomingAlarmNotificationTime: SettingsTime,
    val dynamicTheme: Boolean,
    val autoRestartTimer: Boolean,
    val timerSoundPath: String
)
