package com.example.taskmanager.presentation.ui.components

import DragDropState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.taskmanager.domain.model.Task
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeableTaskItem(
    task: Task,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit,
    dragDropState: DragDropState,
    index: Int,
    modifier: Modifier = Modifier
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val scope = rememberCoroutineScope()
    val width = with(LocalDensity.current) { 100.dp.toPx() }

    val anchors = mapOf(
        0f to 0,
        -width to -1, // Swipe left for Complete
        width to 1    // Swipe right for Delete
    )

    val animatedOffsetY by animateFloatAsState(
        targetValue = if (dragDropState.draggingItemIndex == index && dragDropState.isDragging) {
            dragDropState.dragOffset.y
        } else {
            0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    Box(modifier = modifier) {
        // Determine background color and text visibility based on swipe offset
        val swipeOffset = swipeableState.offset.value
        val backgroundColor by animateColorAsState(
            when {
                swipeOffset > 0 -> Color.Red.copy(alpha = (swipeOffset / width).coerceIn(0f, 0.2f))
                swipeOffset < 0 -> Color.Green.copy(
                    alpha = (-swipeOffset / width).coerceIn(
                        0f,
                        0.2f
                    )
                )

                else -> Color.Transparent
            }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(backgroundColor)
                .padding(horizontal = 20.dp)
        ) {
            // Show text based on swipe direction as soon as swipe starts
            if (swipeOffset > 0) {
                Text(
                    text = "Delete",
                    color = Color.Red.copy(alpha = (swipeOffset / width).coerceIn(0f, 1f)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            } else if (swipeOffset < 0) {
                Text(
                    text = "Complete",
                    color = Color.Green.copy(alpha = (-swipeOffset / width).coerceIn(0f, 1f)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }

        TaskItem(
            task = task,
            onClick = onClick,
            modifier = Modifier
                .swipeable(
                    state = swipeableState,
                    anchors = anchors,
                    thresholds = { _, _ -> FractionalThreshold(1.0f) },
                    orientation = Orientation.Horizontal
                )
                .offset {
                    IntOffset(
                        swipeableState.offset.value.roundToInt(),
                        animatedOffsetY.roundToInt()
                    )
                }
                .graphicsLayer {
                    if (dragDropState.draggingItemIndex == index && dragDropState.isDragging) {
                        shadowElevation = 8.dp.toPx()
                        scaleX = 1.05f
                        scaleY = 1.05f
                    }
                }
        )

        LaunchedEffect(swipeableState.targetValue) {
            if (swipeableState.targetValue != 0) {
                when (swipeableState.targetValue) {
                    1 -> onDelete()
                    -1 -> onComplete()
                }
                scope.launch {
                    swipeableState.animateTo(0)
                }
            }
        }
    }
}