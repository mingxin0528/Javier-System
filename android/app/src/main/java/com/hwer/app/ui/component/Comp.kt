package com.hwer.app.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hwer.app.ui.theme.Main
import com.hwer.app.ui.theme.Mask
import kotlin.math.cos
import kotlin.math.sin

object Comp {
    @Composable
    fun Loading(modifier: Modifier=Modifier) {
        val degTransition = rememberInfiniteTransition(label = "rotate")
        val degree = degTransition.animateFloat(
            0f, 360f, animationSpec = InfiniteRepeatableSpec(
                tween(
                    durationMillis = 1000, easing = FastOutSlowInEasing
                )
            ), label = "rotate"
        )
        Row(
            modifier = modifier.background(Mask).rotate(degree.value),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            val canvasSize = 64.dp
            Canvas(modifier = Modifier.size(canvasSize)) {
                val count=6
                for (i in 0..count) {
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = Offset(
                            ((0.5 + cos(2 * Math.PI * i / count) * 0.4) * canvasSize.toPx()).toFloat(),
                            ((0.5 + sin(2 * Math.PI * i / count) * 0.4) * canvasSize.toPx()).toFloat()
                        )
                    )
                }
            }
        }
    }
}