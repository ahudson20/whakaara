package com.app.whakaara

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.ui.bottomsheet.BottomSheet
import com.app.whakaara.ui.navigation.BottomNavigation
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
import com.app.whakaara.ui.theme.WhakaaraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalLayoutApi
@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
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
                val scaffoldState = rememberScaffoldState()

                ModalBottomSheetLayout(
                    sheetState = sheetState,
                    sheetContent = { BottomSheet( sheetState ) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = { TopBar(navController = navController, sheetState = sheetState) },
                        bottomBar = { BottomNavigation(navController = navController) }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)){
                            NavGraph(navController = navController, scaffoldState = scaffoldState)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainActivity()
}
