package com.example.taskmanager.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskmanager.R
import com.example.taskmanager.domain.model.Priority
import com.example.taskmanager.domain.model.Task

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drag Handle
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.drag_indicator),
                contentDescription = "Drag handle",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { /* Handled by parent */ },
                            onDrag = { _, _ -> /* Handled by parent */ },
                            onDragEnd = { /* Handled by parent */ },
                            onDragCancel = { /* Handled by parent */ }
                        )
                    }
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Title (Bold and Large)
                Text(
                    text = task.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )

                // Description (Multiline)
                Text(
                    text = task.description ?: "",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority with Icon
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority Icon
                    Icon(
                        imageVector = when (task.priority) {
                            Priority.HIGH -> ImageVector.vectorResource(R.drawable.ic_priority_high)
                            Priority.MEDIUM -> Icons.Default.Info
                            Priority.LOW -> ImageVector.vectorResource(R.drawable.ic_low_priority)
                        },
                        contentDescription = "Priority Icon",
                        tint = when (task.priority) {
                            Priority.HIGH -> Color.Red
                            Priority.MEDIUM -> Color(0xFFFFA500) // Orange
                            Priority.LOW -> Color(0xff6aa84f)
                        },
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Priority Text
                    Text(
                        text = task.priority.name,
                        fontSize = 14.sp,
                        color = when (task.priority) {
                            Priority.HIGH -> Color.Red
                            Priority.MEDIUM -> Color(0xFFFFA500) // Orange
                            Priority.LOW -> Color(0xff6aa84f)
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Completion Indicator
            if (task.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color.Green,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}