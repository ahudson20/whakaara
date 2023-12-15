package com.app.whakaara.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    route: String
) {
    TopAppBar(
        title = {
            Text(
                text = route
            )
        }
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TopBarPreview() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    navBackStackEntry?.destination?.route = "alarm"
    WhakaaraTheme {
        TopBar(
            route = "Alarm"
        )
    }
}
