package projects.circlizer

import p5.Sketch
import p5.core.*
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.core.P5.Vector
import p5.ksl.*

private interface BorderedElement {
    val bodyElement: P5.CanvasElement
    val borderElements: List<P5.CanvasElement>
    val borderWidth: Double
}

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

        val imageLeft = width*random()-500
        val imageTop  = (height-500)*random()+500
        val imageSize = 250*(random()+1)
        val imageRight = imageLeft + imageSize
        val imageBottom = imageTop + imageSize

        data class CircleElement(var center: Vector, var radius: Double, override var borderWidth: Double): BorderedElement  {
            override val bodyElement = CanvasElement {
                dist(it, center) < (radius - borderWidth/2)
            }
            override val borderElements = listOf(
                CanvasElement {
                    dist(it, center) in (radius-borderWidth/2)..(radius+borderWidth/2)
                }
            )
        }

        data class BoxElement(var topLeft: Vector, var bottomRight: Vector, override var borderWidth: Double): BorderedElement {
            override val bodyElement = CanvasElement {
                (it.x in (topLeft.x + borderWidth/2)..(bottomRight.x - borderWidth/2)) &&
                        (it.y in (topLeft.y + borderWidth/2)..(bottomRight.y - borderWidth/2))
            }
            override val borderElements = listOf(
                CanvasElement {// Left Border
                    (it.y in topLeft.y..bottomRight.y) && it.x in (topLeft.x-borderWidth/2)..(topLeft.x+borderWidth/2)
                },
                CanvasElement {// Top Border
                    (it.x in topLeft.x..bottomRight.x) && it.y in (topLeft.y-borderWidth/2)..(topLeft.y+borderWidth/2)
                },
                CanvasElement {// Right Border
                    (it.y in topLeft.y..bottomRight.y) && it.x in (bottomRight.x-borderWidth/2)..(bottomRight.x+borderWidth/2)
                },
                CanvasElement {// Top Border
                    (it.x in topLeft.x..bottomRight.x) && it.y in (bottomRight.y-borderWidth/2)..(bottomRight.y+borderWidth/2)
                },
            )
        }

        var cacheMouse = createVector(0, 0)
        var cachePoint = createVector(0, 0)
        var selectedElement: P5.CanvasElement? = null
        var overElement: P5.CanvasElement? = null

        val myBox = BoxElement(createVector(imageLeft, imageTop), createVector(imageRight, imageBottom), 20.0)

        val myCircles = List(5) {
            CircleElement(createVector(circles[it][0], circles[it][1]), circles[it][2].toDouble(), 20.0)
        }

