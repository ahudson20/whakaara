package com.app.whakaara.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    object Alarm : BottomNavItem(title = "Alarm", icon = Icons.Outlined.Alarm, route = "alarm")
    object Settings : BottomNavItem(title = "Settings", icon = Icons.Outlined.Settings, route = "settings")
    object Timer : BottomNavItem(title = "Timer", icon = Icons.Outlined.HourglassEmpty, route = "timer")
    object Stopwatch : BottomNavItem(title = "Stopwatch", icon = Icons.Outlined.Timer, route = "stopwatch")
}
