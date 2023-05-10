package com.app.whakaara.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.app.whakaara.data.Alarm
import com.google.gson.Gson

class GeneralUtils {
    companion object {
        fun showToast(title: String, context: Context) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
            }
        }

        /**
         * Cant pass parcelize object to a BroadcastReceiver inside a PendingIntent extra.
         * Going to convert the object to a string to pass to the receiver.
         * https://issuetracker.google.com/issues/36914697
         * */
        fun convertAlarmObjectToString(alarm: Alarm): String {
            return Gson().toJson(alarm)
        }

        fun convertStringToAlarmObject(string: String?): Alarm {
            return Gson().fromJson(string, Alarm::class.java)
        }

    }
}