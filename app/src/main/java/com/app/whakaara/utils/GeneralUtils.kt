package com.app.whakaara.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class GeneralUtils {
    companion object {
        fun showToast(title: String, context: Context) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
            }
        }
    }
}