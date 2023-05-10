package com.app.whakaara.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.app.whakaara.data.Alarm
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.DateUtils.Companion.TWENTY_FOUR_HOUR_AM_PM
import com.app.whakaara.utils.DateUtils.Companion.toString
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ALARM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullScreenNotificationActivity: AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()

        val alarm = GeneralUtils.convertStringToAlarmObject(string = intent.getStringExtra(INTENT_EXTRA_ALARM))
        setContent {
            WhakaaraTheme {
                Main(
                   alarm = alarm
                )
            }
        }
    }

    @Composable
    private fun Main(
        alarm: Alarm
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Text(text = DateUtils.getCurrentDateTime().toString(format = TWENTY_FOUR_HOUR_AM_PM))
            Button(
                onClick = {
                    viewModel.disable(alarm = alarm)
                    finishAndRemoveTask()
                }
            ) {
                Text(text = "Dismiss alarm")
            }
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}