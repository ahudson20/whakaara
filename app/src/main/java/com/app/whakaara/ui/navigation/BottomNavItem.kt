package com.app.whakaara.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    object Alarm: BottomNavItem(title = "Alarm", icon = Icons.Filled.Notifications, route = "alarm")
    object Settings: BottomNavItem(title = "Settings", icon = Icons.Filled.Settings, route = "settings")
    object Timer: BottomNavItem(title = "Timer", icon = Icons.Filled.Timer, route = "timer")
}
