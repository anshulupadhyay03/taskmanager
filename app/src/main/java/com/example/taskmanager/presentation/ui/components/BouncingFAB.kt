package com.example.taskmanager.presentation.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun BouncingFAB(onClick: () -> Unit) {
    val scale = remember { Animatable(1f) }
    var clickTrigger by remember { mutableIntStateOf(0) } // Use var to allow mutation

    // Bounce animation on click
    LaunchedEffect(clickTrigger) {
        if (clickTrigger > 0) { // Only animate if clicked
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = 100, easing = EaseOut)
            )
            scale.animateTo(
                targetValue = 0.9f,
                animationSpec = tween(durationMillis = 100, easing = EaseIn)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 100, easing = EaseInOut)
            )
        }
    }

    FloatingActionButton(
        onClick = {
            clickTrigger++ // Increment to trigger animation
            onClick() // Call the original onClick action
        },
        modifier = Modifier.scale(scale.value)
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Task")
    }
}