package com.whakaara.feature.alarm.utils

import android.content.Context
import android.widget.Toast

class GeneralUtils {
    companion object {
        fun Context.showToast(
            message: String,
            length: Int = Toast.LENGTH_LONG
        ) {
            Toast.makeText(this, message, length).show()
        }
    }
}
