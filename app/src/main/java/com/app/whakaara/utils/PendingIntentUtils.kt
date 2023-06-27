package com.app.whakaara.utils

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build

class PendingIntentUtils {
    companion object {

        fun getActivity(context: Context?, id: Int, intent: Intent?, flag: Int): PendingIntent {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(context, id, intent, FLAG_MUTABLE or flag)
            } else {
                PendingIntent.getActivity(context, id, intent, flag)
            }
        }

        fun getBroadcast(context: Context?, id: Int, intent: Intent?, flag: Int): PendingIntent {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(context, id, intent!!, FLAG_MUTABLE or flag)
            } else {
                PendingIntent.getBroadcast(context, id, intent!!, flag)
            }
        }
    }
}
