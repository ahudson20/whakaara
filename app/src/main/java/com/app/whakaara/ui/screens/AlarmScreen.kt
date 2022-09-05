package com.app.whakaara.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.whakaara.logic.MainViewModel


@Composable
fun AlarmScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val list = viewModel.alarms
    println("alarm list: $list")
    Text(
        text = "Alarm Screen",
        color = Color.Green,
    )
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    AlarmScreen()
}