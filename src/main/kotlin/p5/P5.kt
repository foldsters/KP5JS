package p5

import kotlinx.html.InputType
import org.w3c.dom.Path2D
import kotlin.js.Json
import kotlin.js.json
import kotlin.math.PI
import kotlin.math.sign
import kotlin.reflect.KProperty

@JsModule("p5")
@JsNonModule
@JsName("p5")
private external val p5: dynamic

open class P5(val sketch: (P5)->Unit) : NativeP5(sketch) {

    fun nativeP5(): dynamic {
        return this.asDynamic()
    }

    // MODE EXTENSION FUNCTIONS

    enum class DescriptionMode(val nativeValue: String) {
        LABEL("label"),
        FALLBACK("fallback")
    }
    fun describe(text : String, display: DescriptionMode) {
        _describe(text, display.nativeValue)
    }
    fun describeElement(name: String, text: String, display: DescriptionMode) {
        _describeElement(name, text, display.nativeValue)
    }
    fun textOutput(display: DescriptionMode) {
        _textOutput(display.nativeValue)
    }
    fun gridOutput(display: DescriptionMode) {
        _gridOutput(display.nativeValue)
    }

    enum class ColorMode(val nativeValue: String) {
        RGB("rgb"),
        HSB("hsb"),
        HSL("hsl")
    }
    fun colorMode(mode: ColorMode) {
        _colorMode(mode.nativeValue)
    }
    fun colorMode(mode: ColorMode, max: Number) {
        _colorMode(mode.nativeValue, max)
    }
    fun colorMode(mode: ColorMode, max1: Number, maxA: Number) {
        _colorMode(mode.nativeValue, max1, maxA)
    }
    fun colorMode(mode: ColorMode, max1: Number, max2: Number, max3: Number) {
        _colorMode(mode.nativeValue, max1, max2, max3)
    }
    fun colorMode(mode: ColorMode, max1: Number, max2: Number, max3: Number, maxA: Number) {
        _colorMode(mode.nativeValue, max1, max2, max3, maxA)
    }

    enum class ArcMode(val nativeValue: String) {
        CHORD("chord"),
        PIE("pie"),
        OPEN("open")
    }
    fun arc(x: Number, y: Number, width: Number, height: Number, startRad: Number, stopRad: Number, mode: ArcMode) {
        return _arc(x, y, width, height, startRad, stopRad, mode.nativeValue)
    }
    fun arc(x: Number, y: Number, width: Number, height: Number, startRad: Number, stopRad: Number, mode: ArcMode, detail: Int) {
        return _arc(x, y, width, height, startRad, stopRad, mode.nativeValue, detail)
    }
    fun arc(xy: Vector, width: Number, height: Number, startRad: Number, stopRad: Number, mode: ArcMode) {
        return _arc(xy.x, xy.y, width, height, startRad, stopRad, mode.nativeValue)
    }
    fun arc(xy: Vector, width: Number, height: Number, startRad: Number, stopRad: Number, mode: ArcMode, detail: Int) {
        return _arc(xy.x, xy.y, width, height, startRad, stopRad, mode.nativeValue, detail)
    }
    fun arc(xy: Vector, wh: Vector, startRad: Number, stopRad: Number, mode: ArcMode) {
        return _arc(xy.x, xy.y, wh.x, wh.y, startRad, stopRad, mode.nativeValue)
    }
    fun arc(xy: Vector, wh: Vector, startRad: Number, stopRad: Number, mode: ArcMode, detail: Int) {
        return _arc(xy.x, xy.y, wh.x, wh.y, startRad, stopRad, mode.nativeValue, detail)
    }



    enum class CenterMode(val nativeValue: String) {
        CENTER("center"),
        RADIUS("radius"),
        CORNER("corner"),
        CORNERS("corners")
    }
    fun ellipseMode(mode: CenterMode) {
        _ellipseMode(mode.nativeValue)
    }
    fun rectMode(mode: CenterMode) {
        _rectMode(mode.nativeValue)
    }

    enum class CapMode(val nativeValue: String) {
        ROUND("round"),
        SQUARE("butt"),
        PROJECT("square")
    }
    fun strokeCap(mode: CapMode) {
        _strokeCap(mode.nativeValue)
    }


    enum class JoinMode(val nativeValue: String) {
        MITER("miter"),
        BEVEL("bevel"),
        ROUND("round")
    }
    fun strokeJoin(mode: JoinMode) {
        _strokeJoin(mode.nativeValue)
    }

    object CLOSE
    enum class PathMode(val nativeValue: dynamic) {
        POINTS(0),
        LINES(1),
        TRIANGLES(4),
        TRIANGLE_FAN(6),
        TRIANGLE_STRIP(5),
        QUADS("quads"),
        QUAD_STRIP("quad_strip"),
        TESS("tess")
    }

