package projects.circlizer

import p5.Sketch
import p5.core.*
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.ksl.KSL
import p5.ksl.vec2
import p5.ksl.vec3
import kotlin.math.abs

fun circlizer() = Sketch {

    lateinit var sourceImage: Image

    Preload {
        sourceImage = loadImage("stock/flower2.jpg")
    }

    Setup {

        getBody().style("background-color", "#46484a")
        val canvas = createCanvas(1920, 1080)
        scalarMode(ScalarMode.XYZ)
        strokeWeight(5)
        noFill()

        val circles: Array<Array<Number>> = Array(5) {
            arrayOf(width*random(), height*random(), 500)
        }

        noFill()
        val colors = arrayOf(color(255, 0, 0), color(255, 255, 0), color(0, 255, 0), color(0, 255, 255), color(0, 0, 255))
        val imageLocation = createVector(width*random()-500, height*random()-500)

        var i = 0

        val liteCanvas = ShaderSketch(width, height, 0, true,
            minFilterMode = MinFilterMode.NEAREST_MIPMAP_NEAREST,
            magFilterMode = MagFilterMode.NEAREST
        ) {

            it.p5.image(sourceImage, imageLocation, 500, 500)

            val fr by url { 10 }

            getWebGLCore(0).sketch.p5.frameRate(fr)

            Fragment {
                val prevFrame by UniformP5 { it.p5 }
                val iResolution: vec2 by Uniform<vec2> { arrayOf(width, height) }
                val c by Uniform<vec3> {
                    circles[i%circles.size].also { i++ }
                }

                val between by buildFunction { v: vec2, dl: vec2, ur: vec2 ->
                    (dl.x LT v.x) AND (dl.y LT v.y) AND (v.x LT ur.x) AND (v.y LT ur.y)
                }

                val flr by buildFunction { v: vec2 ->
                    vec2(floor(v.x), floor(v.y))
                }

                val inv by buildFunction { v: vec2 ->
                    val d by (v - c.xy)
                    c.xy + d*c.z*c.z/dot(d, d)
                }

                Main {
                    val uv by it
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
                        IF( between(uvInv, vec2(0,0), iResolution) AND (colorInv.a EQ 1.0)) {
                            color = colorInv
                        }
                    }

                    color
                }
            }
        }

        getWebGLCore(0).attach(liteCanvas.p5)
        getWebGLCore(0).sketch.p5.getCanvas().attribute("id", "?")
        console.log(getWebGLCore(0).sketch.p5.getCanvas())

        var startMouseLocation = createVector(0, 0)
        var startCircleLocation = createVector(0, 0)
        var lastIndex = 0
        var resizing = false
        var moving = false

        fun restart() {
            if(pmouse != createVector(0, 0)) {
                getWebGLCore(0).clear()
                liteCanvas.p5.clear()
                liteCanvas.p5.image(sourceImage, imageLocation, 500, 500)
                getWebGLCore(0).attach(liteCanvas.p5)
            }
        }

        Draw {
            var hit = false
            clear()
            image(liteCanvas.p5, 0, 0, width, height)
            circles.zip(colors).forEachIndexed { i, (cir, col) ->
                val cxy = createVector(cir[0], cir[1])
                val r = cir[2]
                val w = 20

                if(resizing && i==lastIndex) {
                    hit = true
                    stroke(255)
                    if (mouseIsPressed) {
                        val newRad = dist(mouse, cxy)
                        if(newRad != cir[2]) {
                            cir[2] = dist(mouse, cxy)
                            restart()
                        }
                    } else {
                        resizing = false
                    }
                } else if(moving && i==lastIndex) {
                    hit = true
                    stroke(col)

                    if (mouseIsPressed) {
                        val newXY = (mouse - startMouseLocation + startCircleLocation)
                        if(newXY.x != cir[0] || newXY.y != cir[1]) {
                            cir[0] = newXY.x
                            cir[1] = newXY.y
                            restart()
                        }
                    } else { moving = false }
                } else if(abs(dist(cxy, mouse)-r) < w && !hit && (!mouseIsPressed || i==lastIndex)) {
                    hit = true
                    lastIndex = i
                    stroke(255)
                    if(mouseIsPressed) { resizing = true }
                } else if(dist(cxy, mouse) < r-w && !hit && (!mouseIsPressed || i==lastIndex)) {
                    hit = true
                    lastIndex = i
                    stroke(col)
                    if(mouseIsPressed) {
                        startCircleLocation = cxy
                        startMouseLocation = mouse
                        moving = true
                    }
                } else {
                    stroke(col.asVector { this*0.5 + 128 })
                }
                circle(cir[0], cir[1], 2*cir[2])

            }
        }
    }


}
