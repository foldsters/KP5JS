package projects.layout

import p5.P5
import p5.Sketch

fun composeExample2() = Sketch {

    Setup {

        val size = 256

        val main = createCanvas(size*3, size*3)
        val subR = createGraphics(size*3, size*3)
        val subG = createGraphics(size*3, size*3)
        val subB = createGraphics(size*3, size*3)

        background(128, 255)
        subR.apply {
            fill(255, 128, 128, 128)
            square(0, 0, size*2)
        }
        subG.apply {
            fill(128, 255, 128, 128)
            square(size, 0, size*2)
        }
        subB.apply {
            fill(128, 128, 255, 128)
            square(0, size, size*2)
        }

        val buttonsCounter = createP("3")
        val slider = createSlider(0, 3, 3, 1).apply {
            changed {
                buttonsCounter.html(value().toInt().toString())
                updateLayout()
            }
        }

        val button1 = createButton("1")
        val button2 = createButton("2")
        val button3 = createButton("3")

        Layout {
            GridStyle { style("justify-items", "start") }
            ItemStyle { style("margin", "0px") }
            Column {
                Row {
                    Stack { addAll(main, subR.getCanvas(), subG.getCanvas(), subB.getCanvas()) }
                    Column {
                        GridStyle {
                            style("align-items", "start")
                            style("align-content", "start")
                            style("height", "100%")
                        }
                        ItemStyle { size(size*0.5, size, 3) }
                        if(slider.value() > 0) add(button1)
                        if(slider.value() > 1) add(button2)
                        if(slider.value() > 2) add(button3)
                    }
                }
                Row {
                    GridStyle {
                        style("justify-items", "center")
                        style("grid-template-columns", "6fr 1fr")
                        style("width", "100%")
                    }
                    ItemStyle {
                        style("zoom", "3")
                        style("width", "100%")
                        style("text-align", "center")
                    }
                    add(slider)
                    add(buttonsCounter)
                }
            }
        }
    }
}