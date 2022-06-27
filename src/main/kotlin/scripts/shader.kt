package scripts

import p5.Sketch
import p5.kshader.KShader

fun shader() = Sketch {
    val shader = KShader()
    val fragment = shader.fragment {
        var q by float(12.1) + radians(float(13.0))
        println(1)
        var s by q + q
        println(2)
        s = s + q
        println(3)
        q = q + s
        println(4)
        s
        println(5)
    }
    console.log(fragment)
    shader.logInstructions()

}