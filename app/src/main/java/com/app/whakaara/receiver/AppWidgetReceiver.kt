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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            observeData(context = context)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun observeData(context: Context) {
        // https://issuetracker.google.com/issues/257513022
        // goAsync is still the best way to run suspend functions from broadcast receivers
        // but it throws exception when initially creating the widget. Works fine once widget created + updated.
        GlobalScope.launch(Dispatchers.IO) {
            val alarms = alarmRepository.getAllAlarms()
            val serializedList = Gson().toJson(alarms)
            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(glanceAppWidget.javaClass)
            glanceIds.forEach { glanceId ->
                updateAppWidgetState(
                    context = context,
                    definition = PreferencesGlanceStateDefinition,
                    glanceId = glanceId
                ) { prefs ->
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
    }
}
