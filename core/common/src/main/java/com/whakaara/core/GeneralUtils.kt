package com.whakaara.core

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.whakaara.core.constants.GeneralConstants

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
            return GeneralConstants.RINGTONE_NONE_SELECTED
        }

        fun convertStringToColour(string: String): Color {
            return Gson().fromJson(string, Color::class.java)
        }

        fun Float.toColorInt(): Int = (this * 255 + 0.5f).toInt()

        fun parseOrDefault(path: String): Uri {
            return if (path.isNotEmpty()) Uri.parse(path) else Settings.System.DEFAULT_ALARM_ALERT_URI
        }
    }
}
