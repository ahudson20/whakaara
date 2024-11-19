package com.whakaara.core

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent

class PendingIntentUtils {
    companion object {
        fun getActivity(
            context: Context?,
            id: Int,
            intent: Intent?,
            flag: Int
        ): PendingIntent {
            return PendingIntent.getActivity(context, id, intent, FLAG_MUTABLE or flag)
        }

        fun getBroadcast(
            context: Context?,
            id: Int,
            intent: Intent?,
            flag: Int
        ): PendingIntent {
            return PendingIntent.getBroadcast(context, id, intent!!, FLAG_MUTABLE or flag)
        }

        fun getService(
            context: Context?,
            id: Int,
            intent: Intent,
            flag: Int
        ): PendingIntent {
            return PendingIntent.getService(context, id, intent, FLAG_MUTABLE or flag)
        }
    }
}