    inner class ShapeScope {
        fun beginShape() = _beginShape()
        fun beginShape(kind: PathMode) {
            when(val value = kind.nativeValue) {
                is String -> _beginShapeString(value as String)
                is Number -> _beginShapeNumber(value as Number)
            }
        }
        fun endShape() = _endShape()
        fun endShape(mode: CLOSE) {
            _endShape("close")
        }
        fun vertex(x: Number, y: Number) = _vertex(x, y)
        fun vertex(x: Number, y: Number, z: Number) = _vertex(x, y, z)
        fun vertex(x: Number, y: Number, u: Number, v: Number) = _vertex(x, y, u, v)
        fun vertex(x: Number, y: Number, z: Number, u: Number, v: Number) = _vertex(x, y, z, u, v)
        fun quadraticVertex(cx: Number, cy: Number, x3: Number, y3: Number) = _quadraticVertex(cx, cy, x3, y3)
        fun quadraticVertex(cx: Number, cy: Number, cz: Number, x3: Number, y3: Number, z3: Number) = _quadraticVertex(cx, cy, cz, x3, y3, z3)
        fun bezierVertex(x2: Number, y2: Number, x3: Number, y3: Number, x4: Number, y4: Number) = _bezierVertex(x2, y2, x3, y3, x4, y4)
        fun bezierVertex(
            x2: Number, y2: Number, z2: Number,
            x3: Number, y3: Number, z3: Number,
            x4: Number, y4: Number, z4: Number
        ) = _bezierVertex(x2, y2, z2, x3, y3, z3, x4, y4, z4)
        fun curveVertex(x: Number, y: Number) = _curveVertex(x, y)
        fun curveVertex(x: Number, y: Number, z: Number) = _curveVertex(x, y, z)
        fun normal(vector: Vector) = _normal(vector)
        fun normal(x: Number, y: Number, z: Number) = _normal(x, y, z)

        fun withContour(contour: ()->Unit) {
            _beginContour()
            contour()
            _endContour()
        }
    }


    enum class AlignMode(val nativeValue: String) {
        VERTICAL("vertical"),
        HORIZONTAL("horizontal")
    }
    fun Element.center(alignMode: AlignMode) {
        center(alignMode.nativeValue)
    }

    enum class PositionMode(val nativeValue: String) {
        STATIC("static"),
        FIXED("fixed"),
        RELATIVE("relative"),
        STICKY("sticky"),
        INITIAL("initial"),
        INHERIT("inherit")
    }
    fun Element.position(x: Number, y: Number, positionMode: PositionMode) {
        position(x, y, positionMode.nativeValue)
    }

    object AUTO
    fun Element.size(w: AUTO) {
        _size("auto")
    }
    fun Element.size(w: AUTO, h: Number) {
        _size("auto", h)
    }
    fun Element.size(w: Number, h: AUTO) {
        _size(w, "auto")
    }

    enum class CrossOriginMode(val nativeValue: String) {
        ANONYMOUS("anonymous"),
        USE_CREDENTIALS("use-credentials"),
        NONE("")
    }
    fun createImg(srcPath: String, altText: String, crossOrigin: CrossOriginMode): Element {
        return _createImg(srcPath, altText, crossOrigin.nativeValue)
    }
    fun createImg(srcPath: String, altText: String, crossOrigin: CrossOriginMode, loadedCallback: (Element)->Unit): Element {
        return _createImg(srcPath, altText, crossOrigin.nativeValue, loadedCallback)
    }

    enum class TargetMode(val nativeValue: String) {
        BLANK("_blank"),
        SELF("_self"),
        PARENT("_parent"),
        TOP("_top")
    }
    fun createA(href: String, html: String, target: TargetMode): Element {
        return _createA(href, html, target.nativeValue)
    }


    enum class InputMode(val nativeValue: String) {
        TEXT("text"),
        PASSWORD("password")
    }
    fun createInput(default: String, type: InputMode): Element {
        return _createInput(default, type.nativeValue)
    }


    enum class CaptureMode(val nativeValue: String) {
        AUDIO("audio"),
        VIDEO("video"),
        BOTH("")
    }
    fun createCapture(type: CaptureMode): Element {
        return _createCapture(type.nativeValue)
    }
    fun createCapture(type: CaptureMode, callback: (dynamic)->Unit): Element {
        return _createCapture(type.nativeValue, callback)
    }

    enum class RenderMode(val nativeValue: String) {
        P2D("p2d"),
        WEBGL("webgl"),
        WEBGL2("webgl")
    }
    fun createCanvas(w: Number, h: Number, renderMode: RenderMode): Renderer {
        if (renderMode == RenderMode.WEBGL2) { enableWebgl2() }
        return _createCanvas(w, h, renderMode.nativeValue)
    }
    fun createGraphics(w: Number, h: Number, renderMode: RenderMode): Graphics {
        if (renderMode == RenderMode.WEBGL2) { enableWebgl2() }
        return _createGraphics(w, h, renderMode.nativeValue)
    }

