package com.app.whakaara.widget

import android.content.Context
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
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
import com.app.whakaara.activities.WidgetConfig
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

    override val sizeMode = SizeMode.Responsive(
        setOf(
            ONE_BY_ONE,
            TWO_BY_ONE,
            SMALL_SQUARE,
            HORIZONTAL_RECTANGLE,
            BIG_SQUARE
        )
    )

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
            val deserializedBackgroundColour = prefs[WidgetConfig.backgroundKey] ?: ""
            val deserializedTextColour = prefs[WidgetConfig.textKey] ?: ""

            val backgroundColour: Color = if (deserializedBackgroundColour.isNotBlank()) {
                Gson().fromJson(deserializedBackgroundColour, Color::class.java)
            } else {
                Color(appContext.getColor(R.color.dark_green))
            }
            val textColour = if (deserializedTextColour.isNotBlank()) {
                Gson().fromJson(deserializedTextColour, Color::class.java)
            } else {
                Color.White
            }

            val nextAlarm = if (deserializedList.isNotBlank()) {
                Gson().fromJson(deserializedList, object : TypeToken<List<Alarm>>() {}.type)
            } else {
                listOfAlarms
            }.filter { it.isEnabled }.minByOrNull { it.date.timeInMillis }

            GlanceTheme(colors = WidgetTheme.colors) {
                NextAlarm(
                    nextAlarm = nextAlarm,
                    textColour = textColour,
                    backgroundColor = backgroundColour
                )
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun NextAlarm(
        nextAlarm: Alarm?,
        textColour: Color,
        backgroundColor: Color
    ) {
        val size = LocalSize.current
        Row(
            modifier = GlanceModifier
                .padding(all = spaceXSmall)
                .fillMaxWidth()
                .fillMaxHeight()
                .background(backgroundColor)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (size.width <= ONE_BY_ONE.width) {
                Image(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .size(space40),
                    contentDescription = LocalContext.current.getString(R.string.widget_next_alarm_icon_description),
                    provider = ImageProvider(R.drawable.outline_alarm_24),
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(ColorProvider(textColour))
                )
            } else {
                Column(
                    modifier = GlanceModifier
                        .padding(start = space10, end = space10),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (nextAlarm == null) {
                        GlanceText(
                            modifier = GlanceModifier.fillMaxWidth(),
                            text = LocalContext.current.getString(R.string.widget_next_alarm_none),
                            font = R.font.azeretmono,
                            fontSize = 26.sp,
                            color = textColour
                        )
                    } else {
                        GlanceText(
                            modifier = GlanceModifier.fillMaxWidth(),
                            text = nextAlarm.subTitle.filterNot { it.isWhitespace() },
                            font = R.font.azeret_mono_medium,
                            fontSize = 26.sp,
                            color = textColour
                        )
                        if (size.width > TWO_BY_ONE.width) {
                            GlanceText(
                                modifier = GlanceModifier.fillMaxWidth(),
                                text = nextAlarm.title,
                                font = R.font.azeret_mono_medium,
                                fontSize = 20.sp,
                                color = textColour
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val ONE_BY_ONE = DpSize(57.dp, 102.dp)
        private val TWO_BY_ONE = DpSize(130.dp, 102.dp)
        private val SMALL_SQUARE = DpSize(100.dp, 100.dp)
        private val HORIZONTAL_RECTANGLE = DpSize(200.dp, 100.dp)
        private val BIG_SQUARE = DpSize(250.dp, 250.dp)
    }
}
