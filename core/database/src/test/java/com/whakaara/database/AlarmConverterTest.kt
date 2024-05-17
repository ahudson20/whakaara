package com.whakaara.database

import com.whakaara.database.alarm.converters.AlarmConverter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class AlarmConverterTest {
    private lateinit var alarmConverter: AlarmConverter

    @Before
    fun setUp() {
        alarmConverter = AlarmConverter()
    }

    @Test
    fun `from timestamp`() {
        val date = alarmConverter.fromTimestamp(value = 123123L)
        assertEquals(Calendar.getInstance().apply { timeInMillis = 123123L }, date)
    }

    @Test
    fun `to timestamp`() {
        val timeStamp = alarmConverter.dateToTimestamp(Calendar.getInstance().apply { timeInMillis = 123123L })
        assertEquals(123123L, timeStamp)
    }

    @Test
    fun `from string`() {
        val list: MutableList<Int> = alarmConverter.fromString(value = "[1,2,3,4,5]")
        assertEquals(5, list.size)
    }

    @Test
    fun `to string`() {
        val string = alarmConverter.listToString(mutableListOf(1, 2, 3, 4, 5))
        assertEquals("[1,2,3,4,5]", string)
    }
}
