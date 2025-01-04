package com.whakaara.core.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import com.whakaara.core.WidgetUpdater
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WidgetUpdaterImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : WidgetUpdater {
    override fun updateWidget() {
        val intent = Intent(context, AppWidgetReceiver::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        context.sendBroadcast(intent)
    }
}
