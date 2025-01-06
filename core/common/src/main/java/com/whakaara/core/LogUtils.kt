package com.whakaara.core

import android.util.Log

object LogUtils {
    private val Any.logTag: String
        get() = this::class.java.simpleName

    fun Any.logE(message: String, throwable: Throwable? = null) {
        Log.e(logTag, message, throwable)
    }

    fun Any.logD(message: String, throwable: Throwable? = null) {
        Log.d(logTag, message, throwable)
    }

    fun Any.logI(message: String) {
        Log.i(logTag, message)
    }

    fun Any.logW(message: String, throwable: Throwable? = null) {
        Log.w(logTag, message, throwable)
    }
}
