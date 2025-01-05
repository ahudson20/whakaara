package com.app.whakaara.activities

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.whakaara.R
import com.app.whakaara.logic.WidgetConfigViewModel
import com.app.whakaara.ui.widget.ColourPicker
import com.whakaara.core.designsystem.theme.Spacings.space10
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.data.datastore.PreferencesDataStoreRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WidgetConfig : ComponentActivity() {
    private val viewModel: WidgetConfigViewModel by viewModels()
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    @Inject
    lateinit var preferencesDataStoreRepository: PreferencesDataStoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            val textColor by viewModel.textColor.collectAsStateWithLifecycle()
            val backgroundColor by viewModel.backgroundColor.collectAsStateWithLifecycle()

            if (textColor != null && backgroundColor != null) {
                WidgetConfigScreen(
                    initialTextColor = textColor!!,
                    initialBackgroundColor = backgroundColor!!,
                    onSave = { bgColor, txtColor ->
                        viewModel.saveWidgetConfig(bgColor, txtColor, appWidgetId)
                        setResult(
                            RESULT_OK,
                            Intent().apply {
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                            }
                        )
                        finish()
                    },
                    onCancel = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun WidgetConfigScreen(
    initialTextColor: Color,
    initialBackgroundColor: Color,
    onSave: (Color, Color) -> Unit,
    onCancel: () -> Unit
) {
    val alphaBackground = rememberSaveable { mutableFloatStateOf(initialBackgroundColor.alpha) }
    val redBackground = rememberSaveable { mutableFloatStateOf(initialBackgroundColor.red) }
    val greenBackground = rememberSaveable { mutableFloatStateOf(initialBackgroundColor.green) }
    val blueBackground = rememberSaveable { mutableFloatStateOf(initialBackgroundColor.blue) }
    val colorBackground by remember {
        derivedStateOf {
            Color(
                redBackground.floatValue,
                greenBackground.floatValue,
                blueBackground.floatValue,
                alphaBackground.floatValue
            )
        }
    }

    val alphaText = rememberSaveable { mutableFloatStateOf(initialTextColor.alpha) }
    val redText = rememberSaveable { mutableFloatStateOf(initialTextColor.red) }
    val greenText = rememberSaveable { mutableFloatStateOf(initialTextColor.green) }
    val blueText = rememberSaveable { mutableFloatStateOf(initialTextColor.blue) }
    val colorText by remember {
        derivedStateOf {
            Color(
                redText.floatValue,
                greenText.floatValue,
                blueText.floatValue,
                alphaText.floatValue
            )
        }
    }

    WhakaaraTheme {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(spaceMedium),
                    text = stringResource(id = R.string.widget_config_background_colour),
                    style = MaterialTheme.typography.titleLarge
                )
                ColourPicker(
                    alpha = alphaBackground,
                    red = redBackground,
                    green = greenBackground,
                    blue = blueBackground,
                    color = colorBackground
                )

                Text(
                    modifier = Modifier.padding(spaceMedium),
                    text = stringResource(id = R.string.widget_config_text_colour),
                    style = MaterialTheme.typography.titleLarge
                )
                ColourPicker(
                    alpha = alphaText,
                    red = redText,
                    green = greenText,
                    blue = blueText,
                    color = colorText
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spaceMedium),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onCancel) {
                        Text(text = stringResource(id = R.string.widget_config_cancel_button))
                    }
                    Spacer(modifier = Modifier.width(space10))
                    Button(onClick = { onSave(colorBackground, colorText) }) {
                        Text(text = stringResource(id = R.string.widget_config_save_button))
                    }
                }
            }
        }
    }
}
