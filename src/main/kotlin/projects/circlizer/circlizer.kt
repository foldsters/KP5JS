package projects.circlizer

import p5.Sketch
import p5.core.Image
import p5.core.LiteShaderSketch
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.ksl.buildShader
import p5.ksl.vec2
import p5.ksl.vec3

fun circlizer() = Sketch {

    lateinit var sourceImage: Image

    Preload {
        sourceImage = loadImage("stock/flower.png")
    }

    Setup {

        val glCore = getWebGLCore(0)
        val glSketch = glCore.sketch.p5
        val canvas = createCanvas(512, 512)

        pixelDensity(1)
        frameRate(1)

        val circles: Array<Array<Number>> = Array(5) {
            arrayOf(width*random(), height*random(), 50 + 100*random())
        }
        var i = 0

        image(sourceImage, 10, 10, 200, 200)

        val shader = glSketch.buildShader(debug = true) {
            Fragment {

                val lastFrame by Uniform { canvas }
                val iResolution by Uniform<vec2> { arrayOf(width, height) }
                val c by Uniform<vec3> { circles[i] }

                val between by buildFunction { v: vec2, dl: vec2, ur: vec2 ->
                    (dl.x LT v.x) AND (dl.y LT v.y) AND (v.x LT ur.x) AND (v.y LT ur.y)
                }

                Main {
                    val uv by it
                    val d by (uv - c.xy)
                    var color by texture(lastFrame, uv/iResolution)
                    IF(color.r `==` 0.0) {
                        val inv by c.xy + (d*(c.r*c.r))/dot(d, d)
                        IF( between(inv, vec2(0,0), iResolution) ) {
                            color = texture(lastFrame, inv)
                        }
                    }
                    color
                }
            }
        }

        val ss = LiteShaderSketch(width, height, shader, 0)
        ss.p5.image(sourceImage, 10, 10, 200, 200)

        glCore.attach(ss.p5)
        glSketch.frameRate(1)

        Draw {
            i = (i+1)%circles.size
            console.log(i)
        }

    }


}