//        myBox.bodyElement.mousePressed
//
//        for(element in myBox.borderElements) {
//            element.mousePressed {
//                if(selectedElement != null) return@mousePressed
//                selectedElement = this
//                cacheMouse = mouse.copy()
//                cachePoint = myBox.topLeft.copy()
//            }
//        }
//
//        for(circle in myCircles) {
//            circle.bodyElement.mousePressed {
//                if(selectedElement != null) return@mousePressed
//                selectedElement = this
//                cacheMouse = mouse.copy()
//                cachePoint = circle.center.copy()
//            }
//            circle.borderElements[0].mousePressed {
//                if(selectedElement != null) return@mousePressed
//                selectedElement = this
//                cacheMouse = mouse.copy()
//                cachePoint = circle.center.copy()
//            }
//        }

        MouseReleased {
            selectedElement = null
        }

        val canvasElements = buildList {
            add(listOf(myBox.bodyElement) + myBox.borderElements)
            for(circle in myCircles) {
                add(listOf(circle.bodyElement, circle.borderElements[0]))
            }
        }


        var circleIndex = 0

        val liteCanvas = ShaderSketch(width, height, 0, true,
            minFilterMode = MinFilterMode.NEAREST_MIPMAP_NEAREST,
            magFilterMode = MagFilterMode.NEAREST
        ) {

            it.p5.image(sourceImage, myBox.topLeft, myBox.bottomRight - myBox.topLeft)

            val fr by url { 10 }

            getWebGLCore(0).sketch.p5.frameRate(fr)

            Fragment {
                val prevFrame by UniformP5 { it.p5 }
                val resolution: vec2 by Uniform<vec2> { arrayOf(width, height) }
                val circle by Uniform<vec3> {
                    val circle = myCircles[circleIndex%circles.size]
                    circleIndex++
                    arrayOf(circle.center.x, circle.center.y, circle.radius)
                }

                val pixelate by UniformBool { true }

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

                val alphaMix by buildFunction { color0: vec4, color1: vec4, weightFactor: float ->
                    val newAlpha by mix(color0.a, color1.a, weightFactor)
                    val weight0 by color0.a*(1.0-weightFactor) // avoid div by zero
                    val weight1 by color1.a*weightFactor
                    var resultColor by vec4(0)
                    IF((weight0 + weight1) GT 0.0) {
                        val newRGB by (color0.rgb*weight0 + color1.rgb*weight1)/(weight0 + weight1)
                        resultColor = vec4(newRGB, newAlpha)
                    }
                    resultColor
                }
//
//                val alphaBlend by buildFunction { color0: vec4, color1: vec4, blendFactor: float ->
//                    val newAlpha by color1.a + color0.a*(1.0-color1.a)
//                    val newRGB by (color0.rgb*color0.a*(1.0+color1.a*(blendFactor-1.0)) + color1.rgb*color1.a*(1.0-blendFactor*color0.a))/newAlpha
//                    vec4(newRGB, newAlpha)
//                }

                val alphaTexture by buildFunction { sampler: KSL.Sampler2D, texelCoord: vec2 ->
                    val offset by fract(texelCoord)
                    val intCoord by texelCoord.toIVec()
                    val sampleColorTL by texelFetch(sampler, ivec2(intCoord.x, intCoord.y), int(0))
                    val sampleColorTR by texelFetch(sampler, ivec2(intCoord.x+1, intCoord.y), int(0))
                    val sampleColorBR by texelFetch(sampler, ivec2(intCoord.x+1, intCoord.y+1), int(0))
                    val sampleColorBL by texelFetch(sampler, ivec2(intCoord.x, intCoord.y+1), int(0))

                    val topColor by alphaMix(sampleColorTL, sampleColorTR, offset.x)
                    val bottomColor by alphaMix(sampleColorBL, sampleColorBR, offset.x)
                    val resultColor by alphaMix(topColor, bottomColor, offset.y)
                    resultColor
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
                        otherColor = alphaTexture(prevFrame, uvInv)
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

        fun restart() {
            if(pmouse != createVector(0, 0)) {
                getWebGLCore(0).clear()
                liteCanvas.p5.clear()
                liteCanvas.p5.image(sourceImage, myBox.topLeft, myBox.bottomRight - myBox.topLeft)
                getWebGLCore(0).attach(liteCanvas.p5)
            }
        }

        MouseDragged {
            when(selectedElement) {
                myBox.bodyElement -> {
                    val boxDimensions = myBox.bottomRight - myBox.topLeft
                    myBox.topLeft = cachePoint + mouse - cacheMouse
                    myBox.bottomRight = myBox.topLeft + boxDimensions
                }
                myBox.borderElements[0] -> myBox.topLeft.x = cachePoint.x + mouse.x - cacheMouse.x
                myBox.borderElements[1] -> myBox.topLeft.y = cachePoint.y + mouse.y - cacheMouse.y
                myBox.borderElements[2] -> myBox.bottomRight.x = cachePoint.x + mouse.x - cacheMouse.x
                myBox.borderElements[3] -> myBox.bottomRight.y = cachePoint.y + mouse.y - cacheMouse.y
            }
            for(circleElement in myCircles) {
                if(selectedElement == circleElement.bodyElement) {
                    circleElement.center = cachePoint + mouse - cacheMouse
                } else if(selectedElement == circleElement.borderElements[0]) {
                    console.log("Dragging Radius")
                    circleElement.radius = dist(circleElement.center, mouse)
                }
            }
            restart()
        }


        Draw {
            clear()
            image(liteCanvas.p5, 0, 0, width, height)

            if(!mouseIsPressed) {
                if(overElement?.isMouseOver == true) {
                    overElement = null
                }

                if(overElement == null) {
                    for (element in canvasElements.flatten()) {
                        if (element.isMouseOver) {
                            overElement = element
                            break
                        }
                    }
                }

                if(overElement != null) {
                    selectedElement = overElement
                    cacheMouse = mouse.copy()
                }

                var newCachePoint = when(selectedElement) {
                    myBox.bodyElement -> myBox.topLeft
                    myBox.borderElements[0] -> myBox.topLeft
                    else -> null
                }?.copy()

                if(newCachePoint == null) {
                    for(circleElement in myCircles) {
                        if(selectedElement == circleElement.bodyElement) {
                            newCachePoint = circleElement.center
                        }
                    }
                }

                cachePoint = newCachePoint ?: cachePoint
            }




            for((i, circleElement) in myCircles.withIndex()) {
                val strokeColor = when(overElement) {
                    circleElement.bodyElement -> colors[i]
                    circleElement.borderElements[0] -> color(255)
                    else -> colors[i].asVector { map { it/2 } }
                }
                noFill()
                strokeWeight(circleElement.borderWidth/2)
                stroke(strokeColor)
                circle(circleElement.center, circleElement.radius*2)
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


