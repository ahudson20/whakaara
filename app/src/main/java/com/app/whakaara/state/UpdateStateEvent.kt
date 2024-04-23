package com.app.whakaara.state

import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours

data class StringStateEvent(
    val value: String = "",
    val onValueChange: (String) -> Unit = {}
)

data class BooleanStateEvent(
    val value: Boolean = false,
    val onValueChange: (Boolean) -> Unit = {}
)

data class HoursUpdateEvent(
    val value: Hours = FullHours(0, 0),
    val onValueChange: (Hours) -> Unit = {}
)

data class ListStateEvent(
    val value: MutableList<Int> = mutableListOf(),
    val onValueChange: (Int) -> Unit = {}
)

data class UpdateBottomSheetDetailsAlarmInfo(
    val updateIsVibrationEnabled: BooleanStateEvent = BooleanStateEvent(),
    val updateIsSnoozeEnabled: BooleanStateEvent = BooleanStateEvent(),
    val updateDeleteAfterGoesOff: BooleanStateEvent = BooleanStateEvent(),
    val updateTitle: StringStateEvent = StringStateEvent(),
    val updateRepeatDaily: BooleanStateEvent = BooleanStateEvent(),
    val updateCheckedList: ListStateEvent = ListStateEvent()
)
