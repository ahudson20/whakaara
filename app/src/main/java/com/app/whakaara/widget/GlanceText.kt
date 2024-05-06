package com.app.whakaara.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.TypedValue
import androidx.annotation.FontRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext

// https://medium.com/@andidu/how-to-glance-android-widgets-with-compose-4be0fcd8f6b8

@Composable
fun GlanceText(
    text: String,
    @FontRes font: Int,
    fontSize: TextUnit,
    modifier: GlanceModifier = GlanceModifier,
    color: Color = Color.Black,
    letterSpacing: TextUnit = 0.1.sp
) {
    Image(
        modifier = modifier,
        provider =
        ImageProvider(
            LocalContext.current.textAsBitmap(
                text = text,
                fontSize = fontSize,
                color = color,
                font = font,
                letterSpacing = letterSpacing.value
            )
        ),
        contentDescription = null
    )
}

private fun Context.textAsBitmap(
    text: String,
    fontSize: TextUnit,
    color: Color = Color.Black,
    letterSpacing: Float = 0.1f,
    font: Int
): Bitmap {
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = spToPx(fontSize.value, this)
    paint.color = color.toArgb()
    paint.textAlign = Paint.Align.LEFT
    paint.letterSpacing = letterSpacing
    paint.typeface = ResourcesCompat.getFont(this, font)

    val baseline = -paint.ascent()
    val width = (paint.measureText(text) + 5f).toInt()
    val height = (baseline + paint.descent() + 15f).toInt()
    val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawText(text, 0f, baseline + 10f, paint)
    return image
}

private fun spToPx(
    sp: Float,
    context: Context
): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        context.resources.displayMetrics
    )
}
