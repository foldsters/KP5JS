package scripts

import p5.Sketch
import p5.kglsl.*

fun shader() = Sketch {

    val fragmentCode = KGLSL().fragment {

        val iTime by Uniform<float>()
        val iResolution by Uniform<vec2>()
        val fragCoord by In<vec2>()
        +"Before Main"
        Main {
            +"After Main"
            val uv by fragCoord/iResolution.xy
            val col by float(0.5) + (float(0.5)*cos(iTime + uv.xyx + vec3(0, 2, 4)))
            vec4(col, 1.0)
        }
    }
    println(fragmentCode)

}