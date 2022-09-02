package com.app.whakaara

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.ui.bottomsheet.BottomSheet
import com.app.whakaara.ui.navigation.BottomNavigation
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
import com.app.whakaara.ui.theme.WhakaaraTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhakaaraTheme {
                val navController = rememberNavController()
                val sheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
                    skipHalfExpanded = true,
                )

                val coroutineScope = rememberCoroutineScope()

                BackHandler(sheetState.isVisible) {
                    coroutineScope.launch { sheetState.hide() }
                }

                ModalBottomSheetLayout(
                    sheetState = sheetState,
                    sheetContent = { BottomSheet( sheetState ) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(
                        topBar = { TopBar(navController = navController, sheetState = sheetState) },
                        bottomBar = { BottomNavigation(navController = navController) }
                    ) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainActivity()
}
