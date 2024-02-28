package com.app.whakaara.utils.constants

object NotificationUtilsConstants {
    const val CHANNEL_ID = "channel_id"
    const val CHANNEL_NAME = "Whakaara Alarms"

    const val INTENT_EXTRA_ALARM = "alarm"
    const val INTENT_EXTRA_ACTION_ARBITRARY = "arbitrary"
    const val INTENT_REQUEST_CODE = 0
    const val INTENT_PACKAGE = "package"
    const val INTENT_AUTO_SILENCE = "silence"
    const val INTENT_TIME_FORMAT = "format"
    const val INTENT_TIMER_NOTIFICATION_ID = "timer_notification_id"

    const val ALARM_TITLE_MAX_CHARS = 20
    const val ALARM_SOUND_TIMEOUT_DEFAULT_MILLIS = (10 * 60000).toLong()
    const val ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES = 10

    const val NOTIFICATION_RECEIVER_EXCEPTION_TAG = "NotificationReceiver exception"
    const val TIMER_RECEIVER_EXCEPTION_TAG = "TimerReceiver exception"
    const val MEDIA_SERVICE_RECEIVER_EXCEPTION_TAG = "MediaReceiver exception"
}
