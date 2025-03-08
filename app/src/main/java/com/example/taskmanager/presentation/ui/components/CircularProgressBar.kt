package com.example.taskmanager.presentation.ui.components

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


@Composable
fun CircularProgressBarContainer(padding: PaddingValues, percentage: Float,) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = padding.calculateTopPadding(),
                start = 16.dp, // Left padding for text
                end = 16.dp    // Right padding for circle
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Tasks Progress",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier.size(32.dp) // Smaller size
        ) {
            CircularProgressBar(
                percentage = percentage,
                Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun CircularProgressBar(
    percentage: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 4.dp, // Reduced from 6.dp to 4.dp for smaller size
    backgroundColor: Color = Color.LightGray,
    progressColor: Color = MaterialTheme.colorScheme.primary
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 500, easing = EaseInOut)
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val diameter = size.minDimension
        val radius = diameter / 2f
        val sweepAngle = (animatedPercentage / 100f) * 360f

        drawCircle(
            color = backgroundColor,
            radius = radius,
            style = Stroke(width = strokeWidth.toPx())
        )

        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }

    Text(
        text = "${animatedPercentage.roundToInt()}%",
        style = MaterialTheme.typography.labelSmall, // Smaller text for 32.dp circle
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}