package scripts

import p5.Sketch
import p5.kshader.KShader
import p5.kshader.KShader.*

fun shader() = Sketch {
    KShader().apply {
        val q by float(12.1) + radians(float(13.0))
        var s by q + q
        s = s + q
        s
        console.log(lines.)
    }
}