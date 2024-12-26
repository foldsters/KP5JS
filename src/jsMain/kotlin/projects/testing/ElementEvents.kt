package projects.testing

import p5.Sketch
import p5.core.RenderMode

fun ElementEvents() = Sketch {

    Setup {
        val canvas = createCanvas(100, 100, RenderMode.WEBGL)
        val bigButton = createButton("Hello").apply {
            mouseWheel {
                console.log("wheel!", it)
                console.log(it)
                console.log(it.deltaY)
                console.log(it.screenY)
                console.log(it.wheelDeltaY)
                console.log(it.type)
            }
            mouseOverDelay(1000) {
                console.log("context menu!")
            }
        }
        val bigSlider = createSlider(0, 255, 128, 0.1)
        background(128)

        console.log("htmlCanvas", this)

        Layout {
            Column {
                add(canvas)
                add(bigButton) {
                    size(200, 200, 3)
                }
                add(bigSlider) {
                    size(200, 200, 3)
                }
            }
        }

        KeyPressed {
            background(128)
            fill(0, 255)
            rect(0, 0, keyCode, keyCode)
        }

        MouseWheel {
            console.log(this)
            console.log(deltaY)
        }
    }
}