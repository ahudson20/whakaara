package com.whakaara.database.alarm.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        return value?.let { Calendar.getInstance().apply { timeInMillis = it } }
    }

    @TypeConverter
    fun dateToTimestamp(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }

    @TypeConverter
    fun fromString(value: String): MutableList<Int> {
        return Gson().fromJson(value, object : TypeToken<ArrayList<Int>>() {}.type)
    }

    @TypeConverter
    fun listToString(value: MutableList<Int>): String {
        return Gson().toJson(value)
    }
}
