package com.app.whakaara.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.app.whakaara.ui.theme.WhakaaraTheme

class FullScreenNotificationActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhakaaraTheme {
                Main()
            }
        }
    }

    @Composable
    private fun Main() {
        Text(text = "hello")
    }
}