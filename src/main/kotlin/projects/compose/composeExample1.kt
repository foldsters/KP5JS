package projects.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import p5.Sketch

fun composeExample1() = Sketch {
    var count: Int by mutableStateOf(0)

    Setup {
        renderComposable(rootElementId = "root") {
            Column(255, 90) {
                Button(attrs = {
                    onClick { count -= 1 }
                }) {
                    Text("-")
                }

                Span({ style { padding(15.px) } }) {
                    Text("$count")
                }

                Button(attrs = {
                    onClick { count += 1 }
                }) {
                    Text("+")
                }
            }
        }
    }
}

@Composable
fun Column(columnWidth: Int, rowHeight: Int, contents: @Composable ()->Unit) {
    Div( {
        style {
            display(DisplayStyle.Grid)
            gridTemplateColumns("1fr")
            gridAutoRows("${rowHeight}px")
            justifyItems("Center")
            alignItems(AlignItems.Center)
            width(columnWidth.px)
        }  }
    ) {
        contents()
    }

}