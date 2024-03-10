package com.app.whakaara.ui.bottomsheet.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.app.whakaara.R
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun BottomSheetDetailsTopBar(
    modifier: Modifier = Modifier,
    bottomText: String,
    title: String
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        BottomSheetTitle(
            title = title,
            bottomText = bottomText
        )
    }
}

@Composable
private fun BottomSheetTitle(
    modifier: Modifier = Modifier,
    title: String?,
    bottomText: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxHeight()
    ) {
        Text(
            text = if (!title.isNullOrBlank()) {
                title.toString()
            } else {
                stringResource(id = R.string.bottom_sheet_title)
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = bottomText,
            style = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetTopBarPreview() {
    WhakaaraTheme {
        BottomSheetDetailsTopBar(
            bottomText = "bottomText",
            title = "title"
        )
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun BottomSheetTitlePreview() {
    WhakaaraTheme {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            BottomSheetTitle(
                title = "title",
                bottomText = "Off"
            )
        }
    }
}
