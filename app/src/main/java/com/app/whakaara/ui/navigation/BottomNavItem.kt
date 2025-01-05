package com.app.whakaara.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import com.whakaara.core.LeafScreen
import com.whakaara.core.RootScreen

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String, var rootScreen: RootScreen) {
    data object Alarm : BottomNavItem(title = "Alarm", icon = Icons.Outlined.Alarm, route = LeafScreen.Alarm.route, rootScreen = RootScreen.Alarm)

    data object Settings : BottomNavItem(title = "Settings", icon = Icons.Outlined.Settings, route = LeafScreen.Settings.route, rootScreen = RootScreen.Settings)

    data object Timer : BottomNavItem(title = "Timer", icon = Icons.Outlined.HourglassEmpty, route = LeafScreen.Timer.route, rootScreen = RootScreen.Timer)

    data object Stopwatch : BottomNavItem(title = "Stopwatch", icon = Icons.Outlined.Timer, route = LeafScreen.Stopwatch.route, rootScreen = RootScreen.Stopwatch)
}
