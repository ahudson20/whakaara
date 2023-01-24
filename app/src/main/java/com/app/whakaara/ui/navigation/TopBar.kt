package com.app.whakaara.ui.navigation

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    var context = LocalContext.current

    TopAppBar(
        title = {
            Text(
                text = navBackStackEntry?.destination?.route.toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                color = Color.Black
            )
        },
        actions = {
            when (navBackStackEntry?.destination?.route) {
                "alarm" -> {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "~~replace this later~~", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "create alarm")
                    }
                }
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