package projects.circlizer

import p5.Sketch
import p5.core.*
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.ksl.vec2
import p5.ksl.vec3
import kotlin.math.abs

fun circlizer() = Sketch {

    lateinit var sourceImage: Image

    Preload {
        sourceImage = loadImage("../../../stock/flower2.jpg")
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
        var imageLocation = createVector(width*random()-500, height*random()-500)
        var imageSize = createVector(300*(random()+1), 300*(random()+1))

        var i = 0

        val liteCanvas = ShaderSketch(width, height, 0, true,
            minFilterMode = MinFilterMode.NEAREST_MIPMAP_NEAREST,
            magFilterMode = MagFilterMode.NEAREST
        ) {

            it.p5.image(sourceImage, imageLocation, imageSize)

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
                    //val color by texture(prevFrame, uv/iResolution)
                    val color by texelFetch(prevFrame, ivec2(int(uv.x), int(uv.y)), int(0))
                    val uvInv by inv(uv)
                    val colorInv by texelFetch(prevFrame, uvInv.toIVec(), int(0))

                    val uv1 by flr(inv(vec2(floor(uvInv.x), floor(uvInv.y)))) + offset
                    val uv2 by flr(inv(vec2(floor(uvInv.x), ceil(uvInv.y)))) + offset
                    val uv3 by flr(inv(vec2(ceil(uvInv.x), ceil(uvInv.y)))) + offset
                    val uv4 by flr(inv(vec2(ceil(uvInv.x), floor(uvInv.y)))) + offset

                    IF((uv1 EQ uv) OR (uv2 EQ uv) OR (uv3 EQ uv) OR (uv4 EQ uv)) {
                        IF( between(uvInv, vec2(0,0), iResolution)) {
                            val newAlpha = colorInv.a + color.a*(1.0-colorInv.a)
                            color.rgb = (colorInv.rgb*colorInv.a + color.rgb*color.a*(1.0-colorInv.a))/newAlpha
                            color.a = newAlpha
//                            val newAlpha = color.a + colorInv.a*(1.0-color.a)
//                            color.rgb = (colorInv.rgb*colorInv.a + color.rgb*color.a*(1.0-colorInv.a))/newAlpha
//                            color.a = newAlpha
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
        var lastIndex = -1
        var resizing = false
        var moving = false

        fun restart() {
            if(pmouse != createVector(0, 0)) {
                getWebGLCore(0).clear()
                liteCanvas.p5.clear()
                liteCanvas.p5.image(sourceImage, imageLocation, imageSize)
                getWebGLCore(0).attach(liteCanvas.p5)
            }
        }

        Draw {
            var hit = false
            clear()
            image(liteCanvas.p5, 0, 0, width, height)
            val w = 20

            // Image Resizing
            if(resizing && lastIndex<0) {
                hit = true
                stroke(255)
                if (mouseIsPressed) {
                    when(lastIndex) {
                        -4 -> { // left
                            val right = imageLocation.x + imageSize.x
                            imageLocation.x = mouse.x - startMouseLocation.x + startCircleLocation.x
                            imageSize.x = right - imageLocation.x
                        }
                        -3 -> { // top
                            val bottom = imageLocation.y + imageSize.y
                            imageLocation.y = mouse.y - startMouseLocation.y + startCircleLocation.y
                            imageSize.y = bottom - imageLocation.y
                        }
                        -2 -> imageSize.x = mouse.x - startMouseLocation.x + startCircleLocation.x
                        -1 -> imageSize.y = mouse.y - startMouseLocation.y + startCircleLocation.y
                    }
                    restart()
                } else {
                    resizing = false
                }
            } else if(moving && lastIndex<0) {
                hit = true
                if (mouseIsPressed) {
                    val newXY = (mouse - startMouseLocation + startCircleLocation)
                    if(newXY != imageLocation) {
                        imageLocation.xy = newXY
                        restart()
                    }
                } else {
                    moving = false
                }
            // left
            } else if(dist(mouse.x, imageLocation.x) < w && mouse.y > imageLocation.y && mouse.y < (imageLocation.y + imageSize.y) && !hit) {
                hit = true
                lastIndex = -4
                if(mouseIsPressed) {
                    startCircleLocation.xy = imageLocation
                    startMouseLocation.xy = mouse
                    resizing = true
                }
            // up
            } else if(dist(mouse.y, imageLocation.y) < w && mouse.x > imageLocation.x && mouse.x < (imageLocation.x + imageSize.x) && !hit) {
                hit = true
                lastIndex = -3
                if(mouseIsPressed) {
                    startCircleLocation.xy = imageLocation
                    startMouseLocation.xy = mouse
                    resizing = true
                }
            // right
            } else if(dist(mouse.x, imageLocation.x + imageSize.x) < w && mouse.y > imageLocation.y && mouse.y < (imageLocation.y + imageSize.y) && !hit) {
                hit = true
                lastIndex = -2
                if(mouseIsPressed) {
                    startCircleLocation.xy = imageSize
                    startMouseLocation.xy = mouse
                    resizing = true
                }
            // down
            } else if(dist(mouse.y, imageLocation.y + imageSize.y) < w && mouse.x > imageLocation.x && mouse.x < (imageLocation.x + imageSize.x) && !hit) {
                hit = true
                lastIndex = -1
                if(mouseIsPressed) {
                    startCircleLocation.xy = imageSize
                    startMouseLocation.xy = mouse
                    resizing = true
                }
            // moving
            } else if(mouse.x > imageLocation.x && mouse.y > imageLocation.y && mouse.x < (imageLocation.x + imageSize.x) && mouse.y < (imageLocation.y + imageSize.y) && !hit) {
                console.log("moving!")
                hit = true
                lastIndex = -1
                if(mouseIsPressed) {
                    startCircleLocation.xy = imageLocation
                    startMouseLocation.xy = mouse
                    moving = true
                }
            }

            // Circle Resizing
            circles.zip(colors).forEachIndexed { i, (cir, col) ->
                val cxy = createVector(cir[0], cir[1])
                val r = cir[2]

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

        canvas.drop { file ->
            loadImage(file.data) {
                sourceImage = it
                restart()
            }
        }
    }


}

