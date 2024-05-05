package com.app.whakaara.ui.clock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.app.whakaara.state.StringStateEvent
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.ui.theme.darkGreen
import com.app.whakaara.ui.theme.primaryGreen
import com.whakaara.core.constants.DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE

@Composable
fun TimerInputField(
    modifier: Modifier = Modifier,
    label: String,
    regex: String,
    updateStringEvent: StringStateEvent,
) {
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier.width(Spacings.space100),
            value = updateStringEvent.value,
            onValueChange = {
                if (it.matches(Regex(regex))) {
                    updateStringEvent.onValueChange(it)
                }
            },
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (updateStringEvent.value.isBlank()) updateStringEvent.onValueChange(TIMER_INPUT_INITIAL_VALUE)
                    },
                ),
            textStyle =
                LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                ),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = darkGreen,
                    unfocusedBorderColor = primaryGreen,
                    focusedTextColor = darkGreen,
                    unfocusedTextColor = darkGreen,
                    focusedContainerColor = primaryGreen,
                    unfocusedContainerColor = primaryGreen,
                ),
        )
        Text(text = label)
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TimerInputFieldPreview() {
    WhakaaraTheme {
        TimerInputField(
            label = "Hours",
            regex = "",
            updateStringEvent =
                StringStateEvent(
                    value = "00",
                ),
        )
    }
}
