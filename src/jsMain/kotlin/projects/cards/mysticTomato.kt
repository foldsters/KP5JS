package projects.cards

import p5.Sketch
import p5.core.ColorMode
import kotlin.math.*

@JsExport
fun mysticTomato() = Sketch {
  Setup {
    createCanvas(1920, 1080)
    colorMode(ColorMode.HSB, 1, 1, 1, 255)
    var y = -1
    pixelDensity(1)
    console.log(width, height)
    Draw {
      withPixels {
        if (y++ == height-1) noLoop()
        repeat(width) { x ->
          val n = noise(x*2.0/height, y*2.0/height)
          val b = abs(2.0*((n*20.0 % 1.0) - 0.5)).pow(2)
          val h = noise(x*2.0/height+0.1, y*2.0/height+0.32)
          colorArray[x, y] = color((h*2.0)%1.0, 1, b)
        }
      }
    }
  }
}