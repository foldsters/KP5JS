@file:Suppress("FunctionName", "unused", "PropertyName")

package p5.native

import kotlin.js.Json


@JsModule("p5")
@JsNonModule
external class NativeP5 {
    constructor(sketch: (NativeP5) -> Unit)
    constructor()

    // ENVIRONMENT //

    // Accessibility
    fun describe(text : String)
    fun describeElement(name: String, text: String)
    fun textOutput()
    fun gridOutput()
    fun describe(text : String, display: String)
    fun describeElement(name: String, text: String, display: String)
    fun textOutput(display: String)
    fun gridOutput(display: String)

    // Window
    var frameCount: Int
    val deltaTime: Double
    val focused: Boolean
    fun cursor(type: String)
    fun cursor(type: String, x: Int, y: Int)
    fun frameRate(fps: Number)
    fun frameRate() : Double
    fun noCursor()
    val displayWidth: Int
    val displayHeight: Int
    val windowWidth: Int
    val windowHeight: Int

    val _targetFrameRate: Double
    var windowResized : ()->Unit

    val height : Int
    val width : Int
    fun fullscreen(value: Boolean)
    fun fullscreen(): Boolean
    fun pixelDensity(value: Int)
    fun pixelDensity(): Int
    fun displayDensity(): Int
    fun getURL(): String
    fun getURLPath(): Array<String>
    fun getURLParams(): dynamic


    // COLOR

    // Creating & Rendering
    fun color(gray: Number): NativeColor
    fun color(gray: Number, alpha : Number): NativeColor
    fun color(v1: Number, v2: Number, v3: Number): NativeColor
    fun color(v1: Number, v2: Number, v3: Number, alpha: Number): NativeColor
    fun color(colorString: String): NativeColor
    fun color(colorArray: Array<Number>): NativeColor

    fun alpha(color: NativeColor): Double
    fun blue(color: NativeColor): Double
    fun brightness(color: NativeColor): Double
    fun green(color: NativeColor): Double
    fun hue(color: NativeColor): Double
    fun lerpColor(c1: NativeColor, c2: NativeColor, amt: Double): NativeColor
    fun lightness(color: NativeColor): Double
    fun red(color: NativeColor): Double
    fun saturation(color: NativeColor): Double

    // Setting
    fun background(gray: Number)
    fun background(gray: Number, alpha: Number)
    fun background(colorString: String)
    fun background(colorString: String, alpha: Number)
    fun background(v1: Number, v2: Number, v3: Number)
    fun background(v1: Number, v2: Number, v3: Number, alpha: Number)
    fun background(colorArray: Array<Number>)
    fun background(color: NativeColor)
    fun clear()
    fun colorMode(mode: String)
    fun colorMode(mode: String, max: Number)
    fun colorMode(mode: String, max1: Number, maxA: Number)
    fun colorMode(mode: String, max1: Number, max2: Number, max3: Number)
    fun colorMode(mode: String, max1: Number, max2: Number, max3: Number, maxA: Number)
    fun fill(gray: Number)
    fun fill(gray: Number, alpha: Number)
    fun fill(v1: Number, v2: Number, v3: Number)
    fun fill(v1: Number, v2: Number, v3: Number, alpha: Number)
    fun fill(colorString : String)
    fun fill(colorArray: Array<Number>)
    fun fill(color: NativeColor)
    fun noFill()
    fun noStroke()
    fun stroke(gray: Number)
    fun stroke(gray: Number, alpha: Number)
    fun stroke(v1: Number, v2: Number, v3: Number)
    fun stroke(v1: Number, v2: Number, v3: Number, alpha: Number)
    fun stroke(colorString : String)
    fun stroke(colorArray: Array<Number>)
    fun stroke(color: NativeColor)
    fun erase()
    fun erase(strengthFill: Number)
    fun erase(strengthFill: Number, strengthStroke: Number)
    fun noErase()

    val _colorMaxes: dynamic

    // SHAPE

    // 2D Primitives
    fun arc(x: Number, y: Number, width: Number, height: Number, startRad: Number, stopRad: Number)
    fun arc(x: Number, y: Number, width: Number, height: Number, startRad: Number, stopRad: Number, mode: String)
    fun arc(x: Number, y: Number, width: Number, height: Number, startRad: Number, stopRad: Number, mode: String, detail: Int)

    fun ellipse(x: Number, y: Number, width: Number)
    fun ellipse(x: Number, y: Number, width: Number, height: Number)
    fun ellipse(x: Number, y: Number, width: Number, height: Number, detail: Int)

    fun circle(x: Number, y:Number, d: Number)

    fun line(x1: Number, y1: Number, x2: Number, y2: Number)
    fun line(x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number)

    fun point(x: Number, y: Number)
    fun point(x: Number, y: Number, z: Number)
    fun point(coordinate_vector: NativeVector)

    fun quad(
        x1: Number, y1: Number, x2: Number, y2: Number,
        x3: Number, y3: Number, x4: Number, y4: Number
    )
    fun quad(
        x1: Number, y1: Number, x2: Number, y2: Number,
        x3: Number, y3: Number, x4: Number, y4: Number, detailX: Int, detailY: Int
    )
    fun quad(
        x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number,
        x3: Number, y3: Number, z3: Number, x4: Number, y4: Number, z4: Number
    )
    fun quad(
        x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number,
        x3: Number, y3: Number, z3: Number, x4: Number, y4: Number, z4: Number, detailX: Int, detailY: Int
    )

    fun rect(x: Number, y: Number, width: Number)
    fun rect(x: Number, y: Number, width: Number, height: Number)
    fun rect(x: Number, y: Number, width: Number, height: Number, tl: Number, tr: Number, br: Number, bl:Number)
    fun rect(x: Number, y: Number, width: Number, detailX: Int, detailY: Int)

    fun square(x: Number, y: Number, size: Number)
    fun square(x: Number, y: Number, size: Number, tl: Number, tr: Number, br: Number, bl:Number)
    fun square(x: Number, y: Number, size: Number, detailX: Int, detailY: Int)

    fun triangle(x1: Number, y1: Number, x2: Number, y2: Number, x3: Number, y3: Number)

    // Attributes
    fun ellipseMode(mode: String)
    fun noSmooth()
    fun rectMode(mode: String)
    fun smooth()
    fun strokeCap(mode: String)
    fun strokeJoin(mode: String)
    fun strokeWeight(weight: Number)

    // Curves
    fun bezier(
        x1: Number, y1: Number, x2: Number, y2: Number,
        x3: Number, y3: Number, x4: Number, y4: Number
    )
    fun bezier(
        x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number,
        x3: Number, y3: Number, z3: Number, x4: Number, y4: Number, z4: Number
    )
    fun bezierDetail(detail: Number)
    fun bezierPoint(a: Number, b: Number, c: Number, d: Number, t: Number): Double
    fun bezierTangent(a: Number, b: Number, c: Number, d: Number, t: Number): Double
    fun curve(
        x1: Number, y1: Number, x2: Number, y2: Number,
        x3: Number, y3: Number, x4: Number, y4: Number
    )
    fun curve(
        x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number,
        x3: Number, y3: Number, z3: Number, x4: Number, y4: Number, z4: Number
    )
    fun curveDetail(detail: Number)
    fun curveTightness(amount: Number)
    fun curvePoint(a: Number, b: Number, c: Number, d: Number, t: Number): Double
    fun curveTangent(a: Number, b: Number, c: Number, d: Number, t: Number): Double

    // Vertex
    fun beginContour()
    fun beginShape()
    fun beginShapeString(kind: String)
    fun beginShapeNumber(kind: Number)
    fun bezierVertex(x2: Number, y2: Number, x3: Number, y3: Number, x4: Number, y4: Number)
    fun bezierVertex(
        x2: Number, y2: Number, z2: Number,
        x3: Number, y3: Number, z3: Number,
        x4: Number, y4: Number, z4: Number
    )
    fun curveVertex(x: Number, y: Number)
    fun curveVertex(x: Number, y: Number, z: Number)
    fun endContour()
    fun endShape()
    fun endShape(mode: String)
    fun quadraticVertex(cx: Number, cy: Number, x3: Number, y3: Number)
    fun quadraticVertex(cx: Number, cy: Number, cz: Number, x3: Number, y3: Number, z3: Number)
    fun vertex(x: Number, y: Number)
    fun vertex(x: Number, y: Number, z: Number)
    fun vertex(x: Number, y: Number, u: Number, v: Number)
    fun vertex(x: Number, y: Number, z: Number, u: Number, v: Number)
    fun normal(vector: NativeVector)
    fun normal(x: Number, y: Number, z: Number)

    // TODO: 3D Primitives
    // TODO: 3D Models


    // STRUCTURE
    var preload : ()->Unit
    var draw : ()->Unit
    var setup : ()->Unit
    var disableFriendlyErrors : Boolean
    fun remove()
    fun noLoop()
    fun loop()
    fun isLooping() : Boolean
    fun push()
    fun pop()
    fun redraw()
    fun redraw(n: Int)

    fun select(selectors: String): NativeElement?
    fun select(selectors: String, containerString: String): NativeElement?
    fun select(selectors: String, containerElement: NativeElement): NativeElement?
    fun selectAll(selectors: String): Array<NativeElement>
    fun selectAll(selectors: String, containerString: String): Array<NativeElement>
    fun selectAll(selectors: String, containerElement: NativeElement): Array<NativeElement>
    fun removeElements()
    fun createDiv(htmlString: String): NativeElement
    fun createP(htmlString: String): NativeElement
    fun createSpan(htmlString: String): NativeElement
    fun createImg(srcPath: String, altText: String): NativeElement
    fun createImg(srcPath: String, altText: String, crossOrigin: String): NativeElement
    fun createImg(srcPath: String, altText: String, crossOrigin: String, loadedCallback: (NativeElement)->Unit): NativeElement
    fun createA(href: String, html: String): NativeElement
    fun createA(href: String, html: String, target: String): NativeElement
    fun createSlider(min: Number, max: Number): NativeElement
    fun createSlider(min: Number, max: Number, value: Number): NativeElement
    fun createSlider(min: Number, max: Number, value: Number, step: Number): NativeElement
    fun createButton(label: String): NativeElement
    fun createCheckbox(): NativeCheckbox
    fun createCheckbox(label: String): NativeCheckbox
    fun createCheckbox(label: String, checked: Boolean): NativeCheckbox
    fun createSelect(): NativeSelect
    fun createSelect(multiple: Boolean): NativeSelect
    fun createRadio(): NativeRadio
    fun createRadio(name: String): NativeRadio
    fun createColorPicker(): NativeColorPicker
    fun createColorPicker(default: String): NativeColorPicker
    fun createColorPicker(default: NativeColor): NativeColorPicker
    fun createInput(): NativeElement
    fun createInput(default: String): NativeElement
    fun createInput(default: String, type: String): NativeElement
    fun createFileInput(callback: (NativeFile)->Unit): NativeElement
    fun createFileInput(callback: (NativeFile)->Unit, multiple: Boolean): NativeElement
    fun createVideo(src: String): NativeMediaElement
    fun createVideo(src: String, callback: ()->Unit): NativeMediaElement
    fun createVideo(srcs: Array<String>): NativeMediaElement
    fun createVideo(srcs: Array<String>, callback: ()->Unit): NativeMediaElement
    fun createCapture(type: String): NativeElement
    fun createCapture(type: String, callback: (dynamic)->Unit): NativeElement
    fun createElement(tag: String): NativeElement
    fun createElement(tag: String, content: String): NativeElement

    @JsName("Renderer2D")
    class NativeRenderer2D: NativeElement
    @JsName("RendererGL")
    class NativeRendererGL: NativeElement

    fun createCanvas(w: Number, h: Number): NativeRenderer2D
    fun createCanvas(w: Number, h: Number, rendererMode: String): dynamic
    fun resizeCanvas(w: Number, h: Number)
    fun resizeCanvas(w: Number, h: Number, noRedraw: Boolean)
    fun noCanvas()
    fun createGraphics(w: Number, h: Number): NativeP5
    fun createGraphics(w: Number, h: Number, rendererMode: String): NativeP5
    fun blendMode(mode: String)
    val drawingContext: dynamic
    fun setAttributes(key: String, value: Boolean)
    @JsName("canvas")
    var canvasHtml: dynamic

    // TRANSFORM
    fun applyMatrix(a: Number, b: Number, c: Number, d: Number, e: Number, f: Number)
    fun applyMatrix(a: Number, b: Number, c: Number, d: Number,
                    e: Number, f: Number, g: Number, h: Number,
                    i: Number, j: Number, k: Number, l: Number,
                    m: Number, n: Number, o: Number, p: Number)
    fun rotateMatrix()
    fun rotate(angle: Number)
    fun rotate(angle: Number, axis: NativeVector)
    fun rotateX(angle: Number)
    fun rotateY(angle: Number)
    fun rotateZ(angle: Number)
    fun scale(s: Number)
    fun scale(x: Number, y: Number)
    fun scale(x: Number, y: Number, z: Number)
    fun scale(scales: NativeVector)
    fun shearX(angle: Number)
    fun shearY(angle: Number)
    fun translate(x: Number, y: Number)
    fun translate(x: Number, y: Number, z: Number)
    fun translate(vector: NativeVector)


    // DATA
    fun storeItem(key: String, value: String)
    fun storeItem(key: String, value: Number)
    fun storeItem(key: String, value: Boolean)
    fun storeItem(key: String, value: NativeColor)
    fun storeItem(key: String, value: NativeVector)
    fun <T> getItem(key: String): T?
    fun clearStructure()
    fun removeItem(key: String)

    fun hex(n: Number): String
    fun hex(n: Number, digits: Number): String
    fun hex(ns: Array<Number>): Array<String>
    fun hex(ns: Array<Number>, digits: Number): Array<String>


    // EVENTS

    // Acceleration
    val deviceOrientation: String?
    val accelerationX: Double
    val accelerationY: Double
    val accelerationZ: Double
    val pAccelerationX: Double
    val pAccelerationY: Double
    val pAccelerationZ: Double
    val rotationX: Double
    val rotationY: Double
    val rotationZ: Double
    val pRotationX: Double
    val pRotationY: Double
    val pRotationZ: Double
    val turnAxis: String
    fun setMoveThreshold(value: Number)
    fun setShakeThreshold(value: Number)
    var deviceMoved: ()->Unit
    var deviceTurned: ()->Unit
    var deviceShaken: ()->Unit

    // Keyboard
    val isKeyPressed: Boolean
    val key: String
    val keyCode: Double
    var keyPressed: (NativeKeyboardEvent)->Unit
    var keyReleased: (NativeKeyboardEvent)->Unit
    var keyTyped: (NativeKeyboardEvent)->Unit
    fun keyIsDown(code: Int): Boolean

    // Mouse
    val movedX: Double
    val movedY: Double
    val mouseX: Double
    val mouseY: Double
    val pmouseX: Double
    val pmouseY: Double
    val winMouseX: Double
    val winMouseY: Double
    val pwinMouseX: Double
    val pwinMouseY: Double
    val mouseButton: String
    val mouseIsPressed: Boolean
    var mouseMoved: ()->Unit
    var mouseDragged: ()-> Unit
    var mousePressed: ()->Unit
    var mouseReleased: ()-> Unit
    var mouseClicked: ()->Unit
    var doubleClicked: ()-> Unit
    var mouseWheel: (NativeWheelEvent)-> Unit
    fun requestPointerLock()
    fun exitPointerLock()

    // Touch
    val touches: Array<dynamic> // TODO: Remove Dynamic
    var touchStarted: ()->Unit
    var touchMoved: ()->Unit
    var touchEnded: ()->Unit

    fun createImage(width: Int, height: Int): NativeImage
    fun saveCanvas(fileName: String)
    fun saveCanvas(filename: String, extension: String)
    fun saveCanvas(selectedCanvas: NativeElement, filename: String)
    fun saveCanvas(selectedCanvas: NativeElement, filename: String, extension: String)
    fun saveFrames(filename: String, extension: String, duration: Number, framerate: Number)
    fun saveFrames(filename: String, extension: String, duration: Number, framerate: Number, callback: (dynamic)->Unit)

    // Loading & Displaying
    fun loadImage(path: String): NativeImage
    fun loadImage(path: String, successCallback: (NativeImage)->Unit): NativeImage
    fun loadImage(path: String, successCallback: (NativeImage)->Unit, failureCallback: (dynamic)->Unit): NativeImage
    fun image(img: NativeImage, x: Number, y: Number)
    fun image(img: NativeImage, x: Number, y: Number, width: Number, height: Number)
    fun image(img: NativeP5, x: Number, y: Number, width: Number, height: Number)
    fun image(img: NativeImage, dx: Number, dy: Number, dWidth: Number, dHeight: Number, sx: Number, sy: Number)
    fun image(img: NativeImage, dx: Number, dy: Number, dWidth: Number, dHeight: Number,
              sx: Number, sy: Number, sWidth: Number, sHeight: Number)
    fun tint(gray: Number)
    fun tint(gray: Number, alpha : Number)
    fun tint(v1: Number, v2: Number, v3: Number)
    fun tint(v1: Number, v2: Number, v3: Number, alpha: Number)
    fun tint(colorString: String)
    fun tint(colorArray: Array<Number>)
    fun tint(color: NativeColor)
    fun noTint()
    fun imageMode(modeString: String)

    //Pixels
    val pixels: Array<Int>
    fun loadPixels()
    fun updatePixels()
    fun blend(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: String)
    fun blend(srcImage: NativeImage, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: String)
    fun copy(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int)
    fun copy(srcImage: NativeImage, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int)
    fun filter(filterType: String) // TODO: Make Enum
    fun filter(filterType: String, filterParam: Number) // TODO: Make Enum
    fun get(): NativeImage
    fun get(x: Number, y: Number): Array<Double>
    fun get(x: Number, y: Number, w: Number, h: Number): NativeImage
    fun set(x: Number, y: Number, a: Number)
    fun set(x: Number, y: Number, a: Array<Number>)
    fun set(x: Number, y: Number, a: NativeColor)
    fun set(x: Number, y: Number, a: NativeImage)

    // IO
    fun loadStrings(filename: String): Array<String>
    fun loadStrings(filename: String, callback:(Array<String>)->Unit): Array<String>
    fun loadStrings(filename: String, callback:(Array<String>)->Unit, errorCallback:(dynamic)->Unit): Array<String>

    fun loadJSON(path: String): Json
    fun loadJSON(path: String, callback: (Json)->Unit): Json
    fun loadJSON(path: String, callback: (Json)->Unit, errorCallback: ()->Unit): Json
    fun loadJSON(path: String, datatype: String): Json // TODO: make enum
    fun loadJSON(path: String, datatype: String, callback: (Json)->Unit): Json // TODO: make enum
    fun loadJSON(path: String, datatype: String, callback: (Json)->Unit, errorCallback: ()->Unit): Json // TODO: make enum

    fun httpDo(
        path: String,
        method: String = definedExternally,
        datatype: String = definedExternally,
        data: dynamic = definedExternally,
        callback: (dynamic) -> Unit = definedExternally,
        errorCallback: () -> Unit = definedExternally
    ): dynamic // TODO: Remove Dynamic, make enum
    fun httpDo(
        path: String,
        options: dynamic,
        callback: (dynamic) -> Unit = definedExternally,
        errorCallback: () -> Unit = definedExternally
    )

    fun createWriter(name: String): NativePrintWriter

    fun save(filename: String)
    fun save(obj: NativeElement, filename: String)
    fun save(obj: String, filename: String)
    fun save(obj: Array<String>, filename: String)
    fun save(obj: NativeImage, filename: String)
    fun save(filename: String, options: dynamic)
    fun save(obj: NativeElement, filename: String, options: dynamic)
    fun save(obj: String, filename: String, options: dynamic)
    fun save(obj: Array<String>, filename: String, options: dynamic)
    fun save(obj: NativeImage, filename: String, options: dynamic)

    fun saveTable(table: NativeTable, filename: String)
    fun saveTable(table: NativeTable, filename: String, tableExtension: String)

    fun loadTable(filename: String): NativeTable
    fun loadTable(filename: String, extension: String): NativeTable
    fun loadTable(filename: String, extension: String, header: String): NativeTable
    fun loadTable(filename: String, extension: String, header: String, callback: (NativeTable)->Unit): NativeTable
    fun loadTable(filename: String, extension: String, header: String, callback: (NativeTable)->Unit, errorCallback: (dynamic)->Unit): NativeTable

    // Time & Date

    fun day(): Int
    fun hour(): Int
    fun minute(): Int
    fun millis(): Int
    fun month(): Int
    fun second(): Int
    fun year(): Int

    // MATH

    fun map(value: Number, start1: Number, stop1: Number, start2: Number, stop2: Number): Double
    fun map(value: Number, start1: Number, stop1: Number, start2: Number, stop2: Number, withinBounds: Boolean): Double

    @JsName("Vector")
    class NativeVector(x: Number = definedExternally, y: Number = definedExternally, z: Number = definedExternally) {
        var x: Double
        var y: Double
        var z: Double
        override fun toString(): String
        fun set(x: Number, y: Number, z: Number)
        fun set(value: NativeVector)
        fun copy(): NativeVector
        fun add(value: NativeVector) // TODO: Make Binary Overload
        fun rem(value: NativeVector)
        fun sub(value: NativeVector) // TODO: Make Binary Overload
        fun mult(n: Number) // TODO: Make Binary Overload
        fun mult(v: NativeVector) // TODO: Make Binary Overload
        fun div(n: Number) // TODO: Make Binary Overload
        fun div(v: NativeVector) // TODO: Make Binary Overload
        fun mag(): Double
        fun magSq(): Double
        fun dot(v: NativeVector): Double // TODO: Make Binary Overload
        fun cross(v: NativeVector): NativeVector // TODO: Make Binary Overload
        fun dist(v: NativeVector): Double // TODO: Make Binary Overload
        fun normalize(): NativeVector
        fun limit(n: Number): NativeVector
        fun setMag(len: Number): NativeVector
        fun heading(): Double
        fun setHeading(angle: Number)
        fun rotate(angle: Number)
        fun angleBetween(v: NativeVector): Double
        fun lerp(v: NativeVector): NativeVector
        fun reflect(v: NativeVector): NativeVector
        fun array(): Array<Number>
        fun equals(v: NativeVector): Boolean

        companion object {
            fun fromAngle(angle: Number): NativeVector
            fun fromAngle(angle: Number, length: Number): NativeVector
            fun fromAngles(theta: Number, phi: Number): NativeVector
            fun fromAngles(theta: Number, phi: Number, length: Number): NativeVector
            fun random2D(): NativeVector
            fun random3D(): NativeVector
            fun add(v1: NativeVector, v2: NativeVector): NativeVector
            fun rem(v1: NativeVector, v2: NativeVector): NativeVector
            fun sub(v1: NativeVector, v2: NativeVector): NativeVector
            fun mult(v: NativeVector, n: Number): NativeVector
            fun mult(v1: NativeVector, v2: NativeVector): NativeVector
            fun div(v: NativeVector, n: Number): NativeVector
            fun div(v1: NativeVector, v2: NativeVector): NativeVector
            fun lerp(v1: NativeVector, v2: NativeVector, amt: Number): NativeVector
            fun cross(v1: NativeVector, v2: NativeVector): NativeVector
            fun dot(v1: NativeVector, v2: NativeVector): Double
            fun dist(v1: NativeVector, v2: NativeVector): Double
            fun normalize(v: NativeVector): NativeVector
            fun rotate(v: NativeVector, angle: Number): NativeVector
        }
    }

    fun lerp(start: Number, stop: Number, amt: Number): Double

    fun createVector(): NativeVector
    fun createVector(x: Number, y: Number): NativeVector
    fun createVector(x: Number, y: Number, z: Number): NativeVector

    // Noise
    fun noise(x: Number): Double
    fun noise(x: Number, y: Number): Double
    fun noise(x: Number, y: Number, z: Number): Double
    fun noiseDetail(lod: Number, falloff: Number)
    fun noiseSeed(seed: Int)

    // Random
    fun randomSeed(seed: Int)
    fun random(): Double
    fun random(max: Number): Double
    fun random(min: Number, max: Number): Double
    fun <T> random(choices: Array<T>): T
    fun randomGaussian(): Double
    fun randomGaussian(mean: Number): Double
    fun randomGaussian(mean: Number, sd: Number): Double

    // Trig
    fun angleMode(mode: String)

    // TYPOGRAPHY

    fun textAlign(horizAlign: String) // TODO: Make Enum
    fun textAlign(horizAlign: String, vertAlign: String) // TODO: Make Enum
    fun textLeading(): Double
    fun textLeading(leading: Number)
    fun textSize(): Double
    fun textSize(size: Number)
    fun textStyle(): String // TODO: Make Enum
    fun textStyle(style: String) // TODO: Make Enum
    fun textWidth(text: String): Double
    fun textAscent(): Double
    fun textDecent(): Double
    fun textWrap(): String // TODO: Make Enum
    fun textWrap(wrapStyle: String) // TODO: Make Enum
    fun loadFont(path: String): NativeFont
    fun loadFont(path: String, callback: (NativeFont)->Unit): NativeFont
    fun loadFont(path: String, callback: (NativeFont)->Unit, onError: (dynamic)->Unit): NativeFont
    fun text(str: String, x: Number, y: Number)
    fun text(str: String, x: Number, y: Number, x2: Number, y2: Number)
    fun textFont(): NativeFont?
    fun textFont(font: NativeFont)
    fun textFont(font: NativeFont, size: Number)
    fun textFont(fontName: String)
    fun textFont(fontName: String, size: Number)

    // 3D
    // Material

    fun loadShader(vertFilename: String, fragFilename: String): NativeShader
    fun loadShader(vertFilename: String, fragFilename: String, callback: (NativeShader)->Unit): NativeShader
    fun loadShader(vertFilename: String, fragFilename: String, callback: (NativeShader)->Unit, errorCallback: (dynamic)->Unit): NativeShader
    fun createShader(vertSrc: String, fragSrc: String): NativeShader
    fun shader(s: NativeShader)
    fun resetShader()
    fun texture(tex: NativeImage)
    fun texture(tex: NativeMediaElement)
    fun texture(tex: NativeP5)
    fun texture(tex: NativeTexture)
    fun textureMode(mode: String) // TODO: Make Enum
    fun textureWrap(wrapX: String, wrapY: String) // TODO: Make Enum

}

