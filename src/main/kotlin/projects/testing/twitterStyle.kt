package projects.testing

import p5.Sketch
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
        val downloadButton = createButton("D")
        val favoriteButton = createButton("F")
        val shareButton    = createButton("S")
        Layout {
            Column {
                container.apply {
                    style("background-color", "#AAAADD")
                }
                add(sketch) {
                    style("margin-left", 20.px())
                    style("margin-top", 10.px())
                    style("margin-right", 20.px())
                    style("margin-bottom", 10.px())
                }
                Row {
                    ItemStyle {
                        style("width", 60.px())
                        style("height", 20.px())
                        style("margin-left", 20.px())
                        style("margin-top", 10.px())
                        style("margin-right", 20.px())
                        style("margin-bottom", 10.px())
                    }
                    add(downloadButton)
                    add(favoriteButton)
                    add(shareButton)
                }
            }
        }

    }

}

fun twitterStyle() = Sketch {
    Setup {
        val canvas = createCanvas().apply {
            hide()
        }

        val cards = Array(7) {
            twitterCardWrapper(twitterCard(it/7.0))
         }

        Layout {
            Column {
                addAll(cards) {
                    style("margin-top", "200px")
                    style("margin-bottom", "200px")
                }
            }
        }
        updateLayout()
    }
}