package projects.circlizer

import p5.Sketch
import p5.core.Image
import p5.core.MagFilterMode
import p5.core.MinFilterMode
import p5.core.ShaderSketch
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.ksl.vec2
import p5.ksl.vec3

fun circlizer() = Sketch {

    lateinit var sourceImage: Image

    Preload {
        sourceImage = loadImage("stock/flower.png")
    }

    Setup {

        pixelDensity(1)
        val canvas = createCanvas(1920, 1080)

        val circles: Array<Array<Number>> = Array(10) {
            arrayOf(width*random(), height*random(), height*random())
        }
        var i = 0

        image(sourceImage, width*random(), height*random(), 100, 100)

        val liteCanvas = ShaderSketch(width, height, 0, true,
            updateMipmap = true,
            minFilterMode = MinFilterMode.NEAREST_MIPMAP_NEAREST,
            magFilterMode = MagFilterMode.NEAREST
        ) {

            it.p5.background(0, 0)
            it.p5.clear()
            it.p5.erase()
            it.p5.rect(0, 0, width, height)

            getWebGLCore(0).sketch.p5.background(0, 0)
            getWebGLCore(0).sketch.p5.clear()
            getWebGLCore(0).sketch.p5.erase()
            getWebGLCore(0).sketch.p5.rect(0, 0, width, height)

            Fragment {
                val prevFrame by Uniform { if (i==0) this@Setup else it.p5 }
                val iResolution by Uniform<vec2> { arrayOf(width, height) }
                val c by Uniform<vec3> {
                    println(i)
                    i++
                    circles[i%circles.size]
                }

                val between by buildFunction { v: vec2, dl: vec2, ur: vec2 ->
                    (dl.x LT v.x) AND (dl.y LT v.y) AND (v.x LT ur.x) AND (v.y LT ur.y)
                }

                val flr by buildFunction { v: vec2 ->
                    vec2(floor(v.x), floor(v.y))
                }

                val inv by buildFunction { v: vec2 ->
                    val d by (v - c.xy)
                    c.xy + (d*c.r*c.r)/dot(d, d)
                }

                Main {
                    var uv by it
                    uv.y = (iResolution - uv).y
                    val offset = uv - flr(uv)
                    var color by texture(prevFrame, uv/iResolution)

                    val uvInv by inv(uv)
                    val colorInv by texture(prevFrame, flr(uvInv)/iResolution)

                    val uv1 by flr(inv(vec2(floor(uvInv.x), floor(uvInv.y)))) + offset
                    val uv2 by flr(inv(vec2(floor(uvInv.x), ceil(uvInv.y)))) + offset
                    val uv3 by flr(inv(vec2(ceil(uvInv.x), ceil(uvInv.y)))) + offset
                    val uv4 by flr(inv(vec2(ceil(uvInv.x), floor(uvInv.y)))) + offset

                    IF((uv1 EQ uv) OR (uv2 EQ uv) OR (uv3 EQ uv) OR (uv4 EQ uv)) {
                        IF( between(uvInv, vec2(0,0), iResolution) AND (colorInv.r GT 0.1)) {
                            color = colorInv
                        }
                    }

                    color
                }
            }
        }

        getWebGLCore(0).attach(liteCanvas.p5)

    }


}
