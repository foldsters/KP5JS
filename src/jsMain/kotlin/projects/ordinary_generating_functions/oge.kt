@file:OptIn(ExperimentalJsExport::class)

package projects.ordinary_generating_functions

import p5.Sketch
import p5.core.LiteShaderSketch
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.ksl.buildShader
import p5.ksl.float
import p5.ksl.vec2
import kotlin.math.PI

@JsExport
fun Oge() = Sketch {

    Setup {
        noCanvas()
        val shader = getWebGLCore(0).sketch.p5.buildShader(debug = true) {
            Fragment {

                data class Complex(val re: float, val im: float) {
                    constructor(v: vec2): this(v.x, v.y)
                    constructor(re: Double, im: float): this(float(re), float(im))
                    constructor(re: float, im: Double): this(float(re), float(im))
                    constructor(re: Double, im: Double): this(float(re), float(im))
                    operator fun plus(other: Complex) = Complex(re + other.re, im + other.im)
                    operator fun plus(other: float) = Complex(re+other, im)
                    operator fun minus(other: Complex) = Complex(re - other.re, im - other.im)
                    operator fun minus(other: float) = Complex(re-other, im)
                    operator fun times(other: Complex) = Complex(re*other.re - im*other.im, re*other.im + im*other.re)
                    operator fun times(other: float) = Complex(re*other, im*other)
                    operator fun div(other: Complex) = Complex(re*other.re + im*other.im, im*other.re - re*other.im)/(other.re*other.re)
                    operator fun div(other: float) = Complex(re/other, im/other)
                    fun toVec2() = vec2(re, im)
                }

                Main {
                    val uv by 2.5*(it/vec2(512)-0.5)
                    var o by vec2(0.0, 0.0)
                    var s by vec2(1.0, 1.0)

                    repeat(200) {
                        s = (Complex(s)*Complex(uv)).toVec2()
                        o += s*tan(float(it))
                    }
                    val phase by floor(8.0*fract(atan(o.y, o.x)/(2.0*PI)))/8.0
                    vec4(phase, phase, phase, 1.0)
                }
            }
        }

        val shaderSketch = LiteShaderSketch(512, 512, shader, 0)
        getWebGLCore(0).attach(shaderSketch.p5)

        Layout { add(shaderSketch) }
    }

}