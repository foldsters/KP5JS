package projects.moire

import p5.Sketch
import p5.core.LiteShaderSketch
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.ksl.*
import kotlin.math.PI

fun moire() = Sketch {

    Setup {
        noCanvas()

        val sizeSlider = createSlider(0, 2, 1, 0.0001).apply { setScrollable() }
        val zoomSlider = createSlider(0, 512, 128, 1).apply { setScrollable() }

        val shader = getWebGLCore(0).sketch.p5.buildShader(debug = true) {
            Fragment {

                val size by Uniform { sizeSlider.value() }
                val zoom by Uniform { zoomSlider.value() }

                val inSquare by buildFunction { AB: vec2, AD: vec2, AP: vec2 ->
                    (0.0 `<=` dot(AP, AB)) and (dot(AP, AB) `<=` dot(AB, AB)) and
                    (0.0 `<=` dot(AP, AD)) and (dot(AP, AD) `<=` dot(AD, AD))
                }

                val rotate by buildFunction { v: vec2, a: float ->
                    val c by cos(a)
                    val s by sin(a)
                    vec2(v.x*c - v.y*s, v.x*s + v.y*c)
                }

                Main {
                    val uv by floor(zoom*gl_FragCoord.xy/512.0-0.5)
                    var color by vec3(0.0)
                    val A by rotate(uv,  float(PI/3.0))
                    val B by rotate(uv + vec2(0.0, 1.0), float(PI/3.0))
                    val D by rotate(uv + vec2(1.0, 0.0), float(PI/3.0))
                    var b by bool(false)
                    for(x in 0..4) {
                        for(y in 0..4) {
                            val offset = vec2(x-1.5, y-1.5)
                            if((createVector(x-1.5, y-1.5).mag()) < 1.7) {
                                b = b or inSquare(size*(B-A), size*(D-A), offset-fract(A))
                            }
                        }
                    }
                    If(b) { color = vec3(1.0) }
                    vec4(color, 1.0)
                }
            }
        }

        val shaderSketch = LiteShaderSketch(512, 512, shader, 0)
        getWebGLCore(0).attach(shaderSketch.p5)

        Layout {
            Column {
                add(shaderSketch)
                addAll(arrayOf(sizeSlider, zoomSlider)) {
                    style("width", "100%")
                    style("height", "100")
                    style("zoom", "2")
                }
            }
        }
    }

}