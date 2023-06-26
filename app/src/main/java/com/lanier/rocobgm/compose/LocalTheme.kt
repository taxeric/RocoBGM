package com.lanier.rocobgm.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Created by 幻弦让叶
 * on 2023/6/26
 */
@Composable
fun ExtendTheme(
    content: @Composable () -> Unit
) {
    val theme = if (isSystemInDarkTheme()) extendDarkTextColorTheme else extendLightTextColorTheme
    CompositionLocalProvider(
        LocalTextColorState provides theme
    ) {
        content.invoke()
    }
}

object ExtendTheme {

    val colors: TextColor
        @Composable
        get() = LocalTextColorState.current
}
