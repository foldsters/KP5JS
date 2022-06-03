package scripts.testing

import p5.NativeP5
import p5.Sketch
import kotlin.math.*

fun perlinNoiseTesting() = Sketch {


    setup {

        createCanvas(512, 512)
        background(0)
        frameRate(60)
        stroke(255, 10)
        strokeWeight(1)
        val centerVec = createVector(0.5, 0.5)

            //draw {
            for (x in 0 until width) {
                for (y in 0 until height) {

                    val fx = 10.0*x/width.toDouble()
                    val fy = 10.0*y/height.toDouble()

                    val nx = noise(fx, fy).toDouble()
                    val ny = noise(fx+55.1, fy+21.8).toDouble()

                    val zx = (nx - 0.46)/0.13
                    val zy = (ny - 0.46)/0.13

                    val U1 = exp(-(zx*zx + zy*zy)*0.5)
                    val U2 = cos(atan2(zy, zx)+PI)

                    val U2P = abs(U2+1.0)/2.0

                    stroke(255*U2P)

                    val v = createVector(0.25 + x*0.5, 0.25 + y*0.5)

                    point(v.x, v.y)
                }
            }
        //}

    }

}