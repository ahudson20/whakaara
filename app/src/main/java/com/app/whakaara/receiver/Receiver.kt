package com.app.whakaara.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.whakaara.activities.FullScreenNotificationActivity
import com.app.whakaara.data.Alarm
import com.app.whakaara.data.AlarmRepository
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.NotificationUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ACTION_ARBITRARY
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ACTION_CANCEL
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ACTION_SNOOZE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ALARM_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_NOTIFICATION_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_REQUEST_CODE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class Receiver : BroadcastReceiver() {

    @Inject
    lateinit var repo: AlarmRepository

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val alarm = GeneralUtils.convertStringToAlarmObject(string = intent.getStringExtra(INTENT_EXTRA_ALARM))

            setIsEnabledToFalse(alarmId = alarm.alarmId.toString())

            createNotificationAndDisplay(
                alarm = alarm,
                context = context
            )
        } catch (exception: Exception) {
            Log.d("Receiver exception", exception.printStackTrace().toString())
        }
    }

    private fun createNotificationAndDisplay(
        alarm: Alarm,
        context: Context
    ) {

        val uniqueID = System.currentTimeMillis().toInt()

        val snoozeAlarmIntent = Intent().apply {
            setClass(context, NotificationReceiver::class.java)
            putExtra(INTENT_EXTRA_ALARM_ID, alarm.alarmId.toString())
            putExtra(INTENT_EXTRA_NOTIFICATION_ID, uniqueID)
            action = INTENT_EXTRA_ACTION_SNOOZE
        }
        val snoozeActionPendingIntent = PendingIntentUtils.getBroadcast(
            context = context,
            id = INTENT_REQUEST_CODE,
            intent = snoozeAlarmIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )


        // TODO: Implement cancel button on notification?

        //            val cancelAlarmIntent = Intent().apply {
        //                setClass(context, NotificationService::class.java)
        //                putExtra("alarmId", alarmId.toString())
        //                putExtra("notificationId", uniqueID)
        //                action = "cancel"
        //
        //            }
        //            val cancelActionPendingIntent = PendingIntent.getService(context, 0, cancelAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val cancelAlarmIntent = Intent().apply {
            setClass(context, NotificationReceiver::class.java)
            putExtra(INTENT_EXTRA_ALARM_ID, alarm.alarmId.toString())
            putExtra(INTENT_EXTRA_NOTIFICATION_ID, uniqueID)
            action = INTENT_EXTRA_ACTION_CANCEL
        }
        val cancelActionPendingIntent = PendingIntentUtils.getBroadcast(
            context = context,
            id = INTENT_REQUEST_CODE,
            intent = cancelAlarmIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )//PendingIntent.FLAG_UPDATE_CURRENT

        val fullScreenIntent = Intent().apply {
            setClass(context, FullScreenNotificationActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))

            /**
             * Unsure why I need to set an action here.
             * If I don't, I can't get the extras in the activity this intent starts.
             * https://stackoverflow.com/questions/15343840/intent-extras-missing-when-activity-started#:~:text=We%20stumbled%20upon,might%20fix%20it.
             * */
            action = INTENT_EXTRA_ACTION_ARBITRARY
        }
        val fullScreenPendingIntent = PendingIntentUtils.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationUtils = NotificationUtils(context)
        val notification = notificationUtils.getNotificationBuilder().apply {
            setContentTitle(alarm.title)
            setContentText(alarm.subTitle)
            setFullScreenIntent(fullScreenPendingIntent, true)
//            addAction(
//                R.drawable.baseline_cancel_24,
//                context.getString(R.string.notification_action_button_snooze),
//                snoozeActionPendingIntent
//            )
//            addAction(
//                R.drawable.baseline_cancel_24,
//                context.getString(R.string.notification_action_button_cancel),
//                cancelActionPendingIntent
//            )
        }.build()

        notificationUtils.getChannel().apply {
            enableVibration(alarm.vibration)
        }

        notificationUtils.getManager().notify(uniqueID, notification)
    }

    private fun setIsEnabledToFalse(alarmId: String?) {
        if (alarmId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                repo.isEnabled(id = UUID.fromString(alarmId), isEnabled = false)
            }
        }
    }
}