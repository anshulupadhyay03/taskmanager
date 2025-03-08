package com.example.taskmanager.presentation.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf

import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.taskmanager.domain.model.Task

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableTaskList(tasks: List<Task>, onMove: (Int, Int) -> Unit) {
    val listState = rememberLazyListState()

    LazyColumn(state = listState) {
        items(tasks, key = { it.id }) { task ->
            DraggableTaskItem(task = task, onMove = onMove)
        }
    }
}

@Composable
fun DraggableTaskItem(task: Task, onMove: (Int, Int) -> Unit) {
    var offsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress { change, dragAmount ->
                    change.consume()
                    offsetY += dragAmount.y
                    onMove(task.id, offsetY.toInt())
                }
            }
    ) {
        Text(
            text = task.title,
            modifier = Modifier.padding(16.dp)
        )
    }
}
