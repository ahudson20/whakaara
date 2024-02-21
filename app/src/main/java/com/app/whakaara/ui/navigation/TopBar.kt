package com.app.whakaara.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.app.whakaara.ui.bottomsheet.settings.BottomSheetSettingsWrapper
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.dokar.sheets.rememberBottomSheetState
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    route: String
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState()

    TopAppBar(
        title = {
            Text(
                text = route
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                style = MaterialTheme.typography.headlineLarge
            )
        },
        actions = {
            when (route) {
                "alarm" -> {
                    IconButton(
                        onClick = {
                            scope.launch { sheetState.expand() }
                        }
                    ) {
                        Icon(Icons.Outlined.Settings, contentDescription = "alarm settings")
                    }
                }
            }
        }
    )

    BottomSheetSettingsWrapper(
        state = sheetState
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TopBarPreview() {
    WhakaaraTheme {
        TopBar(
            route = "alarm"
        )
    }
}
