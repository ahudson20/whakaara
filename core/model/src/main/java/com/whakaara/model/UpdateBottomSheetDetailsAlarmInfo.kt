package com.whakaara.model

data class UpdateBottomSheetDetailsAlarmInfo(
    val updateIsVibrationEnabled: BooleanStateEvent = BooleanStateEvent(),
    val updateIsSnoozeEnabled: BooleanStateEvent = BooleanStateEvent(),
    val updateDeleteAfterGoesOff: BooleanStateEvent = BooleanStateEvent(),
    val updateTitle: StringStateEvent = StringStateEvent(),
    val updateRepeatDaily: BooleanStateEvent = BooleanStateEvent(),
    val updateCheckedList: ListStateEvent = ListStateEvent()
)