    enum class BlendMode(val nativeValue: String) {
        BLEND("source-over"),
        DARKEST("darken"),
        LIGHTEST("lighten"),
        DIFFERENCE("difference"),
        MULTIPLY("multiply"),
        EXCLUSION("exclusion"),
        SCREEN("screen"),
        REPLACE("copy"),
        OVERLAY("overlay"),
        HARD_LIGHT("hard-light"),
        SOFT_LIGHT("soft-light"),
        DODGE("color-dodge"),
        BURN("color-burn"),
        ADD("lighter"),
        NORMAL("normal"),
        REMOVE("destination-out"),
        SUBTRACT("subtract")
    }
    fun blendMode(mode: BlendMode) {
        _blendMode(mode.nativeValue)
    }
    fun Image.blend(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: BlendMode) {
        _blend(sx, sy, sw, sh, dx, dy, dw, dh, blendMode.nativeValue)
    }
    fun Image.blend(srcImage: Image, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: BlendMode) {
        _blend(srcImage, sx, sy, sw, sh, dx, dy, dw, dh, blendMode.nativeValue)
    }
    fun blend(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: BlendMode) {
        _blend(sx, sy, sw, sh, dx, dy, dw, dh, blendMode.nativeValue)
    }
    fun blend(srcImage: Image, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: BlendMode) {
        _blend(srcImage, sx, sy, sw, sh, dx, dy, dw, dh, blendMode.nativeValue)
    }

    enum class AngleMode(val nativeValue: String) {
        RADIANS("radians"),
        DEGREES("degrees")
    }
    fun angleMode(mode: AngleMode) {
        _angleMode(mode.nativeValue)
    }


    @Suppress("EnumEntryName")
    enum class RenderAttribute(val nativeValue: String) {
        ALPHA("alpha"),
        DEPTH("depth"),
        STENCIL("stencil"),
        ANTIALIAS("antialias"),
        PREMULTIPLIED_ALPHA("premultipliedAlpha"),
        PRESERVE_DRAWING_BUFFER("preserveDrawingBuffer"),
        PER_PIXEL_LIGHTING("perPixelLighting"),
    }
    fun setAttributes(key: RenderAttribute, value: Boolean) {
        return _setAttributes(key.nativeValue, value)
    }

    enum class DeviceOrientation(val nativeValue: String) {
        LANDSCAPE("landscape"),
        PORTRAIT("portrait")
    }
    val deviceOrientation: DeviceOrientation?
        get() = when(_deviceOrientation) {
            "landscape" -> DeviceOrientation.LANDSCAPE
            "portrait" -> DeviceOrientation.PORTRAIT
            else -> null
        }

    enum class FilterMode(val nativeValue: String) {
        THRESHOLD("threshold"),
        GRAY("gray"),
        OPAQUE("opaque"),
        INVERT("invert"),
        POSTERIZE("posterize"),
        ERODE("erode"),
        DILATE("dilate"),
        BLUR("blur")
    }
    fun Image.filter(filterMode: FilterMode) {
        _filter(filterMode.nativeValue)
    }
    fun Image.filter(filterMode: FilterMode, filterParam: Number) {
        _filter(filterMode.nativeValue, filterParam)
    }

    enum class ImageExtension(val nativeValue: String) {
        PNG("png"),
        JPG("jpg")
    }
    fun Image.save(filename: String, extension: ImageExtension) {
        _save(filename, extension.nativeValue)
    }
    fun saveCanvas(filename: String, extension: ImageExtension) {
        _saveCanvas(filename, extension.nativeValue)
    }
    fun saveCanvas(selectedCanvas: Element, filename: String, extension: ImageExtension) {
        _saveCanvas(selectedCanvas, filename, extension.nativeValue)
    }
    fun saveFrames(filename: String, extension: ImageExtension, duration: Number, framerate: Number) {
        _saveFrames(filename, extension.nativeValue, duration, framerate)
    }
    fun saveFrames(filename: String, extension: ImageExtension, duration: Number, framerate: Number, callback: (dynamic)->Unit) {
        _saveFrames(filename, extension.nativeValue, duration, framerate, callback)
    }

    enum class ImageMode(val nativeValue: String) {
        CENTER("center"),
        CORNER("corner"),
        CORNERS("corners")
    }
    fun imageMode(mode: ImageMode) {
        _imageMode(mode.nativeValue)
    }

    // SCOPE EXTENSION FUNCTIONS
    fun withShape(pathMode: PathMode?=null, close: CLOSE?=null, path: ShapeScope.()->Unit) {
        val shapeScope = ShapeScope()
        if (pathMode == null) shapeScope.beginShape() else shapeScope.beginShape(pathMode)
        path(shapeScope)
        if (close == null) shapeScope.endShape() else shapeScope.endShape(CLOSE)
    }

    private var webgl2Enabled: Boolean = false
    fun enableWebgl2() {
        if (webgl2Enabled) { return }
        js("""p5.RendererGL.prototype._initContext = function() {
                try {
                    this.drawingContext = 
                        this.canvas.getContext("webgl2", this._pInst._glAttributes) ||
                        this.canvas.getContext("webgl", this._pInst._glAttributes) || 
                        this.canvas.getContext("experimental-webgl", this._pInst._glAttributes);
                    if (this.drawingContext === null) { 
                        throw new Error("Error creating webgl context");
                    } 
                    var e = this.drawingContext;
                    e.enable(e.DEPTH_TEST),
                    e.depthFunc(e.LEQUAL),
                    e.viewport(0, 0, e.drawingBufferWidth, e.drawingBufferHeight),
                    this._viewport = this.drawingContext.getParameter(this.drawingContext.VIEWPORT)
                } catch (e) {
                    throw e
                }
            };"""
        )
        webgl2Enabled = true
    }

