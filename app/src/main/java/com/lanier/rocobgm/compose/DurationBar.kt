package com.lanier.rocobgm.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Created by Eric
 * on 2023/6/4
 */
@Composable
fun ComposeDuration(
    percentFlow: MutableStateFlow<Int>,
    maxPercent: Int = 100,
    progressBarStartColor: Color = Color.White,
    progressBarEndColor: Color = Color.Black,
    progressBarWidth: Dp = 7.dp,
    backgroundProgressBarColor: Color = Color.LightGray,
    backgroundProgressBarWidth: Dp = 3.dp,
    roundBorder: Boolean = false,
    startAngle: Float = 0f
) {
    val percent = percentFlow.collectAsState().value
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val target = (percent / maxPercent) * 360f
        val canvasSize = size.minDimension
        val radius = canvasSize / 2 - maxOf(backgroundProgressBarWidth, progressBarWidth).toPx() / 2
        drawCircle(
            color = backgroundProgressBarColor,
            radius = radius,
            center = size.center,
            style = Stroke(width = backgroundProgressBarWidth.toPx())
        )
        drawArc(
            brush = Brush.linearGradient(listOf(progressBarStartColor, progressBarEndColor)),
            startAngle = 270f + startAngle,
            sweepAngle = target,
            useCenter = false,
            topLeft = size.center - Offset(radius, radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(
                width = progressBarWidth.toPx(),
                cap = if (roundBorder) StrokeCap.Round else StrokeCap.Butt
            )
        )
    }
}