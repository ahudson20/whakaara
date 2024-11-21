package com.whakaara.model

data class BooleanStateEvent(
    val value: Boolean = false,
    val onValueChange: (Boolean) -> Unit = {}
)
