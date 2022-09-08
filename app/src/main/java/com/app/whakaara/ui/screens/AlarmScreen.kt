package com.app.whakaara.ui.screens

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.card.CardContainerSwipeToDismiss

@Composable
fun AlarmScreen(
    scaffoldState: ScaffoldState,
    viewModel: MainViewModel = hiltViewModel(),
) {
    /***
     * Passing the WHOLE VM down.
     * I want to pass JUST the delete from the VM,
     * but need to figure out how to pass the coroutine down.
     * **/
    CardContainerSwipeToDismiss(viewModel, scaffoldState)
}

@Preview(showBackground = true)
@Composable
fun AlarmScreenPreview() {
    AlarmScreen(rememberScaffoldState())
}