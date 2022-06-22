package scripts

import p5.Sketch
import p5.kshader.KShader
import p5.kshader.KShader.*

fun shader() = Sketch {
    KShader().apply {
        val q = float(12.0) + radians(float(13.0))
        val s = q + q
        console.log(s)
    }
}