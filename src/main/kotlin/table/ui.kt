package table

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import common.isCopyPressed

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun Table() {
   // var isSelecting by remember { mutableStateOf(false) }
   // val offset = remember { mutableStateOf(Offset(0f, 0f)) }

   var startCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
   var endCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }

   val cellsBounds = remember { mutableStateMapOf<Pair<Int, Int>, Rect>() }

   val selectedCells = remember<List<Pair<Int, Int>>>(startCell, endCell) {
      if (startCell == null || endCell == null) return@remember emptyList()

      val startBounds = cellsBounds[startCell] ?: return@remember emptyList()
      val endBounds = cellsBounds[endCell] ?: return@remember emptyList()

      val selectionRect = Rect(
         left = minOf(startBounds.left, endBounds.left),
         top = minOf(startBounds.top, endBounds.top),
         right = maxOf(startBounds.right, endBounds.right),
         bottom = maxOf(startBounds.bottom, endBounds.bottom),
      )

      return@remember cellsBounds.filter { (_, bounds) ->
         selectionRect.overlaps(
            bounds
         )
      }.keys.toList()
   }

   println(selectedCells)

   val data = (1..7).map { x -> (1..9).map { Position(x, y = it) } }

   Column(
      modifier = Modifier.fillMaxSize().padding(8.dp).focusable().onKeyEvent {
         println(it.key)
         if (it.isCopyPressed()) {
            println("COPY")
            if (selectedCells.isEmpty()) return@onKeyEvent false

            println(selectedCells)

            return@onKeyEvent true
         }

         return@onKeyEvent false
      },
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
   ) {
      OutlinedTextField(
         // value = offset.value.toString(),
         value = "",
         onValueChange = {},
         modifier = Modifier.fillMaxWidth(),
      )

      Spacer(Modifier.height(8.dp))

      Column(Modifier.padding(16.dp).border(16.dp, Color(0x02A8FF))
         .onPointerEvent(PointerEventType.Exit) {
            // if (isSelecting) isSelecting = false
         }) {

         data.forEach { cells ->
            Row {
               cells.forEach { cell ->
                  Column(verticalArrangement = Arrangement.Center,
                     horizontalAlignment = Alignment.CenterHorizontally,
                     modifier = Modifier.size(width = 160.dp, height = 80.dp)
                        .background(remember {
                           Color(0x02A8FF).copy(Math.random().toFloat())
                        }).border(2.dp, Color.Blue).background(
                           if (endCell == cell.x to cell.y) {
                              Color.Red
                           } else if (selectedCells.contains(cell.x to cell.y)) {
                              Color.Green.copy(0.6F)
                           } else {
                              Color.Transparent
                           }
                        ).onGloballyPositioned {
                           cellsBounds[cell.x to cell.y] = it.boundsInRoot()
                        }.pointerInput(true) {
                           detectDragGestures(onDragStart = {
                              startCell = cell.x to cell.y
                              endCell = cell.x to cell.y
                           }, onDragEnd = {
                             // println(selectedCells)
                             // startCell = null
                             // endCell = null
                           }, onDrag = { change, dragAmount ->
                              val cellBound = cellsBounds[cell.x to cell.y]
                              if (cellBound == null) return@detectDragGestures

                              // change.position is relative to the topLeft of the box not the window
                              // we should add the topLeft of the cell to the change
                              val pointerPosition =
                                 change.position + cellBound.topLeft

                              // the onDrag is likely to be called too many times we should optimize updating the state
                              if (pointerPosition in cellBound) {
                                 // if the pointer is still in the same cell, no work needed
                                 return@detectDragGestures
                              }

                              val currentHovered =
                                 cellsBounds.entries.find { (_, bounds) -> pointerPosition in bounds }?.key

                              if (currentHovered != null) {
                                 endCell = currentHovered
                              }
                           })
                        }) {
                     Text(cellsBounds[cell.x to cell.y].toString())
                  }

                  Spacer(Modifier.width(8.dp))
               }
            }

            Spacer(Modifier.height(8.dp).fillMaxWidth())
         }
      }
   }
}

private data class Position(val x: Int, val y: Int)
