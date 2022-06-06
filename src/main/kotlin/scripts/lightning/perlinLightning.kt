package scripts.lightning

import p5.NativeP5
import p5.Sketch
import kotlin.math.abs

fun perlinLightning() = Sketch {

    setup {
        createCanvas(1920, 1920)
        background(0)

        val gradientMap: Map<Number, NativeP5.Vector> = mapOf(
            0.0 to createVector(110, 30, 145),
            0.01 to createVector(100, 37, 137),
            0.011 to createVector(160, 110, 180),
            0.1 to createVector(83, 47, 143),
            0.15 to createVector(52, 40, 137),
            0.3 to createVector(40, 40, 60),
        )

        drawFor(0 until height) { y ->
            withPixels {
                repeat(width) { x ->
                    val level = abs(0.5-fractalNoise(y.toDouble()/height,
                        x.toDouble()/height, listOf(2 to 5, 4 to 2)).toDouble())
                    val backColorVec = gradientMap.interpolate(level)
                    val cloud = map(noise(22.7 + y * 10.0 / height, 84.8 + x * 5.0 / height),
                        0.4, 1.0, 0.0, 1.0, true).pow(2)
                    val colorVec = backColorVec*(1.0-cloud) + createVector(255, 255, 255)*cloud
                    colorArray[y, x] = colorVec.toColor()
                }
            }
        }
    }
}