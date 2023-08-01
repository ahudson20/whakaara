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
