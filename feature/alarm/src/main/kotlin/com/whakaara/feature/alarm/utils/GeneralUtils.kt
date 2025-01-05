package com.whakaara.feature.alarm.utils

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.whakaara.model.alarm.Alarm

class GeneralUtils {
    companion object {
        fun Context.showToast(
            message: String,
            length: Int = Toast.LENGTH_LONG
        ) {
            Toast.makeText(this, message, length).show()
        }

        fun convertAlarmObjectToString(alarm: Alarm): String {
            return Gson().toJson(alarm)
        }

        fun convertStringToAlarmObject(string: String?): Alarm {
            return Gson().fromJson(string, Alarm::class.java)
        }
    }
}
