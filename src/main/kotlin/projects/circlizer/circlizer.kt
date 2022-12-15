package projects.circlizer

import p5.Sketch
import p5.core.*
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.core.P5.Vector
import p5.ksl.*
import p5.util.rotateLeft
import p5.util.rotateRight
import p5.util.Map

private interface BorderedElement {
    val bodyElement: P5.CanvasElement
    val borderElements: List<P5.CanvasElement>
    val borderWidth: Double
}

private fun BorderedElement.elements() = listOf(bodyElement) + borderElements

fun circlizer() = Sketch {

    lateinit var sourceImage: Image

    Preload {
        sourceImage = loadImage("../../../stock/flower2.jpg")
        loadFont("./fonts/futuralight.otf")
    }

    Setup {

        noCanvas()

        getBody().style("background-color", "#46484a")
        val canvas = createCanvas(1920, 1080)
        scalarMode(ScalarMode.XYZ)
        strokeWeight(5)
        noFill()
        noLoop()

        val sourceImageCanvas = createGraphics(width/4, width/4).apply {
            image(sourceImage, 0, 0, width, width)
        }

        var backgroundColor = color(15, 255)

        val postProcessCanvas = createGraphics(width, height)

        val circles: Array<Array<Number>> = Array(5) {
            arrayOf(width*random(), height*random(), 500)
        }

        val colors = arrayOf(color(255, 0, 0), color(255, 255, 0), color(0, 255, 0), color(0, 255, 255), color(0, 0, 255))

        val imageLeft = width*random()-500
        val imageTop  = (height-500)*random()+500
        val imageSize = 250*(random()+1)
        val imageRight = imageLeft + imageSize
        val imageBottom = imageTop + imageSize

        class CircleElement(var center: Vector, var radius: Double, override var borderWidth: Double): BorderedElement  {
            override val bodyElement = CanvasElement {
                dist(it, center) < (radius - borderWidth/2)
            }
            override val borderElements = listOf(
                CanvasElement {
                    dist(it, center) in (radius-borderWidth/2)..(radius+borderWidth/2)
                }
            )
        }

        class BoxElement(var topLeft: Vector, var bottomRight: Vector, override var borderWidth: Double): BorderedElement {
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

        val circleColors = Map(5) {
            myCircles[it] to colors[it]
        }

        var borderedElements = listOf(myBox) + myCircles

        var circleIndex = 0

        val shaderSketch = ShaderSketch(width, height, 0, true,
            minFilterMode = MinFilterMode.NEAREST_MIPMAP_NEAREST,
            magFilterMode = MagFilterMode.NEAREST
        ) {

            it.p5.getCanvas().hide()

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
                    val weight0 by color0.a*(1.0-weightFactor)
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

        var blurImage: Image = sourceImage
        var blurSize = 5
        var blurStrength = 2.0

        val blurShader = ShaderPass(0) {
            Fragment {
                val img by UniformImage { blurImage }
                val resolution: vec2 by Uniform<vec2> { arrayOf(width, height) }
                val gamma by UniformFloat {
                    console.log(blurStrength)
                    blurStrength
                }

                val alphaMix by buildFunction { color0: vec4, color1: vec4, weightFactor: float ->
                    val newAlpha by mix(color0.a, color1.a, weightFactor)
                    val weight0 by color0.a*(1.0-weightFactor)
                    val weight1 by color1.a*weightFactor
                    var resultColor by vec4(0)
                    IF((weight0 + weight1) GT 0.0) {
                        val newRGB by (color0.rgb*weight0 + color1.rgb*weight1)/(weight0 + weight1)
                        resultColor = vec4(newRGB, newAlpha)
                    }
                    resultColor
                }

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

                val flr by buildFunction { v: vec2 ->
                    vec2(floor(v.x), floor(v.y))
                }

                Main {
                    var uv by it
                    uv.y = (resolution - uv).y
                    uv = flr(uv)
                    val c by alphaTexture(img, uv)
                    val l by alphaTexture(img, uv + vec2(-1, 0))
                    val u by alphaTexture(img, uv + vec2(0, 1))
                    val r by alphaTexture(img, uv + vec2(-1, 0))
                    val d by alphaTexture(img, uv + vec2(0, -1))
                    val ul by alphaTexture(img, uv + vec2(1, 1))
                    val ur by alphaTexture(img, uv + vec2(-1, 1))
                    val dr by alphaTexture(img, uv + vec2(-1, -1))
                    val dl by alphaTexture(img, uv + vec2(1, -1))

                    val cornerColor by alphaMix(alphaMix(ul, ur, float(0.5)), alphaMix(dl, dr, float(0.5)), float(0.5))
                    val sideColor by alphaMix(alphaMix(u, d, float(0.5)), alphaMix(l, r, float(0.5)), float(0.5))
                    val cornerCenterColor by alphaMix(cornerColor, c, float(0.5))
                    val resultColor by alphaMix(sideColor, cornerCenterColor, float(0.5))
                    resultColor.a = pow(resultColor.a, 1.0/gamma)
                    resultColor
                }
            }
        }

        var startStopButton: Button? = null

        fun restart(bypassMotionCheck: Boolean = false) {
            loop()
            if(pmouse != createVector(0, 0) || bypassMotionCheck) {
                sourceImageCanvas.image(sourceImage, 0, 0, width/4, width/4)
                getWebGLCore(0).clear()
                shaderSketch.p5.clear()
                shaderSketch.p5.image(sourceImage, myBox.topLeft, myBox.bottomRight - myBox.topLeft)
            }
        }

        fun updateSelectedElement() {
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
                } else if (selectedElement == circleElement.borderElements[0]) {
                    circleElement.radius = dist(circleElement.center, mouse)
                }
            }
            if(selectedElement != null) restart()
        }

        fun updateOverElement() {

            selectedElement = null

            if(overElement?.isMouseOver == false) {
                overElement = null
            }

            if(overElement == null) {
                for (element in borderedElements.map { it.elements() }.flatten()) {
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
                myBox.borderElements[1] -> myBox.topLeft
                myBox.borderElements[2] -> myBox.bottomRight
                myBox.borderElements[3] -> myBox.bottomRight
                else -> null
            }?.copy()

            for(circleElement in myCircles) {
                if(selectedElement == circleElement.bodyElement) {
                    newCachePoint = circleElement.center.copy()
                }
            }

            cachePoint = newCachePoint ?: cachePoint
        }

        fun updateElements() {
            if(mouseIsPressed) {
                updateSelectedElement()
            } else {
                updateOverElement()
            }
        }

        fun drawCircles() {
            for(element in borderedElements.filterIsInstance<CircleElement>()) {
                val circleColor = circleColors[element]!!
                val strokeColor = when(overElement) {
                    element.bodyElement -> circleColor
                    element.borderElements[0] -> circleColor.asVector { map { 127 + it/2 } }
                    else -> circleColor.asVector { map { it/2 } }
                }
                noFill()
                strokeWeight(element.borderWidth/2)
                stroke(strokeColor)
                circle(element.center, element.radius*2)
            }
        }

        KeyPressed {
            if(key == "d") borderedElements = borderedElements.rotateRight(1)
            if(key == "a") borderedElements = borderedElements.rotateLeft(1)
            overElement = null
        }

        Draw {
            startStopButton?.html("Stop")
            clear()
            shaderSketch.p5.redraw()
            image(shaderSketch.p5, 0, 0, width, height)
            updateElements()
            drawCircles()
            postProcessCanvas.background(backgroundColor)
            postProcessCanvas.image(shaderSketch.p5, 0, 0, width, height)
        }

        sourceImageCanvas.getCanvas().drop { file ->
            loadImage(file.data) {
                sourceImage = it
                restart()
            }
        }

        fun stop() {
            noLoop()
            startStopButton?.html("Restart")
            getWebGLCore(0).sketch.p5.noLoop()
        }

        fun postProcess() {
            stop()
            val largeBlur = shaderSketch.p5.get()
            //val smallBlur = shaderSketch.p5.get()
            blurImage = largeBlur
            repeat(blurSize) {
                blurImage = blurShader.redraw(width, height)
            }
            postProcessCanvas.background(backgroundColor)
            postProcessCanvas.image(blurImage, 0, 0, width, height)
            //postProcessCanvas.image(smallBlur, 0, 0, width, height)
            postProcessCanvas.image(shaderSketch.p5, 0, 0, width, height)
        }

        Layout {

            fun P5.Grid.cardStyle() {
                GridStyle(inherit = false) {
                    style("background-color", "#2b2b2b")
                    style("padding", "32px ".repeat(4))
                    style("border-radius", "32px")
                    style("margin", "32px ".repeat(4))
                }
            }

            fun P5.StyleBuilder.titleStyle() {
                style("font-size", "64px")
                style("color", "white")
                style("font-family", "futuralight")
            }

            Column {
                Column {
                    cardStyle()
                    add(createSpan("Adjustments")) {
                        titleStyle()
                    }
                    add(canvas)
                    Row {
                        GridStyle(inherit=false) {
                            style("margin-top", "32px")
                        }
                        GridStyle(inherit=true) {
                            style("grid-gap", "32px")
                        }
                        Column {
                            add(sourceImageCanvas)
                            val changeImageButton = createButton("Change Image").apply {
                                style("background-color", "#2b2b2b")
                                style("font-size", "64px")
                                style("color", "white")
                                style("border-radius", "32px")
                                style("font-family", "futuralight")
                                style("border-color", "white")
                                mouseClicked {
                                    chooseFile {
                                        loadImage(it.data) {
                                            sourceImage = it
                                            restart()
                                        }
                                    }
                                }
                            }
                            add(changeImageButton) {
                                style("width", "${width/4.0}px")
                                style("height", "${width/12.0}px")
                            }
                        }

                        Column {

                            val colorPreview = createDiv("")
                            add(colorPreview) {
                                style("width", "${width/4}px")
                                style("height", "${width/4}px")
                            }
                            colorPreview.apply {
                                style("background-color", backgroundColor.toString())
                                style("border", "2px solid white")
                            }
                            val changeColorButton = createButton("Change Color").apply {
                                style("background-color", "#2b2b2b")
                                style("font-size", "64px")
                                style("color", "white")
                                style("border-radius", "32px")
                                style("font-family", "futuralight")
                                style("border-color", "white")
                                mousePressed {
                                    pickColor {
                                        backgroundColor = it
                                        colorPreview.style("background-color", it.toString())
                                    }
                                }
                            }
                            add(changeColorButton) {
                                style("width", "${width/4.0}px")
                                style("height", "${width/12.0}px")
                            }
                        }

                        Column {


                            startStopButton = createButton("Start").apply {
                                style("background-color", "#2b2b2b")
                                style("font-size", "64px")
                                style("color", "white")
                                style("border-radius", "32px")
                                style("font-family", "futuralight")
                                style("border-color", "white")
                                mousePressed {
                                    if(isLooping() || isRedrawing) {
                                        stop()
                                    } else {
                                        restart(true)
                                    }
                                }
                            }
                            add(startStopButton!!) {
                                style("width", "${width/4.0}px")
                                style("height", "${width/12.0}px")
                            }

                            val postProcessButton = createButton("Post Process").apply {
                                style("background-color", "#2b2b2b")
                                style("font-size", "64px")
                                style("color", "white")
                                style("border-radius", "32px")
                                style("font-family", "futuralight")
                                style("border-color", "white")
                                mousePressed {
                                    postProcess()
                                }
                            }
                            add(postProcessButton) {
                                style("width", "${width/4.0}px")
                                style("height", "${width/12.0}px")
                            }

                            Row {
                                GridStyle(false) {
                                    style("background-color", "#2b2b2b")
                                    style("font-size", "48px")
                                    style("color", "white")
                                    style("font-family", "futuralight")
                                    style("width", "${width/4.0}px")
                                }
                                add(createSpan("Blur Strength:")) {
                                    style("justify-self", "start")
                                }
                                add(createSpan(blurStrength.toString()).apply {
                                    attribute("contenteditable", "true")
                                    input {
                                        blurStrength = html().toDoubleOrNull() ?: blurStrength
                                    }
                                }) {
                                    style("justify-self", "end")
                                }
                            }

                            Row {
                                GridStyle(false) {
                                    style("background-color", "#2b2b2b")
                                    style("font-size", "48px")
                                    style("color", "white")
                                    style("font-family", "futuralight")
                                    style("width", "${width/4.0}px")
                                }
                                add(createSpan("Blur Size:")) {
                                    style("justify-self", "start")
                                }
                                add(createSpan(blurSize.toString()).apply {
                                    attribute("contenteditable", "true")
                                    input {
                                        blurSize = html().toIntOrNull() ?: blurSize
                                    }
                                }) {
                                    style("justify-self", "end")
                                }
                            }

                            Row {
                                GridStyle(false) {
                                    style("background-color", "#2b2b2b")
                                    style("font-size", "48px")
                                    style("color", "white")
                                    style("font-family", "futuralight")
                                    style("width", "${width/4.0}px")
                                }
                                add(createSpan("Frame Rate:")) {
                                    style("justify-self", "start")
                                }
                                add(createSpan(frameRate().toString()).apply {
                                    attribute("contenteditable", "true")
                                }) {
                                    style("justify-self", "end")
                                }
                            }
                        }
                    }

                }
                Column {
                    cardStyle()
                    add(createSpan("Output")) {
                        titleStyle()
                    }
                    add(postProcessCanvas)
                }
            }
        }
    }
}


