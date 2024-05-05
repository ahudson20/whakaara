package com.app.whakaara.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun BottomNavigation(navController: NavController) {
    val navItems =
        listOf(
            BottomNavItem.Alarm,
            BottomNavItem.Timer,
            BottomNavItem.Stopwatch,
        )

    NavigationBar(
        containerColor = Color.Transparent,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                onClick = {
                    navController.navigate(item.route)

                    // Not working with deeplinks
                    // https://stackoverflow.com/questions/68456471/jetpack-compose-bottom-bar-navigation-not-responding-after-deep-linking
                    // https://github.com/android/architecture-components-samples/issues/1003
                    // https://issuetracker.google.com/issues/194301895
                    // https://slack-chats.kotlinlang.org/t/16380212/hello-i-have-a-question-related-to-deep-links-handling-with-
//                    {
//                        navController.graph.startDestinationRoute?.let { screenRoute ->
//                            popUpTo(screenRoute) {
//                                saveState = true
//                            }
//                        }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
                },
            )
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun BottomNavigationPreview() {
    WhakaaraTheme {
        BottomNavigation(
            navController = rememberNavController(),
        )
    }
}
