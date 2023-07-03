package com.lanier.rocobgm.compose

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Created by 幻弦让叶
 * on 2023/6/26
 */

val lightCommonTextColor = Color.Black
val lightCommonTextColor1 = Color.Black
val lightCommonBackgroundColor = Color.White

val darkCommonTextColor = Color(0xFF97D782)
val darkCommonTextColor1 = Color(0xFFCCE6BE)
val darkCommonBackgroundColor = Color(0xFF474747)

val extendLightTextColorTheme = RocoColor(
    commonTextColor = lightCommonTextColor,
    commonTextColor1 = lightCommonTextColor1,
    commonBackgroundColor = lightCommonBackgroundColor,
)
val extendDarkTextColorTheme = RocoColor(
    commonTextColor = darkCommonTextColor,
    commonTextColor1 = darkCommonTextColor1,
    commonBackgroundColor = darkCommonBackgroundColor,
)

data class RocoColor(
    val commonTextColor: Color,
    val commonTextColor1: Color,
    val commonBackgroundColor: Color,
)

val LocalExtensionColorState = compositionLocalOf {
    RocoColor(
        commonTextColor = Color.Unspecified,
        commonTextColor1 = Color.Unspecified,
        commonBackgroundColor = Color.Unspecified,
    )
}