    class Shader(val nativeShader: _Shader) {

        val uniforms: MutableMap<String, Any> = mutableMapOf()

        operator fun set(uniformName: String, data: Boolean) {
            uniforms[uniformName] = data
            nativeShader._setUniform(uniformName, data)
        }
        operator fun set(uniformName: String, data: Number) {
            uniforms[uniformName] = data
            nativeShader._setUniform(uniformName, data)
        }
        operator fun set(uniformName: String, data: Array<Number>) {
            uniforms[uniformName] = data
            nativeShader._setUniform(uniformName, data)
        }
        operator fun set(uniformName: String, data: Image) {
            uniforms[uniformName] = data
            nativeShader._setUniform(uniformName, data)
        }
        operator fun set(uniformName: String, data: Graphics) {
            uniforms[uniformName] = data
            nativeShader._setUniform(uniformName, data)
        }
        operator fun set(uniformName: String, data: MediaElement) {
            uniforms[uniformName] = data
            nativeShader._setUniform(uniformName, data)
        }
        operator fun set(uniformName: String, data: Texture) {
            uniforms[uniformName] = data
            nativeShader._setUniform(uniformName, data)
        }

        operator fun <T> get(uniformName: String): T? {
            return uniforms[uniformName] as? T
        }

        fun updateUniforms(uniforms: Map<String, Any>) {
            for( (k, v) in uniforms) {
                when(v) {
                    is Boolean -> set(k, v)
                    is Number -> set(k, v)
                    is Array<*> -> set(k, v as? Array<Number> ?: arrayOf())
                    is Image -> set(k, v)
                    is Graphics -> set(k, v)
                    is MediaElement -> set(k, v)
                    is Texture -> set(k, v)
                    else -> console.warn("Invalid Shader Uniform Type")
                }
            }
        }
    }

    fun loadShader(vertFilename: String, fragFilename: String): Shader {
        return Shader(_loadShader(vertFilename, fragFilename))
    }
    fun loadShader(vertFilename: String, fragFilename: String, callback: (_Shader)->Unit): Shader {
        return Shader(_loadShader(vertFilename, fragFilename, callback))
    }
    fun loadShader(vertFilename: String, fragFilename: String, callback: (_Shader)->Unit, errorCallback: (dynamic)->Unit): Shader {
        return Shader(_loadShader(vertFilename, fragFilename, callback, errorCallback))
    }
    fun createShader(vertSrc: String, fragSrc: String): Shader {
        return Shader(_createShader(vertSrc, fragSrc))
    }
    fun shader(s: Shader) {
        _shader(s.nativeShader)
    }

    fun withPixels(density: Int = 1, block: PixelScope.()->Unit) {
        val pixelScope = PixelScope(density)
        if (density != pixelDensity()) {
            pixelDensity(density)
        }
        _loadPixels()
        block(pixelScope)
        _updatePixels()
    }

