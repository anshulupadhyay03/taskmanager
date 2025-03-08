import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt

class DragDropState(
    private val lazyListState: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    var draggingItemIndex by mutableStateOf(-1)
    var dragOffset by mutableStateOf(Offset.Zero)
    var isDragging by mutableStateOf(false)
    private var initialItemOffset = 0 // Fixed offset of the dragged item at start
    private var itemHeight = 0 // Height of the dragged item

    fun onDragStart(offset: Offset) {
        val visibleItems = lazyListState.layoutInfo.visibleItemsInfo
        if (visibleItems.isEmpty()) return

        val touchY = offset.y + lazyListState.layoutInfo.viewportStartOffset
        val itemInfo = visibleItems.find { item ->
            touchY >= item.offset && touchY <= (item.offset + item.size)
        } ?: return

        draggingItemIndex = itemInfo.index
        initialItemOffset = itemInfo.offset
        itemHeight = itemInfo.size
        isDragging = true
        println("Drag start at $offset, index: $draggingItemIndex, initialOffset: $initialItemOffset, itemHeight: $itemHeight")
    }

    fun onDrag(offset: Offset) {
        if (!isDragging) return
        dragOffset += Offset(0f, offset.y)

        val draggedItemTop = initialItemOffset + dragOffset.y
        val draggedItemCenter = draggedItemTop + itemHeight / 2

        // Only reorder when the dragged item fully crosses another item's position
        val deltaItems = (dragOffset.y / itemHeight).roundToInt()
        val newIndex = (draggingItemIndex + deltaItems).coerceIn(0, lazyListState.layoutInfo.totalItemsCount - 1)

        if (newIndex != draggingItemIndex && newIndex in 0 until lazyListState.layoutInfo.totalItemsCount) {
            onMove(draggingItemIndex, newIndex)
            draggingItemIndex = newIndex
            // Reset dragOffset to smooth out the transition
            dragOffset = Offset(0f, dragOffset.y - (deltaItems * itemHeight))
        }

        println("Drag offset: $dragOffset, draggedTop: $draggedItemTop, draggedCenter: $draggedItemCenter, newIndex: $newIndex")
    }

    fun onDragEnd() {
        draggingItemIndex = -1
        dragOffset = Offset.Zero
        initialItemOffset = 0
        itemHeight = 0
        isDragging = false
        println("Drag ended")
    }
}

@Composable
fun rememberDragDropState(
    lazyListState: LazyListState,
    onMove: (Int, Int) -> Unit
): DragDropState {
    return remember(lazyListState) { DragDropState(lazyListState, onMove) }
}

fun Modifier.dragContainer(state: DragDropState): Modifier = this.pointerInput(Unit) {
    detectDragGesturesAfterLongPress(
        onDragStart = { offset ->
            state.onDragStart(offset)
        },
        onDrag = { change, dragAmount ->
            change.consume()
            state.onDrag(dragAmount)
        },
        onDragEnd = { state.onDragEnd() },
        onDragCancel = { state.onDragEnd() }
    )
}