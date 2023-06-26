package com.lanier.rocobgm.compose

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Created by 幻弦让叶
 * on 2023/6/26
 */

val lightCommonTextColor = Color.Black
val darkCommonTextColor = Color(0xFF97D782)

val extendLightTextColorTheme = TextColor(
    commonColor = lightCommonTextColor
)
val extendDarkTextColorTheme = TextColor(
    commonColor = darkCommonTextColor
)

data class TextColor(
    val commonColor: Color
)

val LocalTextColorState = compositionLocalOf {
    TextColor(
        commonColor = Color.Unspecified
    )
}
