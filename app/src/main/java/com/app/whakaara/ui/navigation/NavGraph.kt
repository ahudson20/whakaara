package com.app.whakaara.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.screens.AlarmScreen
import com.app.whakaara.ui.screens.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController, viewModel:  MainViewModel) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Alarm.route
    ) {

        composable(
            route = BottomNavItem.Alarm.route
        ) {
            AlarmScreen(viewModel)
        }

        composable(
            route = BottomNavItem.Settings.route
        ) {
            SettingsScreen()
        }

    }
}