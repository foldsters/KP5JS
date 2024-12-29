@file:OptIn(ExperimentalJsExport::class)

package projects.singularity

import p5.Sketch
import p5.core.AUTO
import kotlin.math.*

@JsExport
fun Singularity() = Sketch {
    Setup {
        val colors = listOf(
            color(0, 0, 0),
            color(255, 0, 77),
            color(255, 163, 0),
            color(255, 236, 39),
            color(255, 119, 168),
            color(255, 204, 127),
            color(225, 241, 232)
        )

        createCanvas(1024, 1024)
        pixelDensity(1)
        var xy = createVector(0)
        var t = 0.0

        withPixels {
            for(i in 0..width) {
                for(j in 0..width) {
                    colorArray[i, j] = color(0, 0, 0)
                }
            }
        }

        DrawWithPixels(AUTO) {
            t-=0.00000025
            var i = randInt(5)+1
            val a = atan2(sin(PI*i/2.5)-xy.y, cos(PI*i/2.5)-xy.x)+PI/5.0
            xy.x -= t*cos(a)
            xy.y -= t*sin(a)
            val v = xy*width/8.0 + width/2.0
            if(v.inFrame()) {
                val p = colorArray[v]
                if(p!=colors[0] && p!=colors[i]) i=6
                colorArray[v] = colors[i]
            }
            repeat(2) {
                colorArray[createVector(randInt(1024), randInt(1024))] = color(0)
            }
        }
    }
}