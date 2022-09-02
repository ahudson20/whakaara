package com.app.whakaara.ui.navigation

import android.widget.Toast
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TopBar() {
    val context = LocalContext.current
    TopAppBar(
        title = {
            Text(text = "TOP BAR")
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.Green,
        actions = {
            IconButton(
                onClick = {
                    Toast.makeText(context, "Create alarm clicked..", Toast.LENGTH_LONG).show()
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "create alarm")
            }
        }
    )
}

@Preview(showBackground = false)
@Composable
fun TopBarPreview() {
    TopBar()
}