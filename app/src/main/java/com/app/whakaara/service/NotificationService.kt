package com.app.whakaara.service

import android.app.AlarmManager
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.app.whakaara.data.AlarmRepository
import com.app.whakaara.receiver.Receiver
import com.app.whakaara.utils.NotificationUtils
import com.app.whakaara.utils.PendingIntentUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : Service(), LifecycleObserver {

    @Inject
    lateinit var repo: AlarmRepository

    @Inject
    lateinit var app: Application

    private val scope = CoroutineScope(Job())

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val alarmId = intent?.getStringExtra("alarmId")
            if (intent?.action.equals("cancel")) {
                if (alarmId != null) {
                    scope.launch(Dispatchers.IO) {
                        repo.isEnabled(id = UUID.fromString(alarmId), isEnabled = false)
                    }
                    // deleteAlarm(alarmId = alarmId)
                }
            } else if (intent?.action.equals("snooze")) {
                println("TODO")
            }
        } catch (exception: Exception) {
            Log.d("Notification service exception", exception.printStackTrace().toString())
        }

        return START_NOT_STICKY
    }

    private fun deleteAlarm(
        alarmId: String
    ) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, Receiver::class.java).apply {
            this.action = alarmId
        }
        val pendingIntent = PendingIntentUtils.getBroadcast(app, 0, intent, 0)

        alarmManager.cancel(pendingIntent)
    }

    private fun removeNotification(context: Context, id: Int) {
        NotificationUtils(context).getManager().cancel(id)
    }
}