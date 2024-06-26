package com.app.whakaara.utility

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.whakaara.core.constants.GeneralConstants.RINGTONE_NONE_SELECTED
import com.whakaara.model.alarm.Alarm

class GeneralUtils {
    companion object {
        fun Context.showToast(
            message: String,
            length: Int = Toast.LENGTH_LONG
        ) {
            Toast.makeText(this, message, length).show()
        }

        fun Context.getNameFromUri(uri: Uri): String {
            val ringtone = RingtoneManager.getRingtone(this, uri)
            if (ringtone != null) {
                return ringtone.getTitle(this)
            }
            return RINGTONE_NONE_SELECTED
        }

        /**
         * Can't pass parcelize object to a BroadcastReceiver inside a PendingIntent extra.
         * Going to convert the object to a string to pass to the receiver.
         * https://issuetracker.google.com/issues/36914697
         * */
        fun convertAlarmObjectToString(alarm: Alarm): String {
            return Gson().toJson(alarm)
        }

        fun convertStringToAlarmObject(string: String?): Alarm {
            return Gson().fromJson(string, Alarm::class.java)
        }

        fun convertStringToColour(string: String): Color {
            return Gson().fromJson(string, Color::class.java)
        }

        fun Float.toColorInt(): Int = (this * 255 + 0.5f).toInt()
    }
}
