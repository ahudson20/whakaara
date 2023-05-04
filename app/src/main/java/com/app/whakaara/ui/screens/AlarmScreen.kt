package com.app.whakaara.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.card.CardContainerSwipeToDismiss

@Composable
fun AlarmScreen(
    viewModel: MainViewModel,
) {
    CardContainerSwipeToDismiss(viewModel = viewModel)
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    AlarmScreen(hiltViewModel())
}