    inner class PixelScope(val pd: Int) {
        val pixels: Array<Int>
            get() { return _pixels }

        private fun RCToI(row: Int, col: Int): Int {
            return 4*pd*(pd*width*row + col)
        }

        private fun RCToI(row: Double, col: Double): Int {
            return 4*pd*width*(pd*row).toInt() + 4*(pd*col).toInt()
        }

        private fun RCToI(vector: Vector): Int {
            return RCToI(vector.y.toDouble(), vector.x.toDouble())
        }

        val redChannel = object: PixelArray<Int> {
            override fun set(index: Int, element: Int) {
                _pixels[index*4] = element
            }
            override fun get(index: Int): Int {
                return _pixels[index*4]
            }

            override fun set(row: Int, col: Int, element: Int) {
                _pixels[RCToI(row, col)] = element
            }

            override fun get(row: Int, col: Int): Int {
                return _pixels[RCToI(row, col)]
            }

            override fun set(row: Double, col: Double, element: Int) {
                _pixels[RCToI(row, col)] = element
            }

            override fun get(row: Double, col: Double): Int {
                return _pixels[RCToI(row, col)]
            }

            override fun set(vector: Vector, element: Int) {
                _pixels[RCToI(vector)] = element
            }

            override fun get(vector: Vector): Int {
                return _pixels[RCToI(vector)]
            }
        }

        val greenChannel = object: PixelArray<Int> {
            override fun set(index: Int, element: Int) {
                _pixels[index*4 + 1] = element
            }
            override fun get(index: Int): Int {
                return _pixels[index*4 + 1]
            }

            override fun set(row: Int, col: Int, element: Int) {
                _pixels[RCToI(row, col) + 1] = element
            }

            override fun get(row: Int, col: Int): Int {
                return _pixels[RCToI(row, col) + 1]
            }

            override fun set(row: Double, col: Double, element: Int) {
                _pixels[RCToI(row, col) + 1] = element
            }

            override fun get(row: Double, col: Double): Int {
                return _pixels[RCToI(row, col) + 1]
            }

            override fun set(vector: Vector, element: Int) {
                _pixels[RCToI(vector) + 1] = element
            }

            override fun get(vector: Vector): Int {
                return _pixels[RCToI(vector) + 1]
            }
        }

        val blueChannel = object: PixelArray<Int> {
            override fun set(index: Int, element: Int) {
                _pixels[index*4 + 2] = element
            }
            override fun get(index: Int): Int {
                return _pixels[index*4 + 2]
            }

            override fun set(row: Int, col: Int, element: Int) {
                _pixels[RCToI(row, col) + 2] = element
            }

            override fun get(row: Int, col: Int): Int {
                return _pixels[RCToI(row, col) + 2]
            }

            override fun set(row: Double, col: Double, element: Int) {
                _pixels[RCToI(row, col) + 2] = element
            }

            override fun get(row: Double, col: Double): Int {
                return _pixels[RCToI(row, col) + 2]
            }

            override fun set(vector: Vector, element: Int) {
                _pixels[RCToI(vector) + 2] = element
            }

            override fun get(vector: Vector): Int {
                return _pixels[RCToI(vector) + 2]
            }
        }

        val alphaChannel = object: PixelArray<Int> {
            override fun set(index: Int, element: Int) {
                _pixels[index*4 + 3] = element
            }
            override fun get(index: Int): Int {
                return _pixels[index*4 + 3]
            }

            override fun set(row: Int, col: Int, element: Int) {
                _pixels[RCToI(row, col) + 3] = element
            }

            override fun get(row: Int, col: Int): Int {
                return _pixels[RCToI(row, col) + 3]
            }

            override fun set(row: Double, col: Double, element: Int) {
                _pixels[RCToI(row, col) + 3] = element
            }

            override fun get(row: Double, col: Double): Int {
                return _pixels[RCToI(row, col) + 3]
            }

            override fun set(vector: Vector, element: Int) {
                _pixels[RCToI(vector) + 3] = element
            }

            override fun get(vector: Vector): Int {
                return _pixels[RCToI(vector) + 3]
            }
        }

        val colorArray = object: PixelArray<Color> {
            override fun set(index: Int, element: Color) {
                redChannel[index] = red(element).toInt()
                greenChannel[index] = green(element).toInt()
                blueChannel[index] = blue(element).toInt()
                alphaChannel[index] = (255.0*alpha(element).toDouble()).toInt()
            }

            override fun get(index: Int): Color {
                return color(
                    redChannel[index],
                    greenChannel[index],
                    blueChannel[index],
                    alphaChannel[index]
                )
            }

            override fun set(row: Int, col: Int, element: Color) {
                redChannel[row, col] = red(element).toInt()
                greenChannel[row, col] = green(element).toInt()
                blueChannel[row, col] = blue(element).toInt()
                alphaChannel[row, col] = (255.0*alpha(element).toDouble()).toInt()
            }

            override fun get(row: Int, col: Int): Color {
                return color(
                    redChannel[row, col],
                    greenChannel[row, col],
                    blueChannel[row, col],
                    alphaChannel[row, col]
                )
            }

            override fun set(row: Double, col: Double, element: Color) {
                redChannel[row, col] = red(element).toInt()
                greenChannel[row, col] = green(element).toInt()
                blueChannel[row, col] = blue(element).toInt()
                alphaChannel[row, col] = (255.0*alpha(element).toDouble()).toInt()
            }

            override fun get(row: Double, col: Double): Color {
                return color(
                    redChannel[row, col],
                    greenChannel[row, col],
                    blueChannel[row, col],
                    alphaChannel[row, col]
                )
            }

            override fun set(vector: Vector, element: Color) {
                //console.log(red(element).toInt(), green(element).toInt(), blue(element).toInt(), alpha(element).toInt())
                redChannel[vector] = red(element).toInt()
                greenChannel[vector] = green(element).toInt()
                blueChannel[vector] = blue(element).toInt()
                alphaChannel[vector] = (255.0*alpha(element).toDouble()).toInt()
            }

            override fun get(vector: Vector): Color {
                return color(
                    redChannel[vector],
                    greenChannel[vector],
                    blueChannel[vector],
                    alphaChannel[vector]
                )
            }
        }
    }

    interface PixelArray<T> {
        operator fun set(index: Int, element: T)
        operator fun get(index: Int): T
        operator fun set(row: Int, col: Int, element: T)
        operator fun get(row: Int, col: Int): T
        operator fun set(row: Double, col: Double, element: T)
        operator fun get(row: Double, col: Double): T
        operator fun set(vector: Vector, element: T)
        operator fun get(vector: Vector): T
    }

    enum class ScalarMode {
        X,
        XY,
        XYZ
    }

    private var scalarMode: ScalarMode = ScalarMode.XY
    fun scalarMode(axes: ScalarMode) {
        scalarMode = axes
    }
    fun scalarMode(): ScalarMode {
        return scalarMode
    }

