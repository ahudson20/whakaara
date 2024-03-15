package com.app.whakaara.widget

import android.content.Context
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.unit.ColorProvider
import com.app.whakaara.R
import com.app.whakaara.activities.MainActivity
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.receiver.AppWidgetReceiver
import com.app.whakaara.ui.theme.Spacings.space10
import com.app.whakaara.ui.theme.Spacings.space40
import com.app.whakaara.ui.theme.Spacings.spaceXSmall
import com.app.whakaara.ui.theme.WidgetTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppWidget : GlanceAppWidget() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AlarmRepositoryEntryPoint {
        fun alarmRepository(): AlarmRepository
    }

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appContext = context.applicationContext ?: throw IllegalStateException()
        val alarmEntryPoint = EntryPointAccessors.fromApplication(
            appContext,
            AlarmRepositoryEntryPoint::class.java
        )
        val repository = alarmEntryPoint.alarmRepository()
        var listOfAlarms: List<Alarm>

        withContext(Dispatchers.IO) {
            listOfAlarms = repository.getAllAlarms()
        }

        provideContent {
            val prefs = currentState<Preferences>()
            val deserializedList = prefs[AppWidgetReceiver.allAlarmsKey] ?: ""
            val nextAlarm = if (deserializedList.isNotBlank()) {
                Gson().fromJson(deserializedList, object : TypeToken<List<Alarm>>() {}.type)
            } else {
                listOfAlarms
            }.filter { it.isEnabled }.minByOrNull { it.date.timeInMillis }

            GlanceTheme(colors = WidgetTheme.colors) {
                NextAlarm(nextAlarm = nextAlarm)
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun NextAlarm(nextAlarm: Alarm?) {
        Row(
            modifier = GlanceModifier
                .padding(all = spaceXSmall)
                .fillMaxWidth()
                .fillMaxHeight()
                .background(R.color.dark_green)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = GlanceModifier
                    .defaultWeight()
                    .padding(start = space10),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (nextAlarm == null) {
                    GlanceText(
                        text = LocalContext.current.getString(R.string.next_alarm_widget_none),
                        font = R.font.azeretmono,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                } else {
                    GlanceText(
                        text = LocalContext.current.getString(R.string.next_alarm_widget_title),
                        font = R.font.azeret_mono_medium,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    GlanceText(
                        text = nextAlarm.subTitle,
                        font = R.font.azeret_mono_medium,
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    GlanceText(
                        text = nextAlarm.title.take(15),
                        font = R.font.azeret_mono_medium,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            Image(
                modifier = GlanceModifier.size(space40),
                contentDescription = LocalContext.current.getString(R.string.next_alarm_widget_icon_description),
                provider = ImageProvider(R.drawable.outline_alarm_24),
                contentScale = ContentScale.FillBounds,
                colorFilter = ColorFilter.tint(ColorProvider(Color.White))
            )
        }
    }
}
