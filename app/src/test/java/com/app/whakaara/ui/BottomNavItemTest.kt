package com.app.whakaara.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import com.app.whakaara.ui.navigation.BottomNavItem
import com.whakaara.core.RootScreen
import org.junit.Assert.assertEquals
import org.junit.Test

class BottomNavItemTest {
    @Test
    fun `default data alarm`() {
        val item = BottomNavItem.Alarm

        assertEquals(item.title, "Alarm")
        assertEquals(item.icon, Icons.Outlined.Alarm)
        assertEquals(item.route, RootScreen.Alarm.route)
    }

    @Test
    fun `default data settings`() {
        val item = BottomNavItem.Settings

        assertEquals(item.title, "Settings")
        assertEquals(item.icon, Icons.Outlined.Settings)
        assertEquals(item.route, RootScreen.Settings.route)
    }

    @Test
    fun `default data timer`() {
        val item = BottomNavItem.Timer

        assertEquals(item.title, "Timer")
        assertEquals(item.icon, Icons.Outlined.HourglassEmpty)
        assertEquals(item.route, RootScreen.Timer.route)
    }

    @Test
    fun `default data stopwatch`() {
        val item = BottomNavItem.Stopwatch

        assertEquals(item.title, "Stopwatch")
        assertEquals(item.icon, Icons.Outlined.Timer)
        assertEquals(item.route, RootScreen.Stopwatch.route)
    }
}
