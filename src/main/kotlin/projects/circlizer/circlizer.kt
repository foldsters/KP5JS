package projects.circlizer

import p5.Sketch
import p5.core.*
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.ksl.vec2
import p5.ksl.vec3
import p5.core.P5.Vector
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
        val ImageSides = object {
            var left = width*random()-500
            var top = height*random()-500
            var right = left + 300*(random()+1)
            var bottom = top + 300*(random()+1)
            var location
                get() = createVector(left, top)
                set(value) {
                    val cacheSize = size
                    left = value.x
                    top = value.y
                    right = left + cacheSize.x
                    bottom = top + cacheSize.y
                }
            val size get() = createVector(right-left, bottom-top)
        }

        var i = 0

        val liteCanvas = ShaderSketch(width, height, 0, true,
            minFilterMode = MinFilterMode.NEAREST_MIPMAP_NEAREST,
            magFilterMode = MagFilterMode.NEAREST
        ) {

            it.p5.image(sourceImage, ImageSides.location, ImageSides.size)

            val fr by url { 10 }

            getWebGLCore(0).sketch.p5.frameRate(fr)

            Fragment {
                val prevFrame by UniformP5 { it.p5 }
                val resolution: vec2 by Uniform<vec2> { arrayOf(width, height) }
                val circle by Uniform<vec3> {
                    circles[i%circles.size].also { i++ }
                }

                val pixelate by UniformBool { false }

                val between by buildFunction { v: vec2, dl: vec2, ur: vec2 ->
                    (dl.x LT v.x) AND (dl.y LT v.y) AND (v.x LT ur.x) AND (v.y LT ur.y)
                }

                val flr by buildFunction { v: vec2 ->
                    vec2(floor(v.x), floor(v.y))
                }

                val circleInvert by buildFunction { v: vec2 ->
                    val d by (v - circle.xy)
                    circle.xy + d*circle.z*circle.z/dot(d, d)
                }

                Main {
                    val uv by it
                    uv.y = (resolution - uv).y
                    val uvInv by circleInvert(uv)
                    val thisColor by texelFetch(prevFrame, ivec2(int(uv.x), int(uv.y)), int(0))
                    var otherColor by vec4(0)
                    var passPixelCheck by bool(false)

                    IF(pixelate) {
                        val offset by uv - flr(uv)
                        otherColor = texelFetch(prevFrame, uvInv.toIVec(), int(0))
                        val uv1 by flr(circleInvert(vec2(floor(uvInv.x), floor(uvInv.y)))) + offset
                        val uv2 by flr(circleInvert(vec2(floor(uvInv.x), ceil(uvInv.y)))) + offset
                        val uv3 by flr(circleInvert(vec2(ceil(uvInv.x), ceil(uvInv.y)))) + offset
                        val uv4 by flr(circleInvert(vec2(ceil(uvInv.x), floor(uvInv.y)))) + offset
                        passPixelCheck = (uv1 EQ uv) OR (uv2 EQ uv) OR (uv3 EQ uv) OR (uv4 EQ uv)
                    } ELSE {
                        otherColor = texture(prevFrame, uvInv/resolution)
                    }

                    val l by float(1.0)

                    IF(!pixelate OR passPixelCheck) {
                        IF( between(uvInv, vec2(0,0), resolution)) {
                            val newAlpha = otherColor.a + thisColor.a*(1.0-otherColor.a)
                            thisColor.rgb = (thisColor.rgb*thisColor.a*(1.0+otherColor.a*(l-1.0)) + otherColor.rgb*otherColor.a*(1.0-l*thisColor.a))/newAlpha
                            thisColor.a = newAlpha*.9999 + 0.0001
                        }
                    }

                    thisColor
                }
            }
        }

        getWebGLCore(0).attach(liteCanvas.p5)
        getWebGLCore(0).sketch.p5.getCanvas().attribute("id", "?")
        console.log(getWebGLCore(0).sketch.p5.getCanvas())

        var cacheMouse = createVector(0, 0)
        var cachePoint = createVector(0, 0)
        var lastIndex = -1
        var resizing = false
        var moving = false

        fun restart() {
            if(pmouse != createVector(0, 0)) {
                getWebGLCore(0).clear()
                liteCanvas.p5.clear()
                liteCanvas.p5.image(sourceImage, ImageSides.location, ImageSides.size)
                getWebGLCore(0).attach(liteCanvas.p5)
            }
        }

        data class CircleElement(var center: Vector, var radius: Double, var border: Double)  {
            val bodyElement = CanvasElement {
                dist(it, center) < (radius - border/2)
            }
            val borderElement = CanvasElement {
                dist(it, center) in (radius-border/2)..(radius+border/2)
            }
            val onBorder get() = borderElement.isMouseOver
            val onBody get() = bodyElement.isMouseOver
        }

        val myCircle = CircleElement(createVector(circles[0][0], circles[0][1]), circles[0][2].toDouble(), 20.toDouble())

        myCircle.borderElement.mouseOver {
            println("in circle!")
        }
        myCircle.borderElement.mouseOut {
            println("out circle!")
        }
        myCircle.borderElement.mouseClicked {
            println("clicked!")
        }
        myCircle.borderElement.mouseReleased {
            println("released")
        }
        myCircle.borderElement.mousePressed {
            println("pressed")
        }

        Draw {
            var hit = true
            clear()
            image(liteCanvas.p5, 0, 0, width, height)
            val w = 20

            ImageSides.apply {
                if(resizing && lastIndex < 0) {
                    if (mouseIsPressed) {
                        when(lastIndex) {
                            -4 -> left   = mouse.x - cacheMouse.x + cachePoint.x
                            -3 -> top    = mouse.y - cacheMouse.y + cachePoint.y
                            -2 -> right  = mouse.x - cacheMouse.x + cachePoint.x
                            -1 -> bottom = mouse.y - cacheMouse.y + cachePoint.y
                        }
                        restart()
                    } else {
                        resizing = false
                    }
                } else if(moving && lastIndex < 0) {
                    if (mouseIsPressed) {
                        val newXY = (mouse - cacheMouse + cachePoint)
                        if(newXY != location) {
                            location = newXY
                            restart()
                        }
                    } else {
                        moving = false
                    }
                    // left
                } else if(dist(mouse.x, left) < w && mouse.y in top..bottom) {
                    lastIndex = -4
                    if(mouseIsPressed) {
                        cachePoint.xy = location
                        cacheMouse.xy = mouse
                        resizing = true
                    }
                    // up
                } else if(dist(mouse.y, top) < w && mouse.x in left..right) {
                    lastIndex = -3
                    if(mouseIsPressed) {
                        cachePoint.xy = location
                        cacheMouse.xy = mouse
                        resizing = true
                    }
                    // right
                } else if(dist(mouse.x, right) < w && mouse.y in top..bottom) {
                    lastIndex = -2
                    if(mouseIsPressed) {
                        cachePoint.xy = location + size
                        cacheMouse.xy = mouse
                        resizing = true
                    }
                    // down
                } else if(dist(mouse.y, bottom) < w && mouse.x in left..right) {
                    lastIndex = -1
                    if(mouseIsPressed) {
                        cachePoint.xy = location + size
                        cacheMouse.xy = mouse
                        resizing = true
                    }
                    // moving
                } else if(mouse.x in left..right && mouse.y in top..bottom) {
                    console.log("moving!", mouse.x, mouse.y)
                    console.log(ImageSides.left, ImageSides.top, ImageSides.right, ImageSides.bottom)
                    lastIndex = -1
                    if (mouseIsPressed) {
                        cachePoint.xy = location
                        cacheMouse.xy = mouse
                        moving = true
                    }
                } else {
                    hit = false
                }
            }
            if(!hit) {
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
                            val newXY = (mouse - cacheMouse + cachePoint)
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
                            cachePoint = cxy
                            cacheMouse = mouse
                            moving = true
                        }
                    } else {
                        stroke(col.asVector { this*0.5 + 128 })
                    }
                    circle(cir[0], cir[1], 2*cir[2])
                }
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


