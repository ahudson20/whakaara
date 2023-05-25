package com.app.whakaara

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.screens.MainScreen
import com.app.whakaara.ui.theme.WhakaaraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalLayoutApi
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhakaaraTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