    operator fun Vector.plus(other: Vector) = Vector.add(this, other)
    operator fun Vector.plus(other: Number): Vector {
        return when(scalarMode) {
            ScalarMode.X -> Vector.add(this, createVector(other, 0, 0))
            ScalarMode.XY -> Vector.add(this, createVector(other, other, 0))
            ScalarMode.XYZ -> Vector.add(this, createVector(other, other, other))
        }
    }

    operator fun Vector.plusAssign(other: Vector) = add(other)
    operator fun Vector.plusAssign(other: Number) {
        when(scalarMode) {
            ScalarMode.X -> add(createVector(other, 0, 0))
            ScalarMode.XY -> add(createVector(other, other, 0))
            ScalarMode.XYZ -> add(createVector(other, other, other))
        }
    }

    operator fun Vector.rem(other: Vector) = Vector.rem(this, other)
    operator fun Vector.remAssign(other: Vector) = rem(other)

    operator fun Vector.minus(other: Vector) = Vector.sub(this, other)
    operator fun Vector.minus(other: Number): Vector {
        return when(scalarMode) {
            ScalarMode.X -> Vector.sub(this, createVector(other, 0, 0))
            ScalarMode.XY -> Vector.sub(this, createVector(other, other, 0))
            ScalarMode.XYZ -> Vector.sub(this, createVector(other, other, other))
        }
    }
    operator fun Vector.minusAssign(other: Vector) = sub(other)
    operator fun Vector.minusAssign(other: Number) {
        when(scalarMode) {
            ScalarMode.X -> sub(createVector(other, 0, 0))
            ScalarMode.XY -> sub(createVector(other, other, 0))
            ScalarMode.XYZ -> sub(createVector(other, other, other))
        }
    }

    operator fun Vector.times(other: Number) = Vector.mult(this, other)
    operator fun Vector.times(other: Vector) = Vector.mult(this, other)
    operator fun Vector.timesAssign(other: Number) = mult(other)
    operator fun Vector.timesAssign(other: Vector) = mult(other)

    operator fun Vector.div(other: Number) = Vector.div(this, other)
    operator fun Vector.div(other: Vector) = Vector.div(this, other)
    operator fun Vector.divAssign(other: Number) = div(other)
    operator fun Vector.divAssign(other: Vector) = div(other)

    infix fun Vector.dot(other: Vector) = dot(other)
    infix fun Vector.cross(other: Vector) = cross(other)
    fun dist(value: Vector, other: Vector) = value.dist(other)
    infix fun Vector.dist(other: Vector) = dist(other)


    // Double Get
    val Vector.xx: Vector get() { return createVector(x, x) }
    val Vector.yy: Vector get() { return createVector(y, y) }
    val Vector.zz: Vector get() { return createVector(z, z) }

    // Triple Get
    val Vector.xxx: Vector get() { return createVector(x, x, x) }
    val Vector.xxy: Vector get() { return createVector(x, x, y) }
    val Vector.xxz: Vector get() { return createVector(x, x, z) }
    val Vector.xyx: Vector get() { return createVector(x, y, x) }
    val Vector.xyy: Vector get() { return createVector(x, y, y) }
    val Vector.xzx: Vector get() { return createVector(x, z, x) }
    val Vector.xzz: Vector get() { return createVector(x, z, z) }
    val Vector.yxx: Vector get() { return createVector(y, x, x) }
    val Vector.yxy: Vector get() { return createVector(y, x, y) }
    val Vector.yyx: Vector get() { return createVector(y, y, x) }
    val Vector.yyy: Vector get() { return createVector(y, y, y) }
    val Vector.yyz: Vector get() { return createVector(y, y, z) }
    val Vector.yzy: Vector get() { return createVector(y, z, y) }
    val Vector.yzz: Vector get() { return createVector(y, z, z) }
    val Vector.zxx: Vector get() { return createVector(z, x, x) }
    val Vector.zxz: Vector get() { return createVector(z, x, z) }
    val Vector.zyy: Vector get() { return createVector(z, y, y) }
    val Vector.zyz: Vector get() { return createVector(z, y, z) }
    val Vector.zzx: Vector get() { return createVector(z, z, x) }
    val Vector.zzy: Vector get() { return createVector(z, z, y) }
    val Vector.zzz: Vector get() { return createVector(z, z, z) }

    // Double Set
    var Vector.xy: Vector
        get() { return createVector(x, y) }
        set(other: Vector) { val newX = other.x; val newY = other.y; x = newX; y = newY }
    var Vector.xz: Vector
        get() { return createVector(x, z) }
        set(other: Vector) { val newX = other.x; val newZ = other.y; x = newX; z = newZ }
    var Vector.yx: Vector
        get() { return createVector(y, x) }
        set(other: Vector) { val newY = other.x; val newX = other.y; x = newX; y = newY }
    var Vector.yz: Vector
        get() { return createVector(y, z) }
        set(other: Vector) { val newY = other.x; val newZ = other.y; y = newY; z = newZ }
    var Vector.zx: Vector
        get() { return createVector(z, x) }
        set(other: Vector) { val newZ = other.x; val newX = other.y; x = newX; z = newZ }
    var Vector.zy: Vector
        get() { return createVector(z, y) }
        set(other: Vector) { val newZ = other.x; val newY = other.y; y = newY; z = newZ }

