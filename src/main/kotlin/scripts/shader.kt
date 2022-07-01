package scripts

import p5.Sketch
import p5.kglsl.KGLSL

fun shader() = Sketch {

    val fragmentCode = KGLSL().fragment {
        Main {
            val color by vec2(float(0.8), float(0.1))
            color.xyyx
        }
    }
    println(fragmentCode)

}