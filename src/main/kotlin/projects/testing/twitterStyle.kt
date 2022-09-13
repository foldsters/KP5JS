package projects.testing

import p5.Sketch
import p5.core.Button
import p5.core.P5
import kotlin.math.cos
import kotlin.math.sin

fun twitterCard(offset: Double) = Sketch {
    Setup {
        val canvas = createCanvas(260, 260)
        background(255, 0, 128)
        Draw {
            background(127*cos(offset+millis()/2000.0)+128, 127*sin(offset+millis()/2000.0)+128, 255)
        }
        Layout {
            add(canvas)
        }
    }
}

fun twitterCardWrapper(sketch: P5) = Sketch {

    Setup {
        val downloadButton = createButton("")
        val favoriteButton = createButton("")
        val shareButton    = createButton("")
        noCanvas()
        Layout {
            GridStyle(inherit = false) {
                style("background-color", "#AAAADD")
                style("padding", 20.px().repeat(4))
            }
            Column {
                add(sketch)
                Row { // Buttons
                    GridStyle(false) {
                        style("margin-top", "20px")
                        style("justify-content", "space-between")
                        style("width", "100%")
                    }
                    fun buttonStack(button: Button, path: String, altText: String) {
                        Stack {
                            GridStyle {
                                style("justify-items", "center")
                            }
                            add(button) {
                                size(60, 40, 0.1)
                            }
                            add(createImg(path, altText)) {
                                size(40, 40)
                            }
                        }
                    }
                    buttonStack(downloadButton, "icons/download.png", "download icon")
                    buttonStack(favoriteButton, "icons/star.png", "favorite icon")
                    buttonStack(shareButton, "icons/share.png", "share icon")
                }
            }
        }
    }
}

fun twitterStyle() = Sketch {
    Setup {
        noCanvas()
        val cards = Array(7) {
            twitterCardWrapper(twitterCard(it/3.0).p5)
         }

        Layout {
            Column {
                addAll(cards) {
                    style("margin-bottom", "20px")
                }
            }
        }
        updateLayout()
    }
}