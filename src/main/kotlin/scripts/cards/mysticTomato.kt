package scripts.cards

import p5.P5
import p5.Sketch
import kotlin.math.*

fun mysticTomato() = Sketch {
    Setup {
        createCanvas(1920, 1080)
        colorMode(P5.ColorMode.HSB, 1, 1, 1, 255)
        var y = -1
        Draw {
            withPixels {
                if (y++ == height-1) noLoop()
                repeat(width) { x ->
                    val n = noise(x*2.0/height, y*2.0/height)
                    val b = abs(2.0*((n*20.0 % 1.0) - 0.5).toDouble()).pow(2)
                    val h = noise(x*2.0/height+0.1, y*2.0/height+0.32)
                    colorArray[x, y] = color((h*2.0)%1.0, 1, b)
                }
            }
        }
    }
}