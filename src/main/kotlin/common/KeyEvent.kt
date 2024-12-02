package common

import androidx.compose.ui.input.key.*

fun KeyEvent.isCopyPressed(): Boolean {
   return key == Key.Copy || (isCtrlPressed && key == Key.C)
}