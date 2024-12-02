import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import table.Table

@Composable
@Preview
fun App() {
   MaterialTheme {
      Table()
   }
}

fun main() = application {
   Window(onCloseRequest = ::exitApplication, title = "Table Selection") {
      App()
   }
}