    // Triple Set
    var Vector.xyz: Vector
        get() { return createVector(x, y, z) }
        set(other: Vector) { val newX = other.x; val newY = other.y; val newZ = other.z; x = newX; y = newY; z = newZ }
    var Vector.xzy: Vector
        get() { return createVector(x, z, y) }
        set(other: Vector) { val newX = other.x; val newZ = other.y; val newY = other.z; x = newX; y = newY; z = newZ }
    var Vector.yxz: Vector
        get() { return createVector(y, x, z) }
        set(other: Vector) { val newY = other.x; val newX = other.y; val newZ = other.z; x = newX; y = newY; z = newZ }
    var Vector.yzx: Vector
        get() { return createVector(y, z, x) }
        set(other: Vector) { val newY = other.x; val newZ = other.y; val newX = other.z; x = newX; y = newY; z = newZ }
    var Vector.zxy: Vector
        get() { return createVector(z, x, y) }
        set(other: Vector) { val newZ = other.x; val newX = other.y; val newY = other.z; x = newX; y = newY; z = newZ }
    var Vector.zyx: Vector
        get() { return createVector(z, y, x) }
        set(other: Vector) { val newZ = other.x; val newY = other.y; val newX = other.z; x = newX; y = newY; z = newZ }

    fun Vector.toColor(): Color {
        return color(x, y, z)
    }


    operator fun Number.plus(other: Number): Number {
        val t = this
        return js("t+other") as Number
    }

    operator fun Number.minus(other: Number): Number {
        val t = this
        return js("t-other") as Number
    }

    operator fun Number.times(other: Number): Number {
        val t = this
        return js("t*other") as Number
    }

    operator fun Number.div(other: Number): Number {
        val t = this
        return js("t/other") as Number
    }

    operator fun Number.rem(other: Number): Number {
        val t = this
        return js("t%other") as Number
    }

    operator fun Number.compareTo(other: Number): Int {
        val dif = this - other
        return if (js("dif > 0") as Boolean) {
            1
        } else if (js("dif < 0") as Boolean) {
            -1
        } else 0
    }

    fun Number.pow(other: Number): Number {
        val t = this
        return js("Math.pow(t, other)") as Number
    }

    enum class DitherMode(val serpentine: Boolean = false) {
        FloydSteinberg,
        FalseFloydSteinberg,
        Stucki,
        Atkinson;

        override fun toString(): String {
            return name + if (serpentine) "-serpentine" else ""
        }
    }

    fun Color.toArray(includeAlpha: Boolean=false): Array<Number> {
        colorMode(ColorMode.RGB, 255, 1)
        return if (includeAlpha) {
            arrayOf(
                red(this), green(this), blue(this), 255*alpha(this)
            )
        } else {
            arrayOf(
                red(this), green(this), blue(this)
            )
        }
    }

    fun Color.toHexString(includeAlpha: Boolean=false): String {
        return "#${hex(toArray(includeAlpha), 2).joinToString("")}"
    }

    fun Color.toVector(): Vector {
        return createVector(red(this), green(this), blue(this))
    }

    fun Json.setIfNotNull(propertyName: String, value: Any?) {
        if (value != null) {
            set(propertyName, value)
        }
    }

    inner class Loop(val nativeLoop: dynamic) {
        fun start(renderCallback: ()->Unit = {}) {
            nativeLoop.start(renderCallback)
        }
        val progress: Number get() { return nativeLoop.progress as Number }
        val theta: Number get() { return nativeLoop.theta as Number }
        fun noise(): Number {
            return nativeLoop.noise() as Number
        }
        fun noise1D(x: Number): Number {
            return nativeLoop.noise1D(x) as Number
        }
        fun noise2D(x: Number, y: Number): Number {
            return nativeLoop.noise1D(x, y) as Number
        }
    }

    fun createLoop(
        duration: Number,
        framesPerSecond: Int? = null,
        gif: Boolean = false,
        gifRender: Boolean? = null,
        gifOpen: Boolean? = null,
        gifDownload: Boolean? = null,
        gifFileName: String? = null,
        gifStartLoop: Int? = null,
        gifEndLoop: Int? = null,
        noiseRadius: Number? = null,
        noiseSeed: Number? = null,
        gifRepeat: Int = 0,
        gifQuality: Int = 10,
        gifWorkers: Int = 2,
        gifBackground: Color = color(255, 255, 255),
        width: Int? = null,
        height: Int? = null,
        transparent: Color? = null,
        dither: DitherMode? = null,
        debug: Boolean = false,
        autoStart: Boolean = true,
        renderCallback: (()->Unit)? = null
    ): Loop {
        val options = json(
            "canvas" to _canvas,
            "gifOptions" to json(
                "repeat" to gifRepeat,
                "quality" to gifQuality,
                "workers" to gifWorkers,
                "background" to gifBackground.toHexString(),
                "width" to width,
                "height" to height,
                "transparent" to transparent?.toHexString(),
                "dither" to dither?.toString(),
                "debug" to debug
            )
        )
        with(options) {
            setIfNotNull("duration", duration)
            set("framesPerSecond", framesPerSecond ?: _targetFrameRate)
            set("gif", gif)
            setIfNotNull("gifRender", gifRender)
            setIfNotNull("gifOpen", gifOpen)
            setIfNotNull("gifDownload", gifDownload)
            setIfNotNull("gifFileName", gifFileName)
            setIfNotNull("gifStartLoop", gifStartLoop)
            setIfNotNull("gifEndLoop", gifEndLoop)
            setIfNotNull("noiseRadius", noiseRadius)
            setIfNotNull("noiseSeed", noiseSeed)
        }
        options["canvas"] = _canvas
        val nativeLoop = js("window.createLoop(options)")
        val loop = Loop(nativeLoop)
        if (autoStart) {
            loop.start(renderCallback ?: {})
        }
        return Loop(nativeLoop)
    }


