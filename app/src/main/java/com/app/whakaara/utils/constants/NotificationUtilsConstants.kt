package com.app.whakaara.utils.constants

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

    const val MEDIA_SERVICE_RECEIVER_EXCEPTION_TAG = "MediaReceiver exception"
    const val MEDIA_SERVICE_EXCEPTION_TAG = "MediaService exception"

    const val FOREGROUND_SERVICE_ID = 100
}
