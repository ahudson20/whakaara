package com.app.whakaara.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.whakaara.ui.screens.AlarmScreen
import com.app.whakaara.ui.screens.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Alarm.route
    ) {

        composable(
            route = BottomNavItem.Alarm.route
        ) {
            AlarmScreen()
        }

        composable(
            route = BottomNavItem.Settings.route
        ) {
            SettingsScreen()
        }

    }
}