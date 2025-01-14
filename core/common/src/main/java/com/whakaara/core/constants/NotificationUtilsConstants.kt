package com.whakaara.core.constants

object NotificationUtilsConstants {
    const val CHANNEL_ID = "channel_id"
    const val CHANNEL_NAME = "Whakaara Alarms"

    const val INTENT_EXTRA_ALARM = "alarm"
    const val INTENT_EXTRA_ACTION_ARBITRARY = "arbitrary"
    const val INTENT_REQUEST_CODE = 0
    const val INTENT_PACKAGE = "package"
    const val INTENT_AUTO_SILENCE = "silence"

    const val ALARM_TITLE_MAX_CHARS = 20
    const val ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES = 10

    const val SERVICE_ACTION = "action"
    const val TIMER_NOTIFICATION_ID = 300
    const val STOPWATCH_NOTIFICATION_ID = 400
    const val PLAY = 1
    const val STOP = -1

    const val NOTIFICATION_TYPE = "notification_type"
    const val NOTIFICATION_TYPE_ALARM = 100
    const val NOTIFICATION_TYPE_TIMER = 101

    const val INTENT_ALARM_ID = "alarm_id"
    const val INTENT_TIMER_NOTIFICATION_ID = "timer_notification_id"

    const val FOREGROUND_SERVICE_ID = 100

    const val TIMER_RECEIVER_ACTION_START = "start_timer"
    const val TIMER_RECEIVER_ACTION_PAUSE = "pause_timer"
    const val TIMER_RECEIVER_ACTION_STOP = "stop_timer"
    const val TIMER_RECEIVER_CURRENT_TIME_EXTRA = "current_time"

    const val STOPWATCH_RECEIVER_ACTION_START = "start_stopwatch"
    const val STOPWATCH_RECEIVER_ACTION_PAUSE = "pause_stopwatch"
    const val STOPWATCH_RECEIVER_ACTION_STOP = "stop_stopwatch"

    const val UPCOMING_ALARM_INTENT_ACTION = "intent_action"
    const val UPCOMING_ALARM_INTENT_TRIGGER_TIME = "trigger_time"
    const val UPCOMING_ALARM_RECEIVER_ACTION_START = "start_upcoming_alarm_notification"
    const val UPCOMING_ALARM_RECEIVER_ACTION_STOP = "stop_upcoming_alarm_notification"
    const val UPCOMING_ALARM_RECEIVER_ACTION_CANCEL = "cancel_upcoming_alarm_notification"

    const val STOP_FULL_SCREEN_ACTIVITY = "finish_activity"
}
