package com.app.whakaara.receiver

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.widget.AppWidget
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AppWidget()

    @Inject
    lateinit var alarmRepository: AlarmRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        observeData(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == UPDATE_ACTION) {
            observeData(context = context)
        }
    }

    private fun observeData(context: Context) {
        goAsync {
            val alarms = alarmRepository.getAllAlarms()
            val serializedList = Gson().toJson(alarms)
            val glanceId = GlanceAppWidgetManager(context).getGlanceIds(AppWidget::class.java).firstOrNull()
            if (glanceId != null) {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[allAlarmsKey] = serializedList
                    }
                }
                glanceAppWidget.update(context, glanceId)
            }
        }
    }

    companion object {
        val allAlarmsKey = stringPreferencesKey("all_alarms")
        const val UPDATE_ACTION = "updateAction"
    }
}