    fun Button.fontSize(sizePx: Number) {
        style("font-size", "${sizePx}px")
    }

    operator fun Slider.getValue(thisRef: Any?, property: KProperty<*>): Number {
        return value()
    }

    operator fun Slider.setValue(thisRef: Any?, property: KProperty<*>, value: Number) {
        this.value(value)
    }

    operator fun ParagraphElement.getValue(thisRef: Any?, property: KProperty<*>): String {
        return html()
    }

    operator fun ParagraphElement.setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.html(value)
    }

    operator fun InputElement.getValue(thisRef: Any?, property: KProperty<*>): String {
        return value()
    }

    operator fun InputElement.setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.value(value)
    }


    fun interpolate(vl: Vector?=null, v0: Vector, v1: Vector, vr: Vector?=null, x: Number): Number {

        val scale = 1/(v1.x - v0.x)

        val slope0 = if(vl != null) (v1.y - vl.y)/(1-scale*(vl.x - v0.x)) else 0
        val slope1 = if(vr != null) (vr.y - v0.y)/(scale*(vr.x - v0.x)) else 0

        val xi = scale*(x - v0.x)

        val a = 2*v0.y - 2*v1.y + slope0 + slope1
        val b = -3*v0.y + 3*v1.y - 2*slope0 - slope1

        return ((a*xi + b)*xi + slope0)*xi + v0.y

    }

    fun List<Vector>.interpolate(x: Number): Number {

        if (isEmpty()) return 0
        if (size == 1) return this[0].y
        if (x <= this[0].x) return this[0].y
        if (x >= last().x) return last().y
        if (size == 2) return interpolate(v0=this[0], v1=this[1], x=x)
        if (x <= this[1].x) return interpolate(v0=this[0], v1=this[1], vr=this[2], x=x)
        if (x >= this[lastIndex-1].x) return interpolate(vl=this[lastIndex-2], v0=this[lastIndex-1], v1=this[lastIndex], x=x)

        for(it in windowed(4)) {
            if (it[1].x <= x && x <= it[2].x) return interpolate(vl=it[0], v0=it[1], v1=it[2], vr=it[3], x=x)
        }

        return 0.0
    }

    fun Map<Number, Vector>.interpolate(k: Number): Vector {

        val interpX = map {
            createVector(it.key, it.value.x)
        }.sortedBy { it.x.toDouble() }.interpolate(k)

        val interpY = map {
            createVector(it.key, it.value.y)
        }.sortedBy { it.x.toDouble() }.interpolate(k)

        val interpZ = map {
            createVector(it.key, it.value.z)
        }.sortedBy { it.x.toDouble() }.interpolate(k)

        return createVector(interpX, interpY, interpZ)

    }

    enum class TableMode(val nativeValue: String) {
        TSV("tsv"),
        CSV("csv"),
        HTML("html")
    }

    fun saveTable(table: Table, filename: String, extension: TableMode) {
        _saveTable(table, filename, extension.nativeValue)
    }

    fun loadTable(filename: String, extension: TableMode): Table {
        return _loadTable(filename, extension.nativeValue)
    }
    fun loadTable(filename: String, extension: TableMode, hasHeaders: Boolean): Table {
        return _loadTable(filename, extension.nativeValue, if(hasHeaders) "header" else "")
    }
    fun loadTable(filename: String, extension: TableMode, hasHeaders: Boolean, callback: (Table)->Unit): Table {
        return _loadTable(filename, extension.nativeValue, if(hasHeaders) "header" else "", callback)
    }
    fun loadTable(filename: String, extension: TableMode, hasHeaders: Boolean, callback: (Table)->Unit, errorCallback: (dynamic)->Unit): Table {
        return _loadTable(filename, extension.nativeValue, if(hasHeaders) "header" else "", callback, errorCallback)
    }

    fun fractalNoise(x: Number, y: Number, octaves: List<Number>): Number {
        return octaves.map { noise(x*(2.pow(it)), y*(2.0.pow(it))).toDouble() }.average()
    }

    fun fractalNoise(x: Number, y: Number, octaves: List<Pair<Number, Number>>): Number {
        val weightSum = octaves.sumOf { it.second.toDouble() }
        return octaves.map { (noise(x*(2.pow(it.first)), y*(2.0.pow(it.first)))*it.second).toDouble() }.sum()/weightSum
    }


}

