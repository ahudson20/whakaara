package com.whakaara.model

data class StringStateEvent(
    val value: String = "",
    val onValueChange: (String) -> Unit = {}
)
