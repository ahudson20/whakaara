package com.app.whakaara.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val showMenu = remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = navBackStackEntry?.destination?.route.toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                color = Color.Black
            )
        },
        actions = {

            IconButton(
                onClick = {
                    showMenu.value = !showMenu.value
                }
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = "options menu")
            }

            DropdownMenu(
                expanded = showMenu.value,
                onDismissRequest = {
                    showMenu.value = false
                }
            ) {
                DropdownMenuItem(text = { Text(text = "hello") }, onClick = { /*TODO*/ })
                DropdownMenuItem(text = { Text(text = "goodbye") }, onClick = { /*TODO*/ })
            }

        }
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    navBackStackEntry?.destination?.route = "alarm"
    TopBar(navController)
}