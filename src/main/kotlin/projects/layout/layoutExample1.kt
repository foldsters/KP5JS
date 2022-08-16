package projects.layout

import p5.Sketch
import p5.util.*

fun layoutExample1() = Sketch {

    Setup {

        val size = 256

        val main = createCanvas(size*3, size*3)
        val subM = createGraphics()
        val subY = createGraphics()
        val subC = createGraphics()

        frameRate(30)

        val subCMY = arrayOf(subC, subM, subY)

        var updateButton: (()->Unit)? = null

        val buttonsCounter = createP("3")
        val slider = createSlider(0, 3, 3, 0.01).apply {
            changed { updateLayout() }
            input {
                buttonsCounter.html(value().toFixed(2))
                updateButton?.invoke()
            }
        }
        val sliderValue by { slider.value().toDouble() }

        val button1 = createButton("1")
        val button2 = createButton("2")
        val button3 = createButton("3")

        val helpText = createP("helpText")
        var updateHelpText: (()->Unit)? = null

        noLoop()
        Draw {
            background(255, 255)
            listOf(subM, subY, subC).map { it.clear() }
            subM.background(255,   0, 255, 128)
            subY.background(255, 255,   0, 128)
            subC.background(  0, 255, 255, 128)
        }

        Layout {
            GridStyle {
                style("justify-items", "start")
                style("align-items", "start")
            }
            ItemStyle { style("margin", 0.px()) }
            Column {
                Row {
                    Stack {
                        add(main)
                        add(subCMY[(0+sliderValue.toInt())%3].apply{ resizeCanvas(size*2, size*2) }.getCanvas())
                        add(subCMY[(1+sliderValue.toInt())%3].apply{ resizeCanvas(size*2, size*2) }.getCanvas()) { style("justify-self", "end") }
                        add(subCMY[(2+sliderValue.toInt())%3].apply{ resizeCanvas(size*3, size*2) }.getCanvas()) { style("align-self", "end") }
                    }
                    updateButton = Column {
                        GridStyle {
                            style("align-items", "start")
                            style("align-content", "start")
                            style("height", 100.percent())
                            style("width", (size*0.5).px())
                        }
                        if(sliderValue > 0) add(button1) {
                            val factor = sliderValue.coerceIn(0.0, 1.0)
                            size(size*0.5, size*factor, 5*factor)
                        }
                        if(sliderValue > 1) add(button2) {
                            val factor = (sliderValue-1).coerceIn(0.0, 1.0)
                            size(size*0.5, size*factor, 5*factor)
                        }
                        if(sliderValue > 2) add(button3) {
                            val factor = (sliderValue-2).coerceIn(0.0, 1.0)
                            size(size*0.5, size*factor, 5*factor)
                        }
                    }
                }
                Row {
                    GridStyle {
                        style("justify-items", "center")
                        style("grid-template-columns", 6.fr() + 1.fr())
                        style("width", 100.percent())
                    }
                    ItemStyle {
                        style("zoom", 2)
                        style("width", 100.percent())
                        style("text-align", "center")
                    }
                    add(slider) { style("zoom", 2) }
                    add(buttonsCounter) { style("zoom", 4) }
                }
            }

            updateHelpText = Stack {
                GridStyle {
                    style("margin-left", (mouseX + 5).px())
                    style("margin-top", (mouseY + 5).px())
                    style("background-color", "#46484A") 
                    style("color", "#A9B7C6")
                    style("height", "min-content")
                }
                add(helpText) {
                    style("zoom", 4)
                }
            }

        }

        MouseMoved {
            updateHelpText?.invoke()
        }

        MouseDragged {
            updateHelpText?.invoke()
        }
    }
}