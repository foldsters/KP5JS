@file:Suppress("FunctionName", "unused", "PropertyName")

package p5

import kotlin.js.Json
import kotlin.js.RegExp

@JsModule("p5")
@JsNonModule
abstract external class NativeP5(sketch : (P5)->Unit) {

    class RendererGL {
        companion object {
            var _initContext: dynamic
        }
    }

    // ENVIRONMENT
    fun describe(text : String)
    fun describeElement(name: String, text: String)
    fun textOutput()
    fun gridOutput()
    @JsName("describe")
    fun _describe(text : String, display: String)
    @JsName("describeElement")
    fun _describeElement(name: String, text: String, display: String)
    @JsName("textOutput")
    fun _textOutput(display: String)
    @JsName("gridOutput")
    fun _gridOutput(display: String)

    var frameCount: Int
    val deltaTime: Int
    val focused: Boolean
    fun cursor(type: String)
    fun cursor(type: String, x: Int, y: Int)
    fun frameRate(fps: Number)
    fun frameRate() : Number
    fun noCursor()
    val displayWidth: Int
    val displayHeight: Int
    val windowWidth: Int
    val windowHeight: Int

    val _targetFrameRate: Number

    var windowResized : (()->Unit)?

    val height : Int
    val width : Int
    fun fullscreen(value: Boolean)
    fun fullscreen(): Boolean
    fun pixelDensity(value: Int)
    fun pixelDensity(): Int
    fun displayDensity(): Int
    fun getURL(): String
    fun getURLPath(): Array<String>

    // TODO: How to return an arbitrary js object
    // fun getURLParams()


    // COLOR

    // Creating & Rendering

    class Color {
        override fun toString(): String
        fun setRed(red: Number)
        fun setGreen(green: Number)
        fun setBlue(blue: Number)
        fun setAlpha(alpha: Number)
    }

    fun color(gray: Number): Color
    fun color(gray: Number, alpha : Number): Color
    fun color(v1: Number, v2: Number, v3: Number): Color
    fun color(v1: Number, v2: Number, v3: Number, alpha: Number): Color
    fun color(colorString: String): Color
    fun color(colorArray: Array<Number>): Color

    fun alpha(color: Color): Number
    fun blue(color: Color): Number
    fun brightness(color: Color): Number
    fun green(color: Color): Number
    fun hue(color: Color): Number
    fun lerpColor(c1: Color, c2: Color, amt: Double): Color
    fun lightness(color: Color): Number
    fun red(color: Color): Number
    fun saturation(): Number

    // Setting
    fun background(gray: Number)
    fun background(gray: Number, alpha: Number)
    fun background(colorString: String)
    fun background(colorString: String, alpha: Number)
    fun background(v1: Number, v2: Number, v3: Number)
    fun background(v1: Number, v2: Number, v3: Number, alpha: Number)
    fun background(colorArray: Array<Color>)
    fun background(color: Color)
    fun clear()
    @JsName("colorMode")
    fun _colorMode(mode: String)
    @JsName("colorMode")
    fun _colorMode(mode: String, max: Number)
    @JsName("colorMode")
    fun _colorMode(mode: String, max1: Number, maxA: Number)
    @JsName("colorMode")
    fun _colorMode(mode: String, max1: Number, max2: Number, max3: Number)
    @JsName("colorMode")
    fun _colorMode(mode: String, max1: Number, max2: Number, max3: Number, maxA: Number)
    fun fill(gray: Number)
    fun fill(gray: Number, alpha: Number)
    fun fill(v1: Number, v2: Number, v3: Number)
    fun fill(v1: Number, v2: Number, v3: Number, alpha: Number)
    fun fill(colorString : String)
    fun fill(colorArray: Array<Number>)
    fun fill(color: Color)
    fun noFill()
    fun noStroke()
    fun stroke(gray: Number)
    fun stroke(gray: Number, alpha: Number)
    fun stroke(v1: Number, v2: Number, v3: Number)
    fun stroke(v1: Number, v2: Number, v3: Number, alpha: Number)
    fun stroke(colorString : String)
    fun stroke(colorArray: Array<Number>)
    fun stroke(color: Color)
    fun erase()
    fun erase(strengthFill: Number)
    fun erase(strengthFill: Number, strengthStroke: Number)
    fun noErase()


    // SHAPE

    // 2D Primitives

    fun arc(x: Number, y: Number, width: Number, height: Number, startRad: Number, stopRad: Number)
    @JsName("arc")
    fun _arc(x: Number, y: Number, width: Number, height: Number, startRad: Number, stopRad: Number, mode: String)
    @JsName("arc")
    fun _arc(x: Number, y: Number, width: Number, height: Number, startRad: Number, stopRad: Number, mode: String, detail: Int)

    fun ellipse(x: Number, y: Number, width: Number)
    fun ellipse(x: Number, y: Number, width: Number, height: Number)
    fun ellipse(x: Number, y: Number, width: Number, height: Number, detail: Int)

    fun circle(x: Number, y:Number, d: Number)

    fun line(x1: Number, y1: Number, x2: Number, y2: Number)
    fun line(x1: Number, y1: Number, x2: Number, y2: Number, z1: Number, z2: Number)

    fun point(x: Number, y: Number)
    fun point(x: Number, y: Number, z: Number)
    fun point(coordinate_vector: Vector)

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
    @JsName("ellipseMode")
    fun _ellipseMode(mode: String)
    fun noSmooth()
    @JsName("rectMode")
    fun _rectMode(mode: String)
    fun smooth()
    @JsName("strokeCap")
    fun _strokeCap(mode: String)
    @JsName("strokeJoin")
    fun _strokeJoin(mode: String)
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
    fun bezierPoint(a: Number, b: Number, c: Number, d: Number, t: Number): Number
    fun bezierTangent(a: Number, b: Number, c: Number, d: Number, t: Number): Number
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
    fun curvePoint(a: Number, b: Number, c: Number, d: Number, t: Number): Number
    fun curveTangent(a: Number, b: Number, c: Number, d: Number, t: Number): Number

    // Vertex
    @JsName("beginContour")
    fun _beginContour()
    @JsName("beginShape")
    fun _beginShape()
    @JsName("beginShape")
    fun _beginShapeString(kind: String)
    @JsName("beginShape")
    fun _beginShapeNumber(kind: Number)
    @JsName("bezierVertex")
    fun _bezierVertex(x2: Number, y2: Number, x3: Number, y3: Number, x4: Number, y4: Number)
    @JsName("bezierVertex")
    fun _bezierVertex(
        x2: Number, y2: Number, z2: Number,
        x3: Number, y3: Number, z3: Number,
        x4: Number, y4: Number, z4: Number
    )
    @JsName("curveVertex")
    fun _curveVertex(x: Number, y: Number)
    @JsName("curveVertex")
    fun _curveVertex(x: Number, y: Number, z: Number)
    @JsName("endContour")
    fun _endContour()
    @JsName("endShape")
    fun _endShape()
    @JsName("endShape")
    fun _endShapeClose(mode: String)
    @JsName("quadraticVertex")
    fun _quadraticVertex(cx: Number, cy: Number, x3: Number, y3: Number)
    @JsName("quadraticVertex")
    fun _quadraticVertex(cx: Number, cy: Number, cz: Number, x3: Number, y3: Number, z3: Number)
    @JsName("vertex")
    fun _vertex(x: Number, y: Number)
    @JsName("vertex")
    fun _vertex(x: Number, y: Number, z: Number)
    @JsName("vertex")
    fun _vertex(x: Number, y: Number, u: Number, v: Number)
    @JsName("vertex")
    fun _vertex(x: Number, y: Number, z: Number, u: Number, v: Number)
    @JsName("normal")
    fun _normal(vector: Vector)
    @JsName("normal")
    fun _normal(x: Number, y: Number, z: Number)

    // TODO: 3D Primitives
    // TODO: 3D Models


    // STRUCTURE

    var preload : (()->Unit)?
    var draw : (()->Unit)?
    var setup : (()->Unit)?
    fun remove()
    var disableFriendlyErrors : Boolean
    fun noLoop()
    fun loop()
    fun isLooping() : Boolean
    fun push()
    fun pop()
    fun redraw()
    fun redraw(n: Int)


    // DOM
    open class Element(elt: String) {
        fun parent(): Element
        fun parent(parentString: String)
        fun parent(parentElement: Element)
        fun id(): String
        fun id(idString: String)
        @JsName("class")
        fun `class`()
        @JsName("class")
        fun `class`(classString: String)
        fun mousePressed(keepCallback: Boolean)
        fun mousePressed(callback: ()->Unit)
        fun doubleClicked(keepCallback: Boolean)
        fun doubleClicked(callback: ()->Unit)
        fun mouseWheel(keepCallback: Boolean)
        fun mouseWheel(callback: (dynamic)->Unit) // TODO: Callback called with object, Remove Dynamic
        fun mouseReleased(keepCallback: Boolean)
        fun mouseReleased(callback: ()->Unit)
        fun mouseClicked(keepCallback: Boolean)
        fun mouseClicked(callback: ()->Unit)
        fun mouseMoved(keepCallback: Boolean)
        fun mouseMoved(callback: ()->Unit)
        fun mouseOver(keepCallback: Boolean)
        fun mouseOver(callback: ()->Unit)
        fun mouseOut(keepCallback: Boolean)
        fun mouseOut(callback: ()->Unit)
        fun touchStarted(keepCallback: Boolean)
        fun touchStarted(callback: ()->Unit)
        fun touchMoved(keepCallback: Boolean)
        fun touchMoved(callback: ()->Unit)
        fun touchEnded(keepCallback: Boolean)
        fun touchEnded(callback: ()->Unit)
        fun dragOver(keepCallback: Boolean)
        fun dragOver(callback: ()->Unit)
        fun dragLeave(keepCallback: Boolean)
        fun dragLeave(callback: ()->Unit)
        fun changed(keepCallback: Boolean)
        fun changed(callback: ()->Unit)
        fun input(keepCallback: Boolean)
        fun input(callback: ()->Unit)
        fun addClass(classString: String)
        fun removeClass(classString: String)
        fun hasClass(classString: String): Boolean
        fun toggleClass(classString: String)
        fun child()
        fun child(classString: String)
        fun child(classElement: Element)
        fun center()
        fun center(alignString: String)
        fun html(): String
        fun html(htmlString: String)
        fun html(htmlString: String, append: Boolean)
        fun position(): dynamic // TODO: Remove Dynamic
        fun position(x: Number, y: Number)
        fun position(x: Number, y: Number, positionTypeString: String)
        fun style(property: String): String
        fun style(property: String, value: String)
        fun attribute(attr: String): String
        fun attribute(attr: String, value: String)
        fun removeAttribute(attr: String)
        //fun value(valueString: String)
        fun show()
        fun hide()
        fun size(): dynamic // TODO: Returns Object, Remove Dynamic
        fun size(w: Number)
        fun _size(w: String)
        fun size(w: Number, h: Number)
        fun _size(w: Number, h: String)
        fun _size(w: String, h: Number)
        fun remove()
        fun drop(callback: (File)->Unit)
        fun drop(callback: (File)->Unit, onDrop: ()->Unit)
    }

    class Button: Element

    class ParagraphElement: Element

    class InputElement: Element {
        fun value(): String
        fun value(s: String)
    }

    class Slider: Element {
        fun value(): Number
        fun value(v: Number)
    }

    class Checkbox: Element {
        fun checked(): Boolean
    }

    open class Selector: Element {
        fun option(name: String)
        fun option(name: String, value: String)
        fun selected(): Element
        fun selected(value: String)
        fun value(): String
    }

    class Select: Selector {
        fun disable()
        fun disable(value: String)
    }

    class Radio: Selector {
        fun disable(disabled: Boolean)
    }

    class ColorPicker: Element {
        fun color(): Color
        fun value(): String
    }

    class MediaElement: Element {
        val src: String
        fun play()
        fun stop()
        fun pause()
        fun loop()
        fun noLoop()
        fun autoplay(shouldAutoplay: Boolean)
        fun volume(): Number
        fun volume(value: Number)
        fun speed(): Number
        fun speed(multiplier: Number)
        fun time(): Number
        fun time(timeSeconds: Number)
        fun duration(): Number
        fun onended(callback: (MediaElement)->Unit)
        // fun connect() // TODO: Implement when integrating p5.sound
        fun disconnect()
        fun showControls()
        fun hideControls()
        fun addCue(time: Number, callback: (Number)->Unit): Number
        fun <T> addCue(time: Number, callback: (Number, T)->Unit, value: T): Number
        fun removeCue(id: Number)
        fun clearCues()
        fun get(): Image
    }

    class File {
        val file: dynamic
        val type: String
        val subtype: String
        val name: String
        val size: dynamic // TODO: Figure out what type this really is
        val data: String
    }

    fun select(selectors: String): Element?
    fun select(selectors: String, containerString: String): Element?
    fun select(selectors: String, containerElement: Element): Element?
    fun selectAll(selectors: String): Array<Element>
    fun selectAll(selectors: String, containerString: String): Array<Element>
    fun selectAll(selectors: String, containerElement: Element): Array<Element>
    fun removeElements()
    fun createDiv(htmlString: String): Element
    fun createP(htmlString: String): ParagraphElement
    fun createSpan(htmlString: String): Element
    fun createImg(srcPath: String, altText: String): Element
    @JsName("createImage")
    fun _createImg(srcPath: String, altText: String, crossOrigin: String): Element
    @JsName("createImage")
    fun _createImg(srcPath: String, altText: String, crossOrigin: String, loadedCallback: (Element)->Unit): Element
    fun createA(href: String, html: String): Element
    @JsName("createA")
    fun _createA(href: String, html: String, target: String): Element
    fun createSlider(min: Number, max: Number): Slider
    fun createSlider(min: Number, max: Number, value: Number): Slider
    fun createSlider(min: Number, max: Number, value: Number, step: Number): Slider
    fun createButton(label: String): Button
    fun createCheckbox(): Checkbox
    fun createCheckbox(label: String): Checkbox
    fun createCheckbox(label: String, checked: Boolean): Checkbox
    fun createSelect(): Select
    fun createSelect(multiple: Boolean): Select
    fun createRadio(): Radio
    fun createRadio(name: String): Radio
    fun createColorPicker(): ColorPicker
    fun createColorPicker(default: String): ColorPicker
    fun createColorPicker(default: Color): ColorPicker
    fun createInput(): InputElement
    fun createInput(default: String): InputElement
    @JsName("createInput")
    fun _createInput(default: String, type: String): InputElement
    fun createFileInput(callback: (File)->Unit): Element
    fun createFileInput(callback: (File)->Unit, multiple: Boolean): Element
    fun createVideo(src: String): MediaElement
    fun createVideo(src: String, callback: ()->Unit): MediaElement
    fun createVideo(srcs: Array<String>): MediaElement
    fun createVideo(srcs: Array<String>, callback: ()->Unit): MediaElement
    @JsName("createCapture")
    fun _createCapture(type: String): Element
    @JsName("createCapture")
    fun _createCapture(type: String, callback: (dynamic)->Unit): Element
    fun createElement(tag: String): Element
    fun createElement(tag: String, content: String): Element


    // RENDERING

    class Graphics: Element {
        fun reset()
    }

    class Renderer: Element

    fun createCanvas(w: Number, h: Number): Renderer
    @JsName("createCanvas")
    fun _createCanvas(w: Number, h: Number, rendererMode: String): Renderer
    fun resizeCanvas(w: Number, h: Number)
    fun resizeCanvas(w: Number, h: Number, noRedraw: Boolean)
    fun noCanvas()
    fun createGraphics(w: Number, h: Number): Graphics
    @JsName("createGraphics")
    fun _createGraphics(w: Number, h: Number, rendererMode: String): Graphics
    @JsName("blendMode")
    fun _blendMode(mode: String)
    val drawingContext: dynamic
    @JsName("setAttributes")
    fun _setAttributes(key: String, value: Boolean)
    @JsName("canvas")
    val _canvas: dynamic // TODO: Remove dynamic


    // TRANSFORM
    fun applyMatrix(a: Number, b: Number, c: Number, d: Number, e: Number, f: Number)
    fun applyMatrix(a: Number, b: Number, c: Number, d: Number,
                    e: Number, f: Number, g: Number, h: Number,
                    i: Number, j: Number, k: Number, l: Number,
                    m: Number, n: Number, o: Number, p: Number)
    fun rotateMatrix()
    fun rotate(angle: Number)
    fun rotate(angle: Number, axis: Vector)
    fun rotateX(angle: Number)
    fun rotateY(angle: Number)
    fun rotateZ(angle: Number)
    fun scale(s: Number)
    fun scale(x: Number, y: Number)
    fun scale(x: Number, y: Number, z: Number)
    fun scale(scales: Vector)
    fun shearX(angle: Number)
    fun shearY(angle: Number)
    fun translate(x: Number, y: Number)
    fun translate(x: Number, y: Number, z: Number)
    fun translate(vector: Vector)


    // DATA
    fun storeItem(key: String, value: String)
    fun storeItem(key: String, value: Number)
    fun storeItem(key: String, value: Boolean)
    fun storeItem(key: String, value: Color)
    fun storeItem(key: String, value: Vector)
    fun <T> getItem(key: String): T?
    fun clearStructure()
    fun removeItem(key: String)

    fun hex(n: Number): String
    fun hex(n: Number, digits: Number): String
    fun hex(ns: Array<Number>): Array<String>
    fun hex(ns: Array<Number>, digits: Number): Array<String>


    // EVENTS

    // Acceleration
    @JsName("deviceOrientation")
    val _deviceOrientation: String?
    val accelerationX: Number
    val accelerationY: Number
    val accelerationZ: Number
    val pAccelerationX: Number
    val pAccelerationY: Number
    val pAccelerationZ: Number
    val rotationX: Number
    val rotationY: Number
    val rotationZ: Number
    val pRotationX: Number
    val pRotationY: Number
    val pRotationZ: Number
    val turnAxis: String
    fun setMoveThreshold(value: Number)
    fun setShakeThreshold(value: Number)
    var deviceMoved: (()->Unit)? // TODO: Add to SketchContext
    var deviceTurned: (()->Unit)? // TODO: Add to SketchContext
    var deviceShaken: (()->Unit)? // TODO: Add to SketchContext

    // Keyboard
    val isKeyPressed: Boolean
    val key: String
    val keyCode: Number
    var keyPressed: (()->Unit)? // TODO: Add to SketchContext
    var keyReleased: (()->Unit)? // TODO: Add to SketchContext
    var keyTyped: (()->Unit)? // TODO: Add to SketchContext
    fun keyIsDown(code: Int): Boolean

    class WheelEvent {
        val delta: Number
    }

    // Mouse
    val movedX: Number
    val movedY: Number
    val mouseX: Number
    val mouseY: Number
    val pmouseX: Number
    val pmouseY: Number
    val winMouseX: Number
    val winMouseY: Number
    val pwinMouseX: Number
    val pwinMouseY: Number
    val mouseButton: String
    val mouseIsPressed: Boolean
    var mouseMoved: (()->Unit)? // TODO: Add to SketchContext
    var mouseDragged: (()-> Unit)? // TODO: Add to SketchContext
    var mousePressed: (()->Unit)? // TODO: Add to SketchContext
    var mouseReleased: (()-> Unit)? // TODO: Add to SketchContext
    var mouseClicked: (()->Unit)? // TODO: Add to SketchContext
    var doubleClicked: (()-> Unit)? // TODO: Add to SketchContext
    var mouseWheel: ((WheelEvent)-> Unit)? // TODO: Add to SketchContext, Remove Dynamic
    fun requestPointerLock()
    fun exitPointerLock()

    // Touch
    val touches: Array<dynamic> // TODO: Remove Dynamic
    var touchStarted: (()->Unit)? // TODO: Add to SketchContext
    var touchMoved: (()->Unit)? // TODO: Add to SketchContext
    var touchEnded: (()->Unit)? // TODO: Add to SketchContext


    // IMAGE

    class Image {
        val width: Number
        val height: Number
        val pixels: Array<Int>
        fun loadPixels()
        fun updatePixels()
        fun get(): Image
        fun get(x: Number, y: Number): Image
        fun get(x: Number, y: Number, w: Number, h: Number): Image
        fun set(x: Number, y: Number, a: Number)
        fun set(x: Number, y: Number, a: Array<Number>)
        fun set(x: Number, y: Number, a: Color)
        fun set(x: Number, y: Number, a: Image)
        fun resize(width: Number, height: Number)
        fun copy(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int)
        fun copy(srcImage: Image, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int)
        fun mask(srcImage: Image)
        @JsName("filter")
        fun _filter(filterType: String)
        @JsName("filter")
        fun _filter(filterType: String, filterParam: Number)
        @JsName("blend")
        fun _blend(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: String)
        @JsName("blend")
        fun _blend(srcImage: Image, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: String)
        @JsName("save")
        fun _save(filename: String, extension: String)
        fun reset()
        fun getCurrentFrame(): Number
        fun setFrame(index: Number)
        fun numFrames(): Number
        fun play()
        fun pause()
        fun delay(d: Number)
        fun delay(d: Number, index: Number)
    }

    fun createImage(width: Int, height: Int): Image
    fun saveCanvas(fileName: String)
    @JsName("saveCanvas")
    fun _saveCanvas(filename: String, extension: String)
    fun saveCanvas(selectedCanvas: Element, filename: String)
    @JsName("saveCanvas")
    fun _saveCanvas(selectedCanvas: Element, filename: String, extension: String)
    @JsName("saveFrames")
    fun _saveFrames(filename: String, extension: String, duration: Number, framerate: Number)
    @JsName("saveFrames")
    fun _saveFrames(filename: String, extension: String, duration: Number, framerate: Number, callback: (dynamic)->Unit)

    // Loading & Displaying
    fun loadImage(path: String): Image
    fun loadImage(path: String, successCallback: (Image)->Unit): Image
    fun loadImage(path: String, successCallback: (Image)->Unit, failureCallback: (dynamic)->Unit): Image
    fun image(img: Image, x: Number, y: Number)
    fun image(img: Image, x: Number, y: Number, width: Number, height: Number)
    fun image(img: Image, dx: Number, dy: Number, dWidth: Number, dHeight: Number, sx: Number, sy: Number)
    fun image(img: Image, dx: Number, dy: Number, dWidth: Number, dHeight: Number,
              sx: Number, sy: Number, sWidth: Number, sHeight: Number)
    fun tint(gray: Number)
    fun tint(gray: Number, alpha : Number)
    fun tint(v1: Number, v2: Number, v3: Number)
    fun tint(v1: Number, v2: Number, v3: Number, alpha: Number)
    fun tint(colorString: String)
    fun tint(colorArray: Array<Color>)
    fun tint(color: Color)
    fun noTint()
    @JsName("imageMode")
    fun _imageMode(modeString: String)

    //Pixels
    @JsName("pixels")
    val _pixels: Array<Int>
    @JsName("loadPixels")
    fun _loadPixels()
    @JsName("updatePixels")
    fun _updatePixels()
    @JsName("blend")
    fun _blend(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: String)
    @JsName("blend")
    fun _blend(srcImage: Image, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: String)
    fun copy(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int)
    fun copy(srcImage: Image, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int)
    @JsName("filter")
    fun _filter(filterType: String) // TODO: Make Enum
    @JsName("filter")
    fun _filter(filterType: String, filterParam: Number) // TODO: Make Enum
    fun get(): Image
    fun get(x: Number, y: Number): Image
    fun get(x: Number, y: Number, w: Number, h: Number): Image
    fun set(x: Number, y: Number, a: Number)
    fun set(x: Number, y: Number, a: Array<Number>)
    fun set(x: Number, y: Number, a: Color)
    fun set(x: Number, y: Number, a: Image)

    // IO
    fun loadStrings(filename: String): Array<String>
    fun loadStrings(filename: String, callback:(Array<String>)->Unit): Array<String>
    fun loadStrings(filename: String, callback:(Array<String>)->Unit, errorCallback:(dynamic)->Unit): Array<String>

    fun loadJSON(path: String): dynamic // TODO: Remove Dynamic
    fun loadJSON(path: String, callback: (dynamic)->Unit): dynamic // TODO: Remove Dynamic
    fun loadJSON(path: String, callback: (dynamic)->Unit, errorCallback: ()->Unit): dynamic // TODO: Remove Dynamic
    @JsName("loadJSON")
    fun _loadJSON(path: String, datatype: String): dynamic // TODO: Remove Dynamic, make enum
    @JsName("loadJSON")
    fun _loadJSON(path: String, datatype: String, callback: (dynamic)->Unit): dynamic // TODO: Remove Dynamic, make enum
    @JsName("loadJSON")
    fun _loadJSON(path: String, datatype: String, callback: (dynamic)->Unit, errorCallback: ()->Unit): dynamic // TODO: Remove Dynamic, make enum

    @JsName("httpDo")
    fun _httpDo(
        path: String,
        method: String = definedExternally,
        datatype: String = definedExternally,
        data: dynamic = definedExternally,
        callback: (dynamic) -> Unit,
        errorCallback: () -> Unit = definedExternally
    ): dynamic // TODO: Remove Dynamic, make enum

    class PrintWriter {
        fun write(data: dynamic)
        fun print(data: dynamic)
        fun clear()
        fun close()
    }

    fun createWriter(name: String): PrintWriter

    fun save(filename: String)
    fun save(obj: Element, fileName: String)
    fun save(obj: String, fileName: String)
    fun save(obj: Array<String>, fileName: String)
    fun save(obj: Image, fileName: String)
    fun save(filename: String, options: dynamic)
    fun save(obj: Element, fileName: String, options: dynamic)
    fun save(obj: String, fileName: String, options: dynamic)
    fun save(obj: Array<String>, fileName: String, options: dynamic)
    fun save(obj: Image, fileName: String, options: dynamic)

    // Table
    class Table() {
        constructor(rows: Array<TableRow>)

        val columns: Array<String>
        val rows: Array<TableRow>

        fun addRow(): TableRow
        fun addRow(row: TableRow): TableRow
        fun removeRow(id: Int)
        fun getRow(id: Int): TableRow
        fun getRows(): Array<TableRow>
        fun findRow(value: String, id: Int): TableRow
        fun findRow(value: String, header: String): TableRow
        fun findRows(value: String, columnId: Int): Array<TableRow>
        fun findRows(value: String, columnHeader: String): Array<TableRow>
        fun matchRow(regExp: RegExp, columnId: Int): TableRow
        fun matchRow(string: String, columnId: Int): TableRow
        fun matchRow(regExp: RegExp, columnHeader: String): TableRow
        fun matchRow(string: String, columnHeader: String): TableRow
        fun matchRows(regExp: RegExp, columnId: Int): Array<TableRow>
        fun matchRows(string: String, columnId: Int): Array<TableRow>
        fun matchRows(regExp: RegExp, columnHeader: String): Array<TableRow>
        fun matchRows(string: String, columnHeader: String): Array<TableRow>
        fun getColumn(columnId: Int): Array<Int>
        fun getColumn(columnHeader: String): Array<Int>
        fun clearRows()
        fun addColumn()
        fun addColumn(title: String)
        fun getColumnCount(): Int
        fun getRowCount(): Int
        fun removeTokens(chars: String, columnId: Int)
        fun removeTokens(chars: String, columnHeader: String)
        fun trim(columnId: Int)
        fun trim(columnHeader: String)
        fun removeColumn(columnId: Int)
        fun removeColumn(columnHeader: String)
        fun set(row: Int, columnId: Int, value: String)
        fun set(row: Int, columnId: Int, value: Number)
        fun set(row: Int, columnHeader: String, value: String)
        fun set(row: Int, columnHeader: String, value: Number)
        fun setNum(row: Int, columnId: Int, value: Number)
        fun setNum(row: Int, columnHeader: String, value: Number)
        fun setString(row: Int, columnId: Int, value: String)
        fun setString(row: Int, columnHeader: String, value: String)
        fun <T> get(row: Int, columnId: Int): T
        fun <T> get(row: Int, columnHeader: String): T
        fun getNum(row: Int, columnId: Int): Number
        fun getNum(row: Int, columnHeader: String): Number
        fun getString(row: Int, columnId: Int): String
        fun getString(row: Int, columnHeader: String): String
        fun getObject()
        fun getObject(headerColumn: String): Json
        fun getArray(): Array<Array<Any>>
    }

    class TableRow(str: String = definedExternally, separator: String = definedExternally) {
        fun set(columnId: Int, value: Number)
        fun set(columnId: Int, value: String)
        fun set(columnHeader: String, value: Number)
        fun set(columnHeader: String, value: String)
        fun setNum(columnId: Int, value: Number)
        fun setNum(columnHeader: String, value: Number)
        fun setString(columnId: Int, value: String)
        fun setString(columnHeader: String, value: String)
        fun <T> get(columnId: Int): T
        fun <T> get(columnHeader: String): T
        fun getNum(columnId: Int): Number
        fun getNum(columnHeader: String): Number
        fun getString(columnId: Int): String
        fun getString(columnHeader: String): String
    }

    fun saveTable(table: Table, filename: String)
    @JsName("saveTable")
    fun _saveTable(table: Table, filename: String, tableExtension: String)

    fun loadTable(filename: String): Table
    @JsName("loadTable")
    fun _loadTable(filename: String, extension: String): Table
    @JsName("loadTable")
    fun _loadTable(filename: String, extension: String, header: String): Table
    @JsName("loadTable")
    fun _loadTable(filename: String, extension: String, header: String, callback: (Table)->Unit): Table
    @JsName("loadTable")
    fun _loadTable(filename: String, extension: String, header: String, callback: (Table)->Unit, errorCallback: (dynamic)->Unit): Table




    // Time & Date

    fun day(): Int
    fun hour(): Int
    fun minute(): Int
    fun millis(): Int
    fun month(): Int
    fun second(): Int
    fun year(): Int

    // MATH

    fun map(value: Number, start1: Number, stop1: Number, start2: Number, stop2: Number): Number
    fun map(value: Number, start1: Number, stop1: Number, start2: Number, stop2: Number, withinBounds: Boolean): Number

    open class Vector protected constructor() {
        var x: Number
        var y: Number
        var z: Number
        override fun toString(): String
        fun set(x: Number, y: Number, z: Number)
        fun set(value: Vector)
        fun copy(): Vector
        fun add(value: Vector) // TODO: Make Binary Overload
        fun rem(value: Vector)
        fun sub(value: Vector) // TODO: Make Binary Overload
        fun mult(n: Number) // TODO: Make Binary Overload
        fun mult(v: Vector) // TODO: Make Binary Overload
        fun div(n: Number) // TODO: Make Binary Overload
        fun div(v: Vector) // TODO: Make Binary Overload
        fun mag(): Number
        fun magSq(): Number
        fun dot(v: Vector): Number // TODO: Make Binary Overload
        fun cross(v: Vector): Vector // TODO: Make Binary Overload
        fun dist(v: Vector): Number // TODO: Make Binary Overload
        fun normalize(): Vector
        fun limit(n: Number): Vector
        fun setMag(): Vector
        fun heading(): Number
        fun setHeading(angle: Number)
        fun rotate(angle: Number)
        fun angleBetween(v: Vector): Number
        fun lerp(v: Vector): Vector
        fun reflect(v: Vector): Vector
        fun array(): Array<Number>
        fun equals(v: Vector): Boolean

        companion object {
            fun fromAngle(angle: Number): Vector
            fun fromAngle(angle: Number, length: Number): Vector
            fun fromAngles(theta: Number, phi: Number): Vector
            fun fromAngles(theta: Number, phi: Number, length: Number): Vector
            fun random2D(): Vector
            fun random3D(): Vector
            fun add(v1: Vector, v2: Vector): Vector
            fun rem(v1: Vector, v2: Vector): Vector
            fun sub(v1: Vector, v2: Vector): Vector
            fun mult(v: Vector, n: Number): Vector
            fun mult(v1: Vector, v2: Vector): Vector
            fun div(v: Vector, n: Number): Vector
            fun div(v1: Vector, v2: Vector): Vector
        }
    }

    fun createVector(): Vector
    fun createVector(x: Number, y: Number): Vector
    fun createVector(x: Number, y: Number, z: Number): Vector

    // Noise
    fun noise(x: Number): Number
    fun noise(x: Number, y: Number): Number
    fun noise(x: Number, y: Number, z: Number): Number
    fun noiseDetail(lod: Number, falloff: Number)
    fun noiseSeed(seed: Int)

    // Random
    fun randomSeed(seed: Int)
    fun random(): Number
    fun random(max: Number): Number
    fun random(min: Number, max: Number): Number
    fun <T> random(choices: Array<T>): T
    fun randomGaussian(): Number
    fun randomGaussian(mean: Number): Number
    fun randomGaussian(mean: Number, sd: Number): Number

    // Trig
    @JsName("angleMode")
    fun _angleMode(mode: String)

    // TYPOGRAPHY

    open class Font {
        val font: dynamic // TODO: Remove Dynamic
        fun textBounds(text: String, x: Number, y: Number): dynamic // TODO: Remove Dynamic
        fun textBounds(text: String, x: Number, y: Number, fontSize: Number): dynamic // TODO: Remove Dynamic
        fun textBounds(text: String, x: Number, y: Number, fontSize: Number, options: dynamic): dynamic // TODO: Remove Dynamic
        fun textToPoints(text: String, x: Number, y: Number): Array<dynamic> // TODO: Remove Dynamic
        fun textToPoints(text: String, x: Number, y: Number, fontSize: Number): Array<dynamic> // TODO: Remove Dynamic
        fun textToPoints(text: String, x: Number, y: Number, fontSize: Number, options: dynamic): Array<dynamic> // TODO: Remove Dynamic
    }

    @JsName("textAlign")
    fun _textAlign(horizAlign: String) // TODO: Make Enum
    @JsName("textAlign")
    fun _textAlign(horizAlign: String, vertAlign: String) // TODO: Make Enum
    fun textLeading(): Number
    fun textLeading(leading: Number)
    fun textSize(): Number
    fun textSize(size: Number)
    @JsName("textStyle")
    fun _textStyle(): String // TODO: Make Enum
    @JsName("textStyle")
    fun _textStyle(style: String) // TODO: Make Enum
    fun textWidth(text: String): Number
    fun textAscent(): Number
    fun textDecent(): Number
    @JsName("textWrap")
    fun _textWrap(): String // TODO: Make Enum
    @JsName("textWrap")
    fun _textWrap(wrapStyle: String) // TODO: Make Enum
    fun loadFont(path: String): Font
    fun loadFont(path: String, callback: ()->Unit): Font
    fun loadFont(path: String, callback: (Font)->Unit, onError: ()->Unit): Font
    fun text(str: String, x: Number, y: Number)
    fun text(str: String, x: Number, y: Number, x2: Number, y2: Number)
    fun textFont(): Font?
    fun textFont(font: Font)
    fun textFont(font: Font, size: Number)
    fun textFont(fontName: String)
    fun textFont(fontName: String, size: Number)

    // 3D
    // Material

    class Texture

    @JsName("Shader")
    open class _Shader {
        @JsName("setUniform")
        fun _setUniform(uniformName: String, data: Boolean)
        @JsName("setUniform")
        fun _setUniform(uniformName: String, data: Number)
        @JsName("setUniform")
        fun _setUniform(uniformName: String, data: Array<Number>)
        @JsName("setUniform")
        fun _setUniform(uniformName: String, data: Image)
        @JsName("setUniform")
        fun _setUniform(uniformName: String, data: Graphics)
        @JsName("setUniform")
        fun _setUniform(uniformName: String, data: MediaElement)
        @JsName("setUniform")
        fun _setUniform(uniformName: String, data: Texture)
    }

    @JsName("loadShader")
    fun _loadShader(vertFilename: String, fragFilename: String): _Shader
    @JsName("loadShader")
    fun _loadShader(vertFilename: String, fragFilename: String, callback: (_Shader)->Unit):  _Shader
    @JsName("loadShader")
    fun _loadShader(vertFilename: String, fragFilename: String, callback: (_Shader)->Unit, errorCallback: (dynamic)->Unit):  _Shader
    @JsName("createShader")
    fun _createShader(vertSrc: String, fragSrc: String):  _Shader
    @JsName("shader")
    fun _shader(s: _Shader)
    fun resetShader()
    fun texture(tex: Image)
    fun texture(tex: MediaElement)
    fun texture(tex: Graphics)
    fun texture(tex: Texture)
    @JsName("textureMode")
    fun _textureMode(mode: String) // TODO: Make Enum
    @JsName("textureMode")
    fun _textureWrap(wrapX: String, wrapY: String) // TODO: Make Enum



}

