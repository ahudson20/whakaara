package com.app.whakaara.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.card.CardContainerSwipeToDismiss

@Composable
fun AlarmScreen(
    viewModel: MainViewModel,
) {
    val alarms by viewModel.uiState.collectAsStateWithLifecycle()
    CardContainerSwipeToDismiss(
        alarms = alarms,
        delete = viewModel::delete,
        disable = viewModel::disable,
        enable = viewModel::enable,
        reset = viewModel::reset,
    )
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    AlarmScreen(viewModel = hiltViewModel())
}