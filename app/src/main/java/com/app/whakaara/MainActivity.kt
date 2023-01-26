package com.app.whakaara

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.data.Alarm
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.bottomsheet.BottomSheet
import com.app.whakaara.ui.navigation.BottomNavigation
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.google.accompanist.permissions.*
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.*

@AndroidEntryPoint
@ExperimentalLayoutApi
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhakaaraTheme {
                Main(viewModel = viewModel)
            }
        }
    }
}

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class
)
@Composable
private fun Main(
    viewModel: MainViewModel
) {

    val notificationPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState = scaffoldState.snackbarHostState
    val isDialogShown = rememberSaveable { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val context = LocalContext.current.applicationContext


    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
        if (wasGranted) {
            isDialogShown.value = !isDialogShown.value
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = { BottomSheet(sheetState) },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { TopBar(navController = navController) },
            bottomBar = { BottomNavigation(navController = navController) },
            floatingActionButton = {
                /** Only show FAB when on alarm screen **/
                when (navBackStackEntry?.destination?.route) {
                    "alarm" -> {
                        FloatingActionButton(
                            onClick = {
                                /**PERMISSION GRANTED**/
                                when (notificationPermissionState.status) {
                                    PermissionStatus.Granted -> {
                                        isDialogShown.value = !isDialogShown.value
                                    }
                                    else -> {
                                        /**PERMISSION DENIED - SHOW PROMPT**/
                                        if (notificationPermissionState.status.shouldShowRationale) {
                                            scope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Notification permission required",
                                                    actionLabel = "Go to settings",
                                                    duration = SnackbarDuration.Long
                                                )
                                                /**PROMPT ACCEPTED**/
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    val intent = Intent(
                                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                                    ).apply {
                                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        data = Uri.fromParts("package", context.packageName, null)
                                                    }
                                                    context.startActivity(intent)
                                                }
                                            }
                                        } else {
                                            /**FIRST TIME ACCESSING**/
                                            /**OR USER DOESN'T WANT TO BE ASKED AGAIN**/
                                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Alarm",
                            )
                        }

                        if (isDialogShown.value) {
                            TimePickerDialog(
                                onDismissRequest = { isDialogShown.value = false },
                                initialTime = LocalTime.now().noSeconds(),
                                onTimeChange = {
                                    viewModel.create(
                                        Alarm(
                                            hour = it.hour,
                                            minute = it.minute,
                                            title = null,
                                            subTitle = null,
                                            vibration = true,
                                        )
                                    )
                                    isDialogShown.value = false
                                },
                                title = { Text(text = "Select time") },
                                is24HourFormat = true,
                            )
                        }

                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavGraph(navController = navController, viewModel = viewModel)
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
