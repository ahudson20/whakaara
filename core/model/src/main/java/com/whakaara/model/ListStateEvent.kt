package com.whakaara.model

data class ListStateEvent(
    val value: MutableList<Int> = mutableListOf(),
    val onValueChange: (Int) -> Unit = {}
)
