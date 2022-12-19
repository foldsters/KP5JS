@file:Suppress("unused", "UnsafeCastFromDynamic", "UNUSED_PARAMETER", "UNCHECKED_CAST", "UNUSED_VARIABLE", "UNSUPPORTED_FEATURE")

package p5.core

import kotlinx.browser.document
import kotlinx.serialization.*
import kotlinx.serialization.json.Json as SerialJson
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import p5.Sketch
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.createLoop.nativeCreateLoop
import p5.ksl.*
import p5.native.*
import p5.native.NativeP5.*
import kotlin.js.Json
import kotlin.reflect.KProperty
import p5.native.openSimplexNoise.OpenSimplexNoise
import p5.util.*
import kotlin.js.json
import kotlin.reflect.KClass
import kotlin.math.*
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@JsModule("p5")
@JsNonModule
@JsName("p5")
private external val p5: dynamic

private var P5UUID = 0

class P5(var nativeP5: NativeP5) {
    constructor() : this(NativeP5())

    fun NativeElement.toElement() = Element(this)

    val instanceId = P5UUID++

    // ENVIRONMENT

    // Accessibility
    fun describe(text: String) = nativeP5.describe(text)
    fun describe(text: String, display: DescriptionMode) = nativeP5.describe(text, display.nativeValue)
    fun textOutput() = nativeP5.textOutput()
    fun gridOutput() = nativeP5.gridOutput()
    fun describeElement(name: String, text: String, display: DescriptionMode) =
        nativeP5.describeElement(name, text, display.nativeValue)

    fun textOutput(display: DescriptionMode) = nativeP5.textOutput(display.nativeValue)
    fun gridOutput(display: DescriptionMode) = nativeP5.gridOutput(display.nativeValue)

    // Window
    var frameCount by nativeP5::frameCount
    val deltaTime by nativeP5::deltaTime
    val focused by nativeP5::focused
    val targetFrameRate by nativeP5::_targetFrameRate
    val displayWidth by nativeP5::displayWidth
    val displayHeight by nativeP5::displayHeight
    val windowWidth by nativeP5::windowWidth
    val windowHeight by nativeP5::windowHeight
    var windowResized by nativeP5::windowResized
    val height by nativeP5::height
    val width by nativeP5::width

    fun cursor(type: String) = nativeP5.cursor(type)
    fun cursor(type: String, x: Int, y: Int) = nativeP5.cursor(type, x, y)
    fun frameRate(fps: Number) = nativeP5.frameRate(fps)
    fun frameRate(): Double = nativeP5.frameRate()
    fun noCursor() = nativeP5.noCursor()
    fun fullscreen(value: Boolean) = nativeP5.fullscreen(value)
    fun fullscreen(): Boolean = nativeP5.fullscreen()
    fun pixelDensity(value: Int) = nativeP5.pixelDensity(value)
    fun pixelDensity(): Int = nativeP5.pixelDensity()
    fun displayDensity(): Int = nativeP5.displayDensity()
    fun getURL(): String = nativeP5.getURL()
    fun getURLPath(): Array<String> = nativeP5.getURLPath()
    fun getURLParam(key: String): String? = nativeP5.getURLParams()[key]

    // COLOR

    // Creating and Rendering
    fun color(gray: Number): Color = Color(nativeP5.color(gray))
    fun color(gray: Number, alpha: Number): Color = Color(nativeP5.color(gray, alpha))
    fun color(v1: Number, v2: Number, v3: Number): Color = Color(nativeP5.color(v1, v2, v3))
    fun color(v1: Number, v2: Number, v3: Number, alpha: Number): Color = Color(nativeP5.color(v1, v2, v3, alpha))
    fun color(colorString: String): Color = Color(nativeP5.color(colorString))
    fun color(colorArray: Array<Number>): Color = Color(nativeP5.color(colorArray))

    fun alpha(color: Color): Double = nativeP5.alpha(color.nativeColor)
    fun blue(color: Color): Double = nativeP5.blue(color.nativeColor)
    fun brightness(color: Color): Double = nativeP5.brightness(color.nativeColor)
    fun green(color: Color): Double = nativeP5.green(color.nativeColor)
    fun hue(color: Color): Double = nativeP5.hue(color.nativeColor)
    fun lerpColor(c1: Color, c2: Color, amt: Double): Color =
        Color(nativeP5.lerpColor(c1.nativeColor, c2.nativeColor, amt))

    fun lightness(color: Color): Double = nativeP5.lightness(color.nativeColor)
    fun red(color: Color): Double = nativeP5.red(color.nativeColor)
    fun saturation(color: Color): Double = nativeP5.saturation(color.nativeColor)

    // Setting
    fun background(gray: Number) = nativeP5.background(gray)
    fun background(gray: Number, alpha: Number) = nativeP5.background(gray, alpha)
    fun background(colorString: String) = nativeP5.background(colorString)
    fun background(colorString: String, alpha: Number) = nativeP5.background(colorString, alpha)
    fun background(v1: Number, v2: Number, v3: Number) = nativeP5.background(v1, v2, v3)
    fun background(v1: Number, v2: Number, v3: Number, alpha: Number) = nativeP5.background(v1, v2, v3, alpha)
    fun background(colorArray: Array<Number>) = nativeP5.background(colorArray)
    fun background(color: Color) = nativeP5.background(color.nativeColor)
    fun clear() = nativeP5.clear()
    fun colorMode(mode: ColorMode) = nativeP5.colorMode(mode.nativeValue)
    fun colorMode(mode: ColorMode, max: Number) = nativeP5.colorMode(mode.nativeValue, max)
    fun colorMode(mode: ColorMode, max1: Number, maxA: Number) = nativeP5.colorMode(mode.nativeValue, max1, maxA)
    fun colorMode(mode: ColorMode, max1: Number, max2: Number, max3: Number) =
        nativeP5.colorMode(mode.nativeValue, max1, max2, max3)

    fun colorMode(mode: ColorMode, max1: Number, max2: Number, max3: Number, maxA: Number) =
        nativeP5.colorMode(mode.nativeValue, max1, max2, max3, maxA)

    // fill: modified below
    fun noFill() = nativeP5.noFill()
    fun noStroke() = nativeP5.noStroke()
    fun stroke(gray: Number) = nativeP5.stroke(gray)
    fun stroke(gray: Number, alpha: Number) = nativeP5.stroke(gray, alpha)
    fun stroke(v1: Number, v2: Number, v3: Number) = nativeP5.stroke(v1, v2, v3)
    fun stroke(v1: Number, v2: Number, v3: Number, alpha: Number) = nativeP5.stroke(v1, v2, v3, alpha)
    fun stroke(colorString: String) = nativeP5.stroke(colorString)
    fun stroke(colorArray: Array<Number>) = nativeP5.stroke(colorArray)
    fun stroke(color: Color) = nativeP5.stroke(color.nativeColor)
    fun erase() = nativeP5.erase()
    fun erase(strengthFill: Number) = nativeP5.erase(strengthFill)
    fun erase(strengthFill: Number, strengthStroke: Number) = nativeP5.erase(strengthFill, strengthStroke)
    fun noErase() = nativeP5.noErase()

    // SHAPE
    fun arc(x: Number, y: Number, width: Number, height: Number, startRad: Number, stopRad: Number) {
        return nativeP5.arc(x, y, width, height, startRad, stopRad)
    }

    fun arc(xy: Vector, width: Number, height: Number, startRad: Number, stopRad: Number) {
        return nativeP5.arc(xy.x, xy.y, width, height, startRad, stopRad)
    }

    fun arc(xy: Vector, wh: Vector, startRad: Number, stopRad: Number) {
        return nativeP5.arc(xy.x, xy.y, wh.x, wh.y, startRad, stopRad)
    }

    fun arc(x: Number, y: Number, width: Number, height: Number, startRad: Number, stopRad: Number, mode: ArcMode) {
        return nativeP5.arc(x, y, width, height, startRad, stopRad, mode.nativeValue)
    }

    fun arc(
        x: Number,
        y: Number,
        width: Number,
        height: Number,
        startRad: Number,
        stopRad: Number,
        mode: ArcMode,
        detail: Int
    ) {
        return nativeP5.arc(x, y, width, height, startRad, stopRad, mode.nativeValue, detail)
    }

    fun arc(xy: Vector, width: Number, height: Number, startRad: Number, stopRad: Number, mode: ArcMode) {
        return nativeP5.arc(xy.x, xy.y, width, height, startRad, stopRad, mode.nativeValue)
    }

    fun arc(xy: Vector, width: Number, height: Number, startRad: Number, stopRad: Number, mode: ArcMode, detail: Int) {
        return nativeP5.arc(xy.x, xy.y, width, height, startRad, stopRad, mode.nativeValue, detail)
    }

    fun arc(xy: Vector, wh: Vector, startRad: Number, stopRad: Number, mode: ArcMode) {
        return nativeP5.arc(xy.x, xy.y, wh.x, wh.y, startRad, stopRad, mode.nativeValue)
    }

    fun arc(xy: Vector, wh: Vector, startRad: Number, stopRad: Number, mode: ArcMode, detail: Int) {
        return nativeP5.arc(xy.x, xy.y, wh.x, wh.y, startRad, stopRad, mode.nativeValue, detail)
    }

    fun ellipse(x: Number, y: Number, width: Number) = nativeP5.ellipse(x, y, width)
    fun ellipse(xy: Vector, width: Number) = nativeP5.ellipse(xy.x, xy.y, width)
    fun ellipse(x: Number, y: Number, width: Number, height: Number) = nativeP5.ellipse(x, y, width, height)
    fun ellipse(xy: Vector, width: Number, height: Number) = nativeP5.ellipse(xy.x, xy.y, width, height)
    fun ellipse(xy: Vector, wh: Vector) = nativeP5.ellipse(xy.x, xy.y, wh.x, wh.y)
    fun ellipse(x: Number, y: Number, width: Number, height: Number, detail: Int) =
        nativeP5.ellipse(x, y, width, height, detail)

    fun ellipse(xy: Vector, width: Number, height: Number, detail: Int) =
        nativeP5.ellipse(xy.x, xy.y, width, height, detail)

    fun ellipse(xy: Vector, wh: Vector, detail: Int) = nativeP5.ellipse(xy.x, xy.y, wh.x, wh.y, detail)

    fun circle(x: Number, y: Number, d: Number) = nativeP5.circle(x, y, d)
    fun circle(xy: Vector, d: Number) = nativeP5.circle(xy.x, xy.y, d)

    fun line(x1: Number, y1: Number, x2: Number, y2: Number) =
        nativeP5.line(x1, y1, x2, y2) // TODO: Add canvas type switch

    fun line2D(v1: Vector, v2: Vector) = nativeP5.line(v1.x, v1.y, v2.x, v2.y)
    fun line(x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number) =
        nativeP5.line(x1, y1, z1, x2, y2, z2)

    fun line3D(v1: Vector, v2: Vector) = nativeP5.line(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z)

    fun line(v1: Vector, v2: Vector) {
        when(getCanvas()) {
            is Renderer2D -> line2D(v1, v2)
            is RendererGL -> line3D(v1, v2)
        }
    }

    fun point(x: Number, y: Number) = nativeP5.point(x, y)
    fun point(x: Number, y: Number, z: Number) = nativeP5.point(x, y, z)
    fun point(coordinate_vector: Vector) = nativeP5.point(coordinate_vector.nativeVector)

    fun quad(
        x1: Number, y1: Number, x2: Number, y2: Number,
        x3: Number, y3: Number, x4: Number, y4: Number
    ) = nativeP5.quad(x1, y1, x2, y2, x3, y3, x4, y4)

    fun quad(
        x1: Number, y1: Number, x2: Number, y2: Number,
        x3: Number, y3: Number, x4: Number, y4: Number, detailX: Int, detailY: Int
    ) = nativeP5.quad(x1, y1, x2, y2, x3, y3, x4, y4, detailX, detailY)

    fun quad(
        x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number,
        x3: Number, y3: Number, z3: Number, x4: Number, y4: Number, z4: Number
    ) = nativeP5.quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4)

    fun quad(
        x1: Number, y1: Number, z1: Number, x2: Number, y2: Number, z2: Number,
        x3: Number, y3: Number, z3: Number, x4: Number, y4: Number, z4: Number, detailX: Int, detailY: Int
    ) = nativeP5.quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, detailX, detailY)

    fun quad(v1: Vector, v2: Vector, v3: Vector, v4: Vector, xyz: Boolean = false) { // TODO: Add Canvas Switch
        if (xyz) {
            nativeP5.quad(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z, v4.x, v4.y, v4.z)
        } else {
            nativeP5.quad(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y)
        }
    }

    fun quad(v1: Vector, v2: Vector, v3: Vector, v4: Vector, detailX: Int, detailY: Int, xyz: Boolean = false) {
        if (xyz) {
            nativeP5.quad(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z, v4.x, v4.y, v4.z, detailX, detailY)
        } else {
            nativeP5.quad(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y, detailX, detailY)
        }
    }

    fun rect(x: Number, y: Number, width: Number) = nativeP5.rect(x, y, width)
    fun rect(xy: Vector, width: Number) = nativeP5.rect(xy.x, xy.y, width)
    fun rect(x: Number, y: Number, width: Number, height: Number) = nativeP5.rect(x, y, width, height)
    fun rect(xy: Vector, width: Number, height: Number) = nativeP5.rect(xy.x, xy.y, width, height)
    fun rect(xy: Vector, wh: Vector) = nativeP5.rect(xy.x, xy.y, wh.x, wh.y)
    fun rect(x: Number, y: Number, width: Number, height: Number, tl: Number, tr: Number, br: Number, bl: Number) =
        nativeP5.rect(x, y, width, height, tl, tr, br, bl)

    fun rect(xy: Vector, width: Number, height: Number, tl: Number, tr: Number, br: Number, bl: Number) =
        nativeP5.rect(xy.x, xy.y, width, height, tl, tr, br, bl)

    fun rect(xy: Vector, wh: Vector, tl: Number, tr: Number, br: Number, bl: Number) =
        nativeP5.rect(xy.x, xy.y, wh.x, wh.y, tl, tr, br, bl)

    fun rect(x: Number, y: Number, width: Number, detailX: Int, detailY: Int) =
        nativeP5.rect(x, y, width, detailX, detailY)

    fun rect(xy: Vector, width: Number, detailX: Int, detailY: Int) = nativeP5.rect(xy.x, xy.y, width, detailX, detailY)

    fun square(x: Number, y: Number, size: Number) = nativeP5.square(x, y, size)
    fun square(xy: Vector, size: Number) = nativeP5.square(xy.x, xy.y, size)
    fun square(x: Number, y: Number, size: Number, tl: Number, tr: Number, br: Number, bl: Number) =
        nativeP5.square(x, y, size, tl, tr, br, bl)

    fun square(xy: Vector, size: Number, tl: Number, tr: Number, br: Number, bl: Number) =
        nativeP5.square(xy.x, xy.y, size, tl, tr, br, bl)

    fun square(x: Number, y: Number, size: Number, detailX: Int, detailY: Int) =
        nativeP5.square(x, y, size, detailX, detailY)

    fun square(xy: Vector, size: Number, detailX: Int, detailY: Int) =
        nativeP5.square(xy.x, xy.y, size, detailX, detailY)

    fun triangle(x1: Number, y1: Number, x2: Number, y2: Number, x3: Number, y3: Number) =
        nativeP5.triangle(x1, y1, x2, y2, x3, y3)

    fun triangle(v1: Vector, v2: Vector, v3: Vector) = nativeP5.triangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y)

    fun ellipseMode(mode: CenterMode) = nativeP5.ellipseMode(mode.nativeValue)
    fun rectMode(mode: CenterMode) = nativeP5.rectMode(mode.nativeValue)
    fun strokeCap(mode: CapMode) = nativeP5.strokeCap(mode.nativeValue)
    fun strokeJoin(mode: JoinMode) = nativeP5.strokeJoin(mode.nativeValue)
    fun strokeWeight(weight: Number) = nativeP5.strokeWeight(weight)
    fun smooth() = nativeP5.smooth()
    fun noSmooth() = nativeP5.noSmooth()

    inner class ShapeBuilder2D {
        fun addVertex(x: Number, y: Number) = nativeP5.vertex(x, y)
        fun addVertex(xy: Vector) = nativeP5.vertex(xy.x, xy.y)
        fun addVertex(x: Number, y: Number, u: Number, v: Number) = nativeP5.vertex(x, y, u, v)
        fun addVertex(xy: Vector, uv: Vector) = nativeP5.vertex(xy.x, xy.y, uv.x, uv.y)
        fun addQuadraticVertex(cx: Number, cy: Number, x3: Number, y3: Number) =
            nativeP5.quadraticVertex(cx, cy, x3, y3)

        fun addQuadraticVertex(cxy: Vector, xy3: Vector) = nativeP5.quadraticVertex(cxy.x, cxy.y, xy3.x, xy3.y)
        fun addBezierVertex(x2: Number, y2: Number, x3: Number, y3: Number, x4: Number, y4: Number) =
            nativeP5.bezierVertex(x2, y2, x3, y3, x4, y4)

        fun addBezierVertex(xy2: Vector, xy3: Vector, xy4: Vector) =
            nativeP5.bezierVertex(xy2.x, xy2.y, xy3.x, xy3.y, xy4.x, xy4.y)

        fun addCurveVertex(x: Number, y: Number) = nativeP5.curveVertex(x, y)
        fun addCurveVertex(xy: Vector) = nativeP5.curveVertex(xy.x, xy.y)
        fun List<Vector>.addVertices() {
            forEach { addVertex(it) }
        }

        fun addVertices(vararg vertices: Vector) {
            vertices.forEach { addVertex(it) }
        }

        fun withContour(contour: ShapeBuilder2D.() -> Unit) {
            nativeP5.beginContour()
            contour()
            nativeP5.endContour()
        }
    }

    inner class ShapeBuilder3D {
        fun vertex(x: Number, y: Number, z: Number) = nativeP5.vertex(x, y, z)
        fun vertex(xyz: Vector) = nativeP5.vertex(xyz.x, xyz.y, xyz.z)
        fun vertex(x: Number, y: Number, z: Number, u: Number, v: Number) = nativeP5.vertex(x, y, z, u, v)
        fun vertex(xyz: Vector, uv: Vector) = nativeP5.vertex(xyz.x, xyz.y, xyz.z, uv.x, uv.y)
        fun quadraticVertex(cx: Number, cy: Number, cz: Number, x3: Number, y3: Number, z3: Number) =
            nativeP5.quadraticVertex(cx, cy, cz, x3, y3, z3)

        fun quadraticVertex(cxyz: Vector, xyz3: Vector) =
            nativeP5.quadraticVertex(cxyz.x, cxyz.y, cxyz.z, xyz3.x, xyz3.y, xyz3.z)

        fun bezierVertex(
            x2: Number, y2: Number, z2: Number,
            x3: Number, y3: Number, z3: Number,
            x4: Number, y4: Number, z4: Number
        ) = nativeP5.bezierVertex(x2, y2, z2, x3, y3, z3, x4, y4, z4)

        fun bezierVertex(xyz2: Vector, xyz3: Vector, xyz4: Vector) =
            nativeP5.bezierVertex(xyz2.x, xyz2.y, xyz2.z, xyz3.x, xyz3.y, xyz3.z, xyz4.z, xyz4.y, xyz4.z)

        fun curveVertex(x: Number, y: Number, z: Number) = nativeP5.curveVertex(x, y, z)
        fun curveVertex(xyz: Vector) = nativeP5.curveVertex(xyz.x, xyz.y, xyz.z)
        fun normal(vector: Vector) = nativeP5.normal(vector.nativeVector)
        fun normal(x: Number, y: Number, z: Number) = nativeP5.normal(x, y, z)

        fun withContour(contour: () -> Unit) {
            nativeP5.beginContour()
            contour()
            nativeP5.endContour()
        }
    }

    // STRUCTURE
    var preload: () -> Unit by nativeP5::preload
    var draw: () -> Unit by nativeP5::draw
    var setup: () -> Unit by nativeP5::setup
    var disableFriendlyErrors: Boolean by nativeP5::disableFriendlyErrors
    fun remove() = nativeP5.remove()
    fun noLoop() = nativeP5.noLoop()
    fun loop() = nativeP5.loop()
    fun isLooping(): Boolean = nativeP5.isLooping()
    fun push() = nativeP5.push()
    fun pop() = nativeP5.pop()
    fun redraw() {
        isRedrawing = true
        nativeP5.redraw()
    }
    fun redraw(n: Int) = nativeP5.redraw(n)

    var isRedrawing: Boolean = false

    fun select(selectors: String): Element? = nativeP5.select(selectors)?.toElement()
    fun select(selectors: String, containerString: String): Element? =
        nativeP5.select(selectors, containerString)?.toElement()

    fun select(selectors: String, containerElement: Element): Element? =
        nativeP5.select(selectors, containerElement.nativeElement)?.toElement()

    fun selectAll(selectors: String): Array<Element> = nativeP5.selectAll(selectors).arrayMap(::Element)
    fun selectAll(selectors: String, containerString: String): Array<Element> =
        nativeP5.selectAll(selectors, containerString).arrayMap(::Element)

    fun selectAll(selectors: String, containerElement: Element): Array<Element> =
        nativeP5.selectAll(selectors, containerElement.nativeElement).arrayMap(::Element)

    fun removeElements() = nativeP5.removeElements()
    fun createDiv(htmlString: String): Div = Div(nativeP5.createDiv(htmlString))
    fun createP(htmlString: String): Paragraph = Paragraph(nativeP5.createP(htmlString))
    fun createSpan(htmlString: String): Element = Element(nativeP5.createSpan(htmlString))
    fun createImg(srcPath: String, altText: String): Element = Element(nativeP5.createImg(srcPath, altText))
    fun createImg(srcPath: String, altText: String, crossOrigin: CrossOriginMode): Element =
        Element(nativeP5.createImg(srcPath, altText, crossOrigin.nativeValue))

    fun createImg(
        srcPath: String,
        altText: String,
        crossOrigin: CrossOriginMode,
        loadedCallback: (Element) -> Unit
    ): Element =
        Element(nativeP5.createImg(srcPath, altText, crossOrigin.nativeValue) { loadedCallback(Element(it)) })

    fun createImg(srcPath: String, altText: String, loadedCallback: (Element) -> Unit): Element =
        Element(nativeP5.createImg(srcPath, altText, CrossOriginMode.NONE.nativeValue) { loadedCallback(Element(it)) })

    fun createA(href: String, html: String): Element = Element(nativeP5.createA(href, html))
    fun createA(href: String, html: String, target: TargetMode): Element =
        Element(nativeP5.createA(href, html, target.nativeValue))

    fun createSlider(min: Number, max: Number): Slider = Slider(nativeP5.createSlider(min, max), min, max, null)
    fun createSlider(min: Number, max: Number, value: Number): Slider = Slider(nativeP5.createSlider(min, max, value), min, max, null)
    fun createSlider(min: Number, max: Number, value: Number, step: Number): Slider =
        Slider(nativeP5.createSlider(min, max, value, step), min, max, step)

    fun createButton(label: String): Button = Button(nativeP5.createButton(label))
    fun createCheckbox(): Checkbox = Checkbox(nativeP5.createCheckbox())
    fun createCheckbox(label: String): Checkbox = Checkbox(nativeP5.createCheckbox(label))
    fun createCheckbox(label: String, checked: Boolean): Checkbox = Checkbox(nativeP5.createCheckbox(label, checked))
    fun createSelect(): Select = Select(nativeP5.createSelect())
    fun createSelect(multiple: Boolean): Select = Select(nativeP5.createSelect(multiple))
    fun createRadio(): Radio = Radio(nativeP5.createRadio())
    fun createRadio(name: String): Radio = Radio(nativeP5.createRadio(name))
    fun createColorPicker(): ColorPicker = ColorPicker(nativeP5.createColorPicker())
    fun createColorPicker(default: String): ColorPicker = ColorPicker(nativeP5.createColorPicker(default))
    fun createColorPicker(default: Color): ColorPicker = ColorPicker(nativeP5.createColorPicker(default.nativeColor))
    fun createInput(): Input = Input(nativeP5.createInput())
    fun createInput(default: String): Input = Input(nativeP5.createInput(default))
    fun createInput(default: String, type: InputMode): Input = Input(nativeP5.createInput(default, type.nativeValue))
    fun createFileInput(callback: (File) -> Unit): Element = Element(nativeP5.createFileInput { callback(File(it)) })
    fun createFileInput(multiple: Boolean, callback: (File) -> Unit): Element =
        Element(nativeP5.createFileInput({ callback(File(it)) }, multiple))

    fun createVideo(src: String): MediaElement = MediaElement(nativeP5.createVideo(src))
    fun createVideo(src: String, callback: () -> Unit): MediaElement = MediaElement(nativeP5.createVideo(src, callback))
    fun createVideo(srcs: Array<String>): MediaElement = MediaElement(nativeP5.createVideo(srcs))
    fun createVideo(srcs: Array<String>, callback: () -> Unit): MediaElement =
        MediaElement(nativeP5.createVideo(srcs, callback))

    fun createCapture(type: CaptureMode): Element = Element(nativeP5.createCapture(type.nativeValue))
    fun createCapture(type: CaptureMode, callback: (dynamic) -> Unit): Element =
        Element(nativeP5.createCapture(type.nativeValue, callback)) // TWhat is callback parameter?

    fun createElement(tag: String): Element = Element(nativeP5.createElement(tag))
    fun createElement(tag: String, content: String): Element = Element(nativeP5.createElement(tag, content))

    sealed class Renderer(nativeRenderer: NativeElement): Element(nativeRenderer) {
        val gl: dynamic get() = nativeElement.asDynamic().GL
    }
    inner class Renderer2D(val nativeRenderer2D: NativeRenderer2D): Renderer(nativeRenderer2D)

    open inner class CanvasElement(val pointInRegion: (Vector)->Boolean) {

        private var parent: Interactable = getCanvas()
        private val children = mutableListOf<Interactable>()
        fun parent(): Interactable = parent
        fun parent(newParent: Interactable) {
            parent = newParent
        }
        fun children(): List<Interactable> = children.toList()

        private var wasMouseOver: Boolean = false
        var isMouseOver: Boolean = false
        var isClicked: Boolean = false
        var isTouched: Boolean = false
        var isDraggedOver: Boolean = false

        var handleMouseOver: Boolean = false
        var handleMouseOut: Boolean = false

        init {
            parent.mouseMoved {
                wasMouseOver = isMouseOver
                isMouseOver = pointInRegion(mouse)
                if(!wasMouseOver && isMouseOver) handleMouseOver = true
                if(wasMouseOver && !isMouseOver) handleMouseOut = true
            }
            parent.mousePressed {
                isClicked = parent.isMouseOver
            }
            parent.mouseReleased {
                isClicked = false
            }
            parent.mouseOut {
                wasMouseOver = isMouseOver
                isMouseOver = false
                handleMouseOut = true
            }
        }

        fun drop(callback: CanvasElement.(File)->Unit) = parent.drop {
            if(isMouseOver) { callback(it) }
        }
        fun drop(callback: CanvasElement.(File)->Unit, onDrop: ()->Unit) = parent.drop(
            { if(isMouseOver) { callback(it) } },
            { if(isMouseOver) { onDrop() } })
        fun mousePressed(callback: CanvasElement.(Unit)->Unit): EventAction<Unit> = parent.mousePressed {
            if(isMouseOver) { callback(it) }
        }
        fun doubleClicked(callback: CanvasElement.(MouseEvent)->Unit): EventAction<MouseEvent> = parent.doubleClicked {
            if(isMouseOver) { callback(it) }
        }
        fun mouseClicked(callback: CanvasElement.(PointerEvent)->Unit): EventAction<PointerEvent> = parent.mouseClicked {
            if(isMouseOver) { callback(it) }
        }
        fun mouseReleased(callback: CanvasElement.(MouseEvent)->Unit): EventAction<MouseEvent> = parent.mouseReleased {
            callback(it)
        }
        fun mouseMoved(callback: CanvasElement.(MouseEvent)->Unit): EventAction<MouseEvent> = parent.mouseMoved {
            if(isMouseOver) { callback(it) }
        }
        fun mouseOver(callback: CanvasElement.(MouseEvent)->Unit): EventAction<MouseEvent> = parent.mouseMoved {
            if(handleMouseOver) {
                callback(it)
                handleMouseOver = false
            }
        }
        fun mouseOut(callback: CanvasElement.(MouseEvent)->Unit): EventAction<MouseEvent> = parent.mouseMoved {
            if(handleMouseOut) {
                callback(it)
                handleMouseOut = false
            }
        }
        fun mouseWheel(callback: CanvasElement.(WheelEvent)->Unit): EventAction<WheelEvent> = parent.mouseWheel {
            if(isMouseOver) { callback(it) }
        }

//            // Smarter Event Handler
//            private class ActionHandler<D: Any> {
//                val actions = mutableListOf<EventAction<D>>()
//                fun addEvent(callback: (D)->Unit): EventAction<D> {
//                    val action = EventAction(callback)
//                    actions.add(action)
//                    action.remove = {
//                        action.remove = null
//                        actions.remove(action)
//                    }
//                    return action
//                }
//                fun clear() {
//                    actions.forEach { it.remove = null }
//                    actions.clear()
//                }
//                fun trigger(eventData: D) = actions.forEach {
//                    it.callback(eventData)
//                }
//            }

//            private val doubleClickedHandler = ActionHandler<MouseEvent>()
//            private val mouseReleasedHandler = ActionHandler<MouseEvent>()
//            private val mouseWheelHandler    = ActionHandler<WheelEvent>()
//            private val mouseClickedHandler  = ActionHandler<PointerEvent>()
//            private val mouseMovedHandler    = ActionHandler<MouseEvent>()
//            private val mouseOverHandler     = ActionHandler<MouseEvent>()
//            private val mouseOutHandler      = ActionHandler<MouseEvent>()
//            private val touchStartedHandler  = ActionHandler<dynamic>()
//            private val touchMovedHandler    = ActionHandler<dynamic>()
//            private val touchEndedHandler    = ActionHandler<dynamic>()
//            private val dragOverHandler      = ActionHandler<DragEvent>()
//            private val dragLeaveHandler     = ActionHandler<DragEvent>()
//            private val changedHandler       = ActionHandler<dynamic>()
//            private val inputHandler         = ActionHandler<dynamic>()

//            override fun touchStarted(callback: (dynamic)->Unit): EventAction<dynamic> = touchStartedHandler.addEvent(callback)
//            override fun touchMoved(callback: (dynamic)->Unit): EventAction<dynamic> = touchMovedHandler.addEvent(callback)
//            override fun touchEnded(callback: (dynamic)->Unit): EventAction<dynamic> = touchEndedHandler.addEvent(callback)
//            override fun dragOver(callback: (DragEvent)->Unit): EventAction<DragEvent> = dragOverHandler.addEvent(callback)
//            override fun dragLeave(callback: (DragEvent)->Unit): EventAction<DragEvent> = dragLeaveHandler.addEvent(callback)
//            override fun changed(callback: (Event)->Unit): EventAction<dynamic> = changedHandler.addEvent(callback)
//            override fun input(callback: (Event)->Unit): EventAction<dynamic> = inputHandler.addEvent(callback)
//
//            override fun clearMousePressed() = mousePressedHandler.clear()
//            override fun clearDoubleClicked() = doubleClickedHandler.clear()
//            override fun clearMouseReleased() = mouseReleasedHandler.clear()
//            override fun clearMouseWheel() = mouseWheelHandler.clear()
//            override fun clearMouseClicked() = mouseClickedHandler.clear()
//            override fun clearMouseMoved() = mouseMovedHandler.clear()
//            override fun clearMouseOver() = mouseOverHandler.clear()
//            override fun clearMouseOut() = mouseOutHandler.clear()
//            override fun clearTouchStarted() = touchStartedHandler.clear()
//            override fun clearTouchMoved() = touchMovedHandler.clear()
//            override fun clearTouchEnded() = touchEndedHandler.clear()
//            override fun clearDragOver() = dragOverHandler.clear()
//            override fun clearDragLeave() = dragLeaveHandler.clear()
//            override fun clearChanged() = changedHandler.clear()
//            override fun clearInput() = inputHandler.clear()


        //
//            fun mouseOverDelay(delayMillis: Number, block: () -> Unit): EventAction<MouseEvent> {
//                return mouseOver {
//                    setTimeout(delayMillis) {
//                        if(isMouseOver) {
//                            block()
//                        }
//                    }
//                }
//            }
//
        var name: String? = null
    }

    class RendererGL(val nativeRendererGl: NativeRendererGL) : Renderer(nativeRendererGl)

    private var canvas: Renderer? = null

    fun getCanvas(): Renderer = canvas ?: error("canvas has not been initialized yet")
    fun createCanvas(w: Number, h: Number): Renderer2D = Renderer2D(nativeP5.createCanvas(w, h)).also { canvas = it }
    fun createCanvas(wh: Vector): Renderer = createCanvas(wh.x, wh.y)
    fun createCanvas(w: Number, h: Number, renderMode: RenderMode): Renderer = when (renderMode) {
        RenderMode.P2D -> {
            val nativeCanvas = nativeP5.createCanvas(w, h, renderMode.nativeValue)
            val result = Renderer2D(nativeCanvas as NativeRenderer2D)
            canvas = result
            result
        }
        RenderMode.WEBGL -> {
            val nativeCanvas = nativeP5.createCanvas(w, h, renderMode.nativeValue)
            val result = RendererGL(nativeCanvas as NativeRendererGL).apply {
                setAttributes(RenderAttribute.ALPHA, true)
            }
            canvas = result
            result
        }
        RenderMode.WEBGL2 -> {
            val previousCanvasElement = nativeP5.canvasHtml
            enableWebgl2()
            val nativeCanvas = nativeP5.createCanvas(w, h, renderMode.nativeValue)
            val result = RendererGL(nativeCanvas as NativeRendererGL).apply {
                setAttributes(RenderAttribute.ALPHA, true)
            }
            document.getElementById(previousCanvasElement.id).let { it?.parentNode?.removeChild(it) }
            canvas = result
            result
        }
    }

    fun createCanvas(wh: Vector, renderMode: RenderMode): Renderer = createCanvas(wh.x, wh.y, renderMode)
    fun createCanvas(renderMode: RenderMode): Renderer = createCanvas(0, 0, renderMode)
    fun createCanvas(): Renderer = createCanvas(0, 0)
    fun resizeCanvas(w: Number, h: Number) {
        if(w != width || h != height) {
            nativeP5.resizeCanvas(w, h)
        }
    }
    fun resizeCanvas(wh: Vector) = nativeP5.resizeCanvas(wh.x, wh.y)
    fun resizeCanvas(w: Number, h: Number, noRedraw: Boolean) = nativeP5.resizeCanvas(w, h, noRedraw)
    fun resizeCanvas(wh: Vector, noRedraw: Boolean) = nativeP5.resizeCanvas(wh.x, wh.y, noRedraw)
    fun noCanvas() = nativeP5.noCanvas()
    fun createGraphics(w: Number, h: Number, hide: Boolean = true): P5 = P5().apply {
        noCanvas()
        createCanvas(w, h).apply { if (hide) hide() }
        noLoop()
    }
    fun createGraphics(wh: Vector, hide: Boolean = true): P5 = createGraphics(wh.x, wh.y, hide)
    fun createGraphics(w: Number, h: Number, renderMode: RenderMode, hide: Boolean = true): P5 = P5().apply {
        noCanvas()
        createCanvas(w, h, renderMode).apply { if (hide) hide() }
        noLoop()
    }
    fun createGraphics(wh: Vector, renderMode: RenderMode, hide: Boolean = true): P5 =
        createGraphics(wh.x, wh.y, renderMode, hide)

    fun createGraphics(hide: Boolean = true): P5 = createGraphics(0, 0, hide)
    fun createGraphics(renderMode: RenderMode, hide: Boolean = true): P5 = createGraphics(0, 0, renderMode, hide)
    fun blendMode(mode: BlendMode) = nativeP5.blendMode(mode.nativeValue)
    val drawingContext: dynamic get() = nativeP5.drawingContext // TODO: Remove dynamic
    fun setAttributes(key: RenderAttribute, value: Boolean) = nativeP5.setAttributes(key.nativeValue, value)
    val canvasHtml: dynamic get() = nativeP5.canvasHtml

    // TRANSFORM
    fun applyMatrix(a: Number, b: Number, c: Number, d: Number, e: Number, f: Number) =
        nativeP5.applyMatrix(a, b, c, d, e, f)

    fun applyMatrix(
        a: Number, b: Number, c: Number, d: Number,
        e: Number, f: Number, g: Number, h: Number,
        i: Number, j: Number, k: Number, l: Number,
        m: Number, n: Number, o: Number, p: Number
    ) = nativeP5.applyMatrix(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)

    fun rotateMatrix() = nativeP5.rotateMatrix()
    fun rotate(angle: Number) = nativeP5.rotate(angle)
    fun rotate(angle: Number, axis: NativeVector) = nativeP5.rotate(angle, axis)
    fun rotateX(angle: Number) = nativeP5.rotateX(angle)
    fun rotateY(angle: Number) = nativeP5.rotateY(angle)
    fun rotateZ(angle: Number) = nativeP5.rotateZ(angle)
    fun scale(s: Number) = nativeP5.scale(s)
    fun scale(x: Number, y: Number) = nativeP5.scale(x, y)
    fun scale(x: Number, y: Number, z: Number) = nativeP5.scale(x, y, z)
    fun scale(scale: Vector) = nativeP5.scale(scale.nativeVector)
    fun shearX(angle: Number) = nativeP5.shearX(angle)
    fun shearY(angle: Number) = nativeP5.shearY(angle)
    fun translate(x: Number, y: Number) = nativeP5.translate(x, y)
    fun translate(x: Number, y: Number, z: Number) = nativeP5.translate(x, y, z)
    fun translate(vector: Vector) = nativeP5.translate(vector.nativeVector)

    fun storeItem(key: String, value: String) = nativeP5.storeItem(key, value)
    fun storeItem(key: String, value: Number) = nativeP5.storeItem(key, value)
    fun storeItem(key: String, value: Boolean) = nativeP5.storeItem(key, value)
    fun storeItem(key: String, value: Color) = nativeP5.storeItem(key, value.nativeColor)
    fun storeItem(key: String, value: Vector) = nativeP5.storeItem(key, value.nativeVector)
    fun <T> getItem(key: String): T? = nativeP5.getItem(key)
    fun clearStructure() = nativeP5.clearStructure()
    fun removeItem(key: String) = nativeP5.removeItem(key)

    fun hex(n: Number): String = nativeP5.hex(n)
    fun hex(n: Number, digits: Number): String = nativeP5.hex(n, digits)
    fun hex(ns: Array<Number>): Array<String> = nativeP5.hex(ns)
    fun hex(ns: Array<Number>, digits: Number): Array<String> = nativeP5.hex(ns, digits)

    // EVENTS

    // Acceleration
    val deviceOrientation: DeviceOrientation?
        get() = when (nativeP5.deviceOrientation) {
            "landscape" -> DeviceOrientation.LANDSCAPE
            "portrait" -> DeviceOrientation.PORTRAIT
            else -> null
        }
    val accelerationX: Number by nativeP5::accelerationX
    val accelerationY: Number by nativeP5::accelerationY
    val accelerationZ: Number by nativeP5::accelerationZ
    val pAccelerationX: Number by nativeP5::pAccelerationX
    val pAccelerationY: Number by nativeP5::pAccelerationX
    val pAccelerationZ: Number by nativeP5::pAccelerationX
    val rotationX: Number by nativeP5::rotationX
    val rotationY: Number by nativeP5::rotationY
    val rotationZ: Number by nativeP5::rotationZ
    val pRotationX: Number by nativeP5::pRotationX
    val pRotationY: Number by nativeP5::pRotationY
    val pRotationZ: Number by nativeP5::pRotationZ
    val turnAxis: String by nativeP5::turnAxis
    var deviceMoved: () -> Unit by nativeP5::deviceMoved
    var deviceTurned: () -> Unit by nativeP5::deviceTurned
    var deviceShaken: () -> Unit by nativeP5::deviceShaken
    fun setMoveThreshold(value: Number) = nativeP5.setMoveThreshold(value)
    fun setShakeThreshold(value: Number) = nativeP5.setShakeThreshold(value)

    // Keyboard
    val isKeyPressed: Boolean by nativeP5::isKeyPressed
    val key: String by nativeP5::key
    val keyCode: Number by nativeP5::keyCode
    var keyPressed: (KeyboardEvent) -> Unit
        get() = { nativeP5.keyPressed(it.nativeKeyboardEvent) }
        set(value) {
            nativeP5.keyPressed = { value(KeyboardEvent(it)) }
        }
    var keyReleased: (KeyboardEvent) -> Unit
        get() = { nativeP5.keyPressed(it.nativeKeyboardEvent) }
        set(value) {
            nativeP5.keyPressed = { value(KeyboardEvent(it)) }
        }
    var keyTyped: (KeyboardEvent) -> Unit
        get() = { nativeP5.keyPressed(it.nativeKeyboardEvent) }
        set(value) {
            nativeP5.keyPressed = { value(KeyboardEvent(it)) }
        }

    fun keyIsDown(code: Int): Boolean = nativeP5.keyIsDown(code)

    // Mouse
    val movedX: Number by nativeP5::movedX
    val movedY: Number by nativeP5::movedY
    val moved: Vector get() = createVector(movedX, movedY)
    val mouseX: Number by nativeP5::mouseX
    val mouseY: Number by nativeP5::mouseY
    val mouse: Vector get() = createVector(mouseX, mouseY)
    val pmouseX: Number by nativeP5::pmouseX
    val pmouseY: Number by nativeP5::pmouseY
    val pmouse: Vector get() = createVector(pmouseX, pmouseY)
    val winMouseX: Number by nativeP5::winMouseX
    val winMouseY: Number by nativeP5::winMouseY
    val winMouse: Vector get() = createVector(winMouseX, winMouseY)
    val absMouseX: Number get() = nativeP5.mouseX + (getCanvas().position().x as Double)
    val absMouseY: Number get() = nativeP5.mouseY + (getCanvas().position().y as Double)
    val absMouse: Vector get() = createVector(absMouseX, absMouseY)
    val pwinMouseX: Number by nativeP5::pwinMouseX
    val pwinMouseY: Number by nativeP5::pwinMouseY
    val pwinMouse: Vector get() = createVector(pwinMouseX, pwinMouseY)
    val mouseIsPressed: Boolean by nativeP5::mouseIsPressed
    var mouseMoved: () -> Unit by nativeP5::mouseMoved
    var mouseDragged: () -> Unit by nativeP5::mouseDragged
    var mousePressed: () -> Unit by nativeP5::mousePressed
    var mouseReleased: () -> Unit by nativeP5::mouseReleased
    var mouseClicked: () -> Unit by nativeP5::mouseClicked
    var doubleClicked: () -> Unit by nativeP5::doubleClicked
    var mouseWheel: (WheelEvent) -> Unit
        get() = { nativeP5.mouseWheel(it.nativeWheelEvent) }
        set(value) {
            nativeP5.mouseWheel = { value(WheelEvent(it)) }
        }
    val mouseButton: MouseButton?
        get() {
            return when (nativeP5.mouseButton) {
                MouseButton.CENTER.nativeValue -> MouseButton.CENTER
                MouseButton.LEFT.nativeValue -> MouseButton.LEFT
                MouseButton.RIGHT.nativeValue -> MouseButton.RIGHT
                else -> null
            }
        }

    fun requestPointerLock() = nativeP5.requestPointerLock()
    fun exitPointerLock() = nativeP5.exitPointerLock()

    // Touch
    val touches: Array<dynamic> by nativeP5::touches// TODO: Remove Dynamic
    var touchStarted: () -> Unit by nativeP5::touchStarted
    var touchMoved: () -> Unit by nativeP5::touchMoved
    var touchEnded: () -> Unit by nativeP5::touchEnded

    // IMAGES
    fun createImage(width: Int, height: Int): Image = Image(nativeP5.createImage(width, height))
    fun saveCanvas(fileName: String) = nativeP5.saveCanvas(fileName)
    fun saveCanvas(filename: String, extension: ImageExtension) = nativeP5.saveCanvas(filename, extension.nativeValue)
    fun saveCanvas(selectedCanvas: Element, filename: String) =
        nativeP5.saveCanvas(selectedCanvas.nativeElement, filename)

    fun saveCanvas(selectedCanvas: Element, filename: String, extension: ImageExtension) =
        nativeP5.saveCanvas(selectedCanvas.nativeElement, filename, extension.nativeValue)

    fun saveFrames(filename: String, extension: ImageExtension, duration: Number, framerate: Number) =
        nativeP5.saveFrames(filename, extension.nativeValue, duration, framerate)

    fun saveFrames(
        filename: String,
        extension: ImageExtension,
        duration: Number,
        framerate: Number,
        callback: (dynamic) -> Unit
    ) =
        nativeP5.saveFrames(filename, extension.nativeValue, duration, framerate, callback) // TODO: Remove Dynamic

    // Loading & Displaying
    fun loadImage(path: String): Image = Image(nativeP5.loadImage(path))
    fun loadImage(path: String, successCallback: (Image) -> Unit): Image =
        Image(nativeP5.loadImage(path) { successCallback(Image(it)) })

    // fun loadImage(path: String, successCallback: (Image)->Unit, failureCallback: (dynamic)->Unit): Image TODO: Implement this
    fun image(img: Image, x: Number, y: Number) = nativeP5.image(img.nativeImage, x, y)
    fun image(img: Image, xy: Vector) = image(img, xy.x, xy.y)
    fun image(img: Image, x: Number, y: Number, width: Number, height: Number) =
        nativeP5.image(img.nativeImage, x, y, width, height)

    fun image(img: Image, xy: Vector, width: Number, height: Number) = image(img, xy.x, xy.y, width, height)
    fun image(img: Image, xy: Vector, wh: Vector) = image(img, xy.x, xy.y, wh.x, wh.y)
    fun image(img: P5, x: Number, y: Number, width: Number, height: Number) =
        nativeP5.image(img.nativeP5, x, y, width, height)

    fun image(img: P5, xy: Vector, width: Number, height: Number) = image(img, xy.x, xy.y, width, height)
    fun image(img: P5, xy: Vector, wh: Vector) = image(img, xy.x, xy.y, wh.x, wh.y)
    fun image(img: Image, dx: Number, dy: Number, dWidth: Number, dHeight: Number, sx: Number, sy: Number) =
        nativeP5.image(img.nativeImage, dx, dy, dWidth, dHeight, sx, sy)

    fun image(
        img: Image,
        dx: Number,
        dy: Number,
        dWidth: Number,
        dHeight: Number,
        sx: Number,
        sy: Number,
        sWidth: Number,
        sHeight: Number
    ) =
        nativeP5.image(img.nativeImage, dx, dy, dWidth, dHeight, sx, sy, sWidth, sHeight)

    fun tint(gray: Number) = nativeP5.tint(gray)
    fun tint(gray: Number, alpha: Number) = nativeP5.tint(gray, alpha)
    fun tint(v1: Number, v2: Number, v3: Number) = nativeP5.tint(v1, v2, v3)
    fun tint(v1: Number, v2: Number, v3: Number, alpha: Number) = nativeP5.tint(v1, v2, v3, alpha)
    fun tint(colorString: String) = nativeP5.tint(colorString)
    fun tint(colorArray: Array<Number>) = nativeP5.tint(colorArray)
    fun tint(color: Color) = nativeP5.tint(color.nativeColor)
    fun noTint() = nativeP5.noTint()
    fun imageMode(mode: ImageMode) = nativeP5.imageMode(mode.nativeValue)

    //Pixels
    fun blend(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: BlendMode) =
        nativeP5.blend(sx, sy, sw, sh, dx, dy, dw, dh, blendMode.nativeValue)

    fun blend(
        srcImage: Image,
        sx: Int,
        sy: Int,
        sw: Int,
        sh: Int,
        dx: Int,
        dy: Int,
        dw: Int,
        dh: Int,
        blendMode: BlendMode
    ) =
        nativeP5.blend(srcImage.nativeImage, sx, sy, sw, sh, dx, dy, dw, dh, blendMode.nativeValue)

    fun copy(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int) =
        nativeP5.copy(sx, sy, sw, sh, dx, dy, dw, dh)

    fun copy(srcImage: Image, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int) =
        nativeP5.copy(srcImage.nativeImage, sx, sy, sw, sh, dx, dy, dw, dh)

    fun filter(filterMode: FilterMode) = nativeP5.filter(filterMode.nativeValue)
    fun filter(filterMode: FilterMode, filterParam: Number) = nativeP5.filter(filterMode.nativeValue, filterParam)

    fun arrayToColor(array: Array<Double>): Color {
        return color(
            maxRed * array[0] / 255.0,
            maxGreen * array[1] / 255,
            maxBlue * array[2] / 255,
            maxAlpha * array[3] / 255
        )
    }

    fun get(): Image = Image(nativeP5.get())
    fun get(x: Number, y: Number): Color = arrayToColor(nativeP5.get(x, y))
    fun get(xy: Vector): Color = get(xy.x.toInt(), xy.y.toInt())
    fun get(x: Number, y: Number, w: Number, h: Number): Image = Image(nativeP5.get(x, y, w, h))
    fun get(xy: Vector, w: Number, h: Number): Image = Image(nativeP5.get(xy.x.toInt(), xy.y.toInt(), w, h))
    fun get(xy: Vector, wh: Vector): Image = Image(nativeP5.get(xy.x.toInt(), xy.y.toInt(), wh.x.toInt(), wh.y.toInt()))

    fun set(x: Number, y: Number, a: Number) = nativeP5.set(x, y, a)
    fun set(xy: Vector, a: Number) = nativeP5.set(xy.x, xy.y, a)
    fun set(x: Number, y: Number, a: Array<Number>) = nativeP5.set(x, y, a)
    fun set(xy: Vector, a: Array<Number>) = nativeP5.set(xy.x, xy.y, a)
    fun set(x: Number, y: Number, a: Color) = nativeP5.set(x, y, a.nativeColor)
    fun set(xy: Vector, a: Color) = nativeP5.set(xy.x, xy.y, a.nativeColor)
    fun set(x: Number, y: Number, a: Image) = nativeP5.set(x, y, a.nativeImage)
    fun set(xy: Vector, a: Image) = nativeP5.set(xy.x, xy.y, a.nativeImage)

    // IO
    fun loadStrings(filename: String): Array<String> = nativeP5.loadStrings(filename)
    fun loadStrings(filename: String, callback: (Array<String>) -> Unit): Array<String> =
        nativeP5.loadStrings(filename, callback)

    fun loadStrings(
        filename: String,
        callback: (Array<String>) -> Unit,
        errorCallback: (dynamic) -> Unit
    ): Array<String> = loadStrings(filename, callback, errorCallback)

    fun loadJSON(path: String): Json = nativeP5.loadJSON(path)
    fun loadJSON(path: String, callback: (Json) -> Unit): Json = nativeP5.loadJSON(path, callback)
    fun loadJSON(path: String, callback: (Json) -> Unit, errorCallback: () -> Unit): Json =
        nativeP5.loadJSON(path, callback, errorCallback)

    fun loadJSON(path: String, jsonType: JsonType): Json = nativeP5.loadJSON(path, jsonType.nativeValue)
    fun loadJSON(path: String, jsonType: JsonType, callback: (Json) -> Unit): Json =
        nativeP5.loadJSON(path, jsonType.nativeValue, callback)

    fun loadJSON(path: String, jsonType: JsonType, callback: (Json) -> Unit, errorCallback: () -> Unit): Json =
        nativeP5.loadJSON(path, jsonType.nativeValue, callback, errorCallback)

    // TODO: Remove Dynamic, Make Enum
    fun httpDo(path: String): dynamic = nativeP5.httpDo(path)
    fun httpDo(path: String, method: String): dynamic = nativeP5.httpDo(path, method)
    fun httpDo(path: String, method: String, datatype: String): dynamic = nativeP5.httpDo(path, method, datatype)
    fun httpDo(path: String, method: String, datatype: String, data: dynamic): dynamic =
        nativeP5.httpDo(path, method, datatype, data)

    fun httpDo(path: String, method: String, datatype: String, data: dynamic, callback: (dynamic) -> Unit): dynamic =
        nativeP5.httpDo(path, method, datatype, data, callback)

    fun httpDo(
        path: String,
        method: String,
        datatype: String,
        data: dynamic,
        callback: (dynamic) -> Unit,
        errorCallback: () -> Unit
    ): dynamic =
        nativeP5.httpDo(path, method, datatype, data, callback, errorCallback)

    fun httpDo(path: String, options: dynamic) = nativeP5.httpDo(path, options)
    fun httpDo(path: String, options: dynamic, callback: (dynamic) -> Unit) = nativeP5.httpDo(path, options, callback)
    fun httpDo(path: String, options: dynamic, callback: (dynamic) -> Unit, errorCallback: () -> Unit) =
        nativeP5.httpDo(path, options, callback, errorCallback)

    fun createWriter(name: String): PrintWriter = PrintWriter(nativeP5.createWriter(name))

    // TODO: Remove Dynamic
    fun save(filename: String) = nativeP5.save(filename)
    fun save(obj: Element, filename: String) = nativeP5.save(obj.nativeElement, filename)
    fun save(obj: String, filename: String) = nativeP5.save(obj, filename)
    fun save(obj: Array<String>, filename: String) = nativeP5.save(obj, filename)
    fun save(obj: Image, filename: String) = nativeP5.save(obj.nativeImage, filename)
    fun save(filename: String, options: dynamic) = nativeP5.save(filename, options)
    fun save(obj: Element, filename: String, options: dynamic) = nativeP5.save(obj.nativeElement, filename, options)
    fun save(obj: String, filename: String, options: dynamic) = nativeP5.save(obj, filename, options)
    fun save(obj: Array<String>, filename: String, options: dynamic) = nativeP5.save(obj, filename, options)
    fun save(obj: Image, filename: String, options: dynamic) = nativeP5.save(obj.nativeImage, filename, options)

    fun saveTable(table: Table, filename: String) = nativeP5.saveTable(table.nativeTable, filename)
    fun saveTable(table: Table, filename: String, extension: TableMode) =
        nativeP5.saveTable(table.nativeTable, filename, extension.nativeValue) // TODO: Make Enum

    fun loadTable(filename: String): Table = Table(nativeP5.loadTable(filename))
    fun loadTable(filename: String, extension: TableMode): Table =
        Table(nativeP5.loadTable(filename, extension.nativeValue))

    fun loadTable(filename: String, extension: TableMode, hasHeaders: Boolean): Table =
        Table(nativeP5.loadTable(filename, extension.nativeValue, if (hasHeaders) "header" else ""))

    fun loadTable(filename: String, extension: TableMode, hasHeaders: Boolean, callback: (Table) -> Unit): Table =
        Table(
            nativeP5.loadTable(
                filename,
                extension.nativeValue,
                if (hasHeaders) "header" else ""
            ) { callback(Table(it)) })

    fun loadTable(
        filename: String,
        extension: TableMode,
        hasHeaders: Boolean,
        callback: (Table) -> Unit,
        errorCallback: (dynamic) -> Unit
    ): Table =
        Table(
            nativeP5.loadTable(
                filename,
                extension.nativeValue,
                if (hasHeaders) "header" else "",
                { callback(Table(it)) },
                errorCallback
            )
        )

    fun createTable(): Table = Table(NativeTable())
    fun createTable(rows: Array<TableRow>): Table = Table(NativeTable(rows.arrayMap { it.nativeTableRow }))
    fun createTableRow(): TableRow = TableRow(NativeTableRow())
    fun createTableRow(str: String): TableRow = TableRow(NativeTableRow(str))
    fun createTableRow(str: String, separator: String): TableRow = TableRow(NativeTableRow(str, separator))

    // Time & Date
    fun day(): Int = nativeP5.day()
    fun hour(): Int = nativeP5.hour()
    fun minute(): Int = nativeP5.minute()
    fun millis(): Int = nativeP5.millis()
    fun month(): Int = nativeP5.month()
    fun second(): Int = nativeP5.second()
    fun year(): Int = nativeP5.year()

    // MATH

    fun map(value: Number, start1: Number, stop1: Number, start2: Number, stop2: Number): Double =
        nativeP5.map(value, start1, stop1, start2, stop2)

    fun map(
        value: Number,
        start1: Number,
        stop1: Number,
        start2: Number,
        stop2: Number,
        withinBounds: Boolean
    ): Double =
        nativeP5.map(value, start1, stop1, start2, stop2, withinBounds)

    @Serializable(with = VectorSerializer::class)
    class Vector(val nativeVector: NativeVector) {
        constructor() : this(NativeVector())
        constructor(x: Number) : this(NativeVector(x))
        constructor(x: Number, y: Number) : this(NativeVector(x, y))
        constructor(x: Number, y: Number, z: Number) : this(NativeVector(x, y, z))

        var x: Double by nativeVector::x
        var y: Double by nativeVector::y
        var z: Double by nativeVector::z
        override fun toString(): String = nativeVector.toString()
        fun set(x: Number, y: Number, z: Number) = nativeVector.set(x, y, z)
        fun set(value: Vector) = nativeVector.set(value.nativeVector)
        fun copy(): Vector = Vector(nativeVector.copy())
        fun add(value: Vector) = nativeVector.add(value.nativeVector)
        fun rem(value: Vector) = nativeVector.rem(value.nativeVector)
        fun sub(value: Vector) = nativeVector.sub(value.nativeVector)
        fun mult(n: Number) = nativeVector.mult(n)
        fun mult(v: Vector) = nativeVector.mult(v.nativeVector)
        fun div(n: Number) = nativeVector.div(n)
        fun div(v: Vector) = nativeVector.div(v.nativeVector)
        fun mag(): Double = nativeVector.mag()
        fun magSq(): Double = nativeVector.magSq()
        fun dot(v: Vector): Double = nativeVector.dot(v.nativeVector)
        fun cross(v: Vector) {
            nativeVector.cross(v.nativeVector)
        }

        fun crossed(v: Vector): Vector = cross(this, v)
        fun dist(v: Vector): Double = nativeVector.dist(v.nativeVector)
        fun normalize() {
            nativeVector.normalize()
        }

        fun normalized(): Vector = normalize(this)
        fun limit(n: Number) {
            nativeVector.limit(n)
        }

        fun limited(n: Number): Vector = Vector(nativeVector.copy().limit(n))
        fun setMag(len: Number) {
            nativeVector.setMag(len)
        }

        fun heading(): Double = nativeVector.heading()
        fun setHeading(angle: Number) = nativeVector.setHeading(angle)
        fun rotate(angle: Number) {
            nativeVector.rotate(angle)
        }

        fun rotated(angle: Number): Vector = rotate(this, angle)
        fun angleBetween(v: Vector): Double = nativeVector.angleBetween(v.nativeVector)
        fun lerp(v: Vector): Vector {
            nativeVector.lerp(v.nativeVector)
            return this
        }

        fun lerped(v: Vector): Vector = Vector(nativeVector.copy().lerp(v.nativeVector))
        fun reflect(v: Vector): Vector {
            nativeVector.reflect(v.nativeVector)
            return this
        }

        fun reflected(v: Vector): Vector = Vector(nativeVector.copy().reflect(v.nativeVector))
        fun array(): Array<Number> = nativeVector.array()
        override fun equals(other: Any?): Boolean = when (other) {
            is Vector -> (x == other.x) && (y == other.y) && (z == other.z)
            else -> false
        }

        override fun hashCode(): Int {
            var result = x.hashCode()
            result = 31 * result + y.hashCode()
            result = 31 * result + z.hashCode()
            return result
        }

        companion object {
            fun fromAngle(angle: Number): Vector = Vector(NativeVector.fromAngle(angle))
            fun fromAngle(angle: Number, length: Number): Vector = Vector(NativeVector.fromAngle(angle, length))
            fun fromAngles(theta: Number, phi: Number): Vector = Vector(NativeVector.fromAngles(theta, phi))
            fun fromAngles(theta: Number, phi: Number, length: Number): Vector =
                Vector(NativeVector.fromAngles(theta, phi, length))

            fun random2D(): Vector = Vector(NativeVector.random2D())
            fun random3D(): Vector = Vector(NativeVector.random3D())
            fun add(v1: Vector, v2: Vector): Vector = Vector(NativeVector.add(v1.nativeVector, v2.nativeVector))
            fun rem(v1: Vector, v2: Vector): Vector = Vector(NativeVector.rem(v1.nativeVector, v2.nativeVector))
            fun sub(v1: Vector, v2: Vector): Vector = Vector(NativeVector.sub(v1.nativeVector, v2.nativeVector))
            fun mult(v: Vector, n: Number): Vector = Vector(NativeVector.mult(v.nativeVector, n))
            fun mult(v1: Vector, v2: Vector): Vector = Vector(NativeVector.mult(v1.nativeVector, v2.nativeVector))
            fun div(v: Vector, n: Number): Vector = Vector(NativeVector.div(v.nativeVector, n))
            fun div(v1: Vector, v2: Vector): Vector = Vector(NativeVector.div(v1.nativeVector, v2.nativeVector))
            fun lerp(v1: Vector, v2: Vector, amt: Number): Vector =
                Vector(NativeVector.lerp(v1.nativeVector, v2.nativeVector, amt))

            fun cross(v1: Vector, v2: Vector): Vector = Vector(NativeVector.cross(v1.nativeVector, v2.nativeVector))
            fun dot(v1: Vector, v2: Vector): Double = NativeVector.dot(v1.nativeVector, v2.nativeVector)
            fun dist(v1: Vector, v2: Vector): Double = NativeVector.dist(v1.nativeVector, v2.nativeVector)
            fun normalize(v: Vector): Vector = Vector(NativeVector.normalize(v.nativeVector))
            fun rotate(v: Vector, angle: Number): Vector = Vector(NativeVector.rotate(v.nativeVector, angle))
        }
    }

    fun lerp(start: Number, stop: Number, amt: Number): Double = nativeP5.lerp(start, stop, amt)

    fun createVector(): Vector = Vector(nativeP5.createVector())
    fun createVector(x: Number): Vector = Vector(nativeP5.createVector(x, 0))
    fun createVector(x: Number, y: Number): Vector = Vector(nativeP5.createVector(x, y))
    fun createVector(x: Number, y: Number, z: Number): Vector = Vector(nativeP5.createVector(x, y, z))

    // Noise
    fun noise(x: Number): Double = nativeP5.noise(x)
    fun noise(x: Number, y: Number): Double = nativeP5.noise(x, y)
    fun noise(x: Number, y: Number, z: Number): Double = nativeP5.noise(x, y, z)
    fun noiseDetail(lod: Number, falloff: Number) = nativeP5.noiseDetail(lod, falloff)
    fun noiseSeed(seed: Int) = nativeP5.noiseSeed(seed)

    // Random
    fun randomSeed(seed: Int) = nativeP5.randomSeed(seed)
    fun random(): Double = nativeP5.random()
    fun random(max: Number): Double = nativeP5.random(max)
    fun random(min: Number, max: Number): Double = nativeP5.random(min, max)
    fun <T> random(choices: Array<T>): T = nativeP5.random(choices)
    fun randomGaussian(): Double = nativeP5.randomGaussian()
    fun randomGaussian(mean: Number): Double = nativeP5.randomGaussian(mean)
    fun randomGaussian(mean: Number, sd: Number): Double = nativeP5.randomGaussian(mean, sd)

    // Trig
    fun angleMode(mode: AngleMode) = nativeP5.angleMode(mode.nativeValue)

    // TYPOGRAPHY

    fun textAlign(horizAlign: String) = nativeP5.textAlign(horizAlign) // TODO: Make Enum
    fun textAlign(horizAlign: String, vertAlign: String) = nativeP5.textAlign(horizAlign, vertAlign) // TODO: Make Enum
    fun textLeading(): Double = nativeP5.textLeading()
    fun textLeading(leading: Number) = nativeP5.textLeading(leading)
    fun textSize(): Double = nativeP5.textSize()
    fun textSize(size: Number) = nativeP5.textSize(size)
    fun textStyle(): String = nativeP5.textStyle() // TODO: Make Enum
    fun textStyle(style: String) = nativeP5.textStyle(style) // TODO: Make Enum
    fun textWidth(text: String): Double = nativeP5.textWidth(text)
    fun textAscent(): Double = nativeP5.textAscent()
    fun textDecent(): Double = nativeP5.textDecent()
    fun textWrap(): String = nativeP5.textWrap() // TODO: Make Enum
    fun textWrap(wrapStyle: String) = nativeP5.textWrap(wrapStyle) // TODO: Make Enum
    fun loadFont(path: String): Font = Font(nativeP5.loadFont(path))
    fun loadFont(path: String, callback: (Font) -> Unit): Font = Font(nativeP5.loadFont(path) { callback(Font(it)) })
    fun loadFont(path: String, callback: (Font) -> Unit, onError: (dynamic) -> Unit): Font =
        Font(nativeP5.loadFont(path, { callback(Font(it)) }, onError))

    fun text(str: String, x: Number, y: Number) = nativeP5.text(str, x, y)
    fun text(str: String, xy: Vector) = nativeP5.text(str, xy.x, xy.y)
    fun text(str: String, x: Number, y: Number, x2: Number, y2: Number) = nativeP5.text(str, x, y, x2, y2)
    fun text(str: String, xy: Vector, xy2: Vector) = nativeP5.text(str, xy.x, xy.y, xy2.x, xy2.y)
    fun textFont(): Font? = nativeP5.textFont()?.let { Font(it) }
    fun textFont(font: Font) = nativeP5.textFont(font.nativeFont)
    fun textFont(font: Font, size: Number) = nativeP5.textFont(font.nativeFont, size)
    fun textFont(fontName: String) = nativeP5.textFont(fontName)
    fun textFont(fontName: String, size: Number) = nativeP5.textFont(fontName, size)

    // SHADERS
    fun loadShader(vertFilename: String, fragFilename: String): Shader =
        Shader(nativeP5.loadShader(vertFilename, fragFilename))

    fun loadShader(vertFilename: String, fragFilename: String, callback: (Shader) -> Unit): Shader =
        Shader(nativeP5.loadShader(vertFilename, fragFilename) { callback(Shader(it)) })

    fun loadShader(
        vertFilename: String,
        fragFilename: String,
        callback: (Shader) -> Unit,
        errorCallback: (dynamic) -> Unit
    ): Shader =
        Shader(nativeP5.loadShader(vertFilename, fragFilename, { callback(Shader(it)) }, errorCallback))

    fun shader(s: Shader) = nativeP5.shader(s.nativeShader)
    fun resetShader() = nativeP5.resetShader()
    fun texture(tex: Image) = nativeP5.texture(tex.nativeImage)
    fun texture(tex: MediaElement) = nativeP5.texture(tex.nativeMediaElement)
    fun texture(tex: P5) = nativeP5.texture(tex.nativeP5)
    fun texture(tex: Texture) = nativeP5.texture(tex.nativeTexture)
    fun textureMode(mode: String) = nativeP5.textureMode(mode) // TODO: Make Enum
    fun textureWrap(wrapX: String, wrapY: String) = nativeP5.textureWrap(wrapX, wrapX) // TODO: Make Enum

    fun createShader(
        vertSrc: String,
        fragSrc: String,
        uniformCallbacks: MutableMap<String, () -> Any> = mutableMapOf()
    ): Shader =
        Shader(nativeP5.createShader(vertSrc, fragSrc), uniformCallbacks)

    fun Shader.update(updateMipmap: Boolean = false) {
        updateUniformCallbacks()
        val gl = getCanvas().gl
        //if(updateMipmap) {
            gl.generateMipmap(gl.TEXTURE_2D)
        //}
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, minFilterMode.nativeValue)
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, magFilterMode.nativeValue)
    }

    // SCOPE EXTENSION FUNCTIONS
    fun buildShape2D(pathMode: PathMode?, close: CLOSE?, path: ShapeBuilder2D.() -> Unit) {
        val shapeScope = ShapeBuilder2D()
        if (pathMode == null) {
            nativeP5.beginShape()
        } else when (val value = pathMode.nativeValue) {
            is String -> nativeP5.beginShapeString(value)
            is Number -> nativeP5.beginShapeNumber(value)
            else -> throw IllegalStateException()
        }
        path(shapeScope)
        if (close == null) nativeP5.endShape() else nativeP5.endShape("close")
    }

    fun buildShape2D(path: ShapeBuilder2D.() -> Unit) = buildShape2D(null, null, path)
    fun buildShape2D(pathMode: PathMode, path: ShapeBuilder2D.() -> Unit) = buildShape2D(pathMode, null, path)
    fun buildShape2D(close: CLOSE?, path: ShapeBuilder2D.() -> Unit) = buildShape2D(null, close, path)


    fun buildShape3D(pathMode: PathMode? = null, close: CLOSE? = null, path: ShapeBuilder3D.() -> Unit) {
        val shapeScope = ShapeBuilder3D()
        when (val value = pathMode?.nativeValue) {
            (value == null) -> nativeP5.beginShape()
            is String -> nativeP5.beginShapeString(value)
            is Number -> nativeP5.beginShapeNumber(value)
        }
        path(shapeScope)
        if (close == null) nativeP5.endShape() else nativeP5.endShape("close")
    }

    var isWebgl2Enabled: Boolean = false
    fun enableWebgl2() {
        if (isWebgl2Enabled) {
            return
        }
        val p5 = p5
        js(
            """ 
            console.log("enabling experimental WEBGL2 mode");
            p5.RendererGL.prototype._initContext = function() {
                try {
                    this.drawingContext = 
                        this.canvas.getContext("webgl2", this._pInst._glAttributes) ||
                        this.canvas.getContext("webgl", this._pInst._glAttributes) || 
                        this.canvas.getContext("experimental-webgl", this._pInst._glAttributes);
                    if (this.drawingContext === null) { 
                        throw new Error("Error creating webgl context");
                    } 
                    var e = this.drawingContext;
                    e.clearColor(0.0, 0.0, 0.0, 0.0);
                    //e.enable(e.DEPTH_TEST);
                     e.disable(e.DEPTH_TEST); 
                    e.depthFunc(e.LEQUAL);
                    e.viewport(0, 0, e.drawingBufferWidth, e.drawingBufferHeight);
                    e.enable(e.BLEND); 
                    e.blendFunc(e.SRC_ALPHA, e.ONE_MINUS_SRC_ALPHA);
                    this._viewport = this.drawingContext.getParameter(this.drawingContext.VIEWPORT);
                } catch (e) {
                    throw e
                }
            };"""
        )
        isWebgl2Enabled = true
    }

    sealed class PixelSource {
        abstract val pixels: Array<Int>
    }

    class P5PixelSource(val instance: P5) : PixelSource() {
        override val pixels: Array<Int>
            get() {
                return instance.nativeP5.pixels
            }
    }

    class ImagePixelSource(val instance: Image) : PixelSource() {
        override val pixels: Array<Int>
            get() {
                return instance.pixels
            }
    }

    fun <T> withPixels(density: Int = 1, block: PixelScope.() -> T): T {
        val pixelScope = PixelScope(density, P5PixelSource(this))
        if (density != pixelDensity()) {
            pixelDensity(density)
        }
        nativeP5.loadPixels()
        val result = block(pixelScope)
        nativeP5.updatePixels()
        return result
    }

    fun <T> Image.withPixels(density: Int = 1, block: PixelScope.() -> T): T {
        val pixelScope = PixelScope(density, ImagePixelSource(this))
        if (density != pixelDensity()) {
            pixelDensity(density)
        }
        nativeImage.loadPixels()
        val result = block(pixelScope)
        nativeImage.updatePixels()
        return result
    }

    inner class PixelScope(val pd: Int, val pixelSource: PixelSource) {

        val pixels: Array<Int>
            get() {
                return pixelSource.pixels
            }

        fun RCToI(row: Int, col: Int): Int {
            return 4 * pd * (pd * width * row + col)
        }

        fun RCToI(row: Double, col: Double): Int {
            return 4 * pd * width * (pd * row).toInt() + 4 * (pd * col).toInt()
        }

        fun RCToI(vector: Vector): Int {
            return RCToI(vector.y, vector.x)
        }

        val redChannel = object : PixelArray<Double> {

            fun encode(element: Double): Int {
                return (element / maxRed * 255.0).toInt()
            }

            fun decode(element: Int): Double {
                return (element * maxRed) / 255.0
            }

            override fun set(index: Int, element: Double) {
                pixels[index * 4] = encode(element)
            }

            override fun get(index: Int): Double {
                return decode(pixels[index * 4])
            }

            override fun set(row: Int, col: Int, element: Double) {
                pixels[RCToI(row, col)] = encode(element)
            }

            override fun get(row: Int, col: Int): Double {
                return decode(pixels[RCToI(row, col)])
            }

            override fun set(row: Double, col: Double, element: Double) {
                pixels[RCToI(row, col)] = encode(element)
            }

            override fun get(row: Double, col: Double): Double {
                return decode(pixels[RCToI(row, col)])
            }

            override fun set(vector: Vector, element: Double) {
                pixels[RCToI(vector)] = encode(element)
            }

            override fun get(vector: Vector): Double {
                return decode(pixels[RCToI(vector)])
            }
        }

        val greenChannel = object : PixelArray<Double> {
            fun encode(element: Double): Int {
                return (element / maxGreen * 255.0).toInt()
            }

            fun decode(element: Int): Double {
                return (element * maxGreen) / 255.0
            }

            override fun set(index: Int, element: Double) {
                pixels[index * 4 + 1] = encode(element)
            }

            override fun get(index: Int): Double {
                return decode(pixels[index * 4 + 1])
            }

            override fun set(row: Int, col: Int, element: Double) {
                pixels[RCToI(row, col) + 1] = encode(element)
            }

            override fun get(row: Int, col: Int): Double {
                return decode(pixels[RCToI(row, col) + 1])
            }

            override fun set(row: Double, col: Double, element: Double) {
                pixels[RCToI(row, col) + 1] = encode(element)
            }

            override fun get(row: Double, col: Double): Double {
                return decode(pixels[RCToI(row, col) + 1])
            }

            override fun set(vector: Vector, element: Double) {
                pixels[RCToI(vector) + 1] = encode(element)
            }

            override fun get(vector: Vector): Double {
                return decode(pixels[RCToI(vector) + 1])
            }
        }

        val blueChannel = object : PixelArray<Double> {
            fun encode(element: Double): Int {
                return (element / maxBlue * 255.0).toInt()
            }

            fun decode(element: Int): Double {
                return (element * maxBlue) / 255.0
            }

            override fun set(index: Int, element: Double) {
                pixels[index * 4 + 2] = encode(element)
            }

            override fun get(index: Int): Double {
                return decode(pixels[index * 4 + 2])
            }

            override fun set(row: Int, col: Int, element: Double) {
                pixels[RCToI(row, col) + 2] = encode(element)
            }

            override fun get(row: Int, col: Int): Double {
                return decode(pixels[RCToI(row, col) + 2])
            }

            override fun set(row: Double, col: Double, element: Double) {
                pixels[RCToI(row, col) + 2] = encode(element)
            }

            override fun get(row: Double, col: Double): Double {
                return decode(pixels[RCToI(row, col) + 2])
            }

            override fun set(vector: Vector, element: Double) {
                pixels[RCToI(vector) + 2] = encode(element)
            }

            override fun get(vector: Vector): Double {
                return decode(pixels[RCToI(vector) + 2])
            }
        }

        val alphaChannel = object : PixelArray<Double> {
            fun encode(element: Double): Int {
                return (element / maxAlpha * 255.0).toInt()
            }

            fun decode(element: Int): Double {
                return (element * maxAlpha) / 255.0
            }

            override fun set(index: Int, element: Double) {
                pixels[index * 4 + 3] = encode(element)
            }

            override fun get(index: Int): Double {
                return decode(pixels[index * 4 + 3])
            }

            override fun set(row: Int, col: Int, element: Double) {
                pixels[RCToI(row, col) + 3] = encode(element)
            }

            override fun get(row: Int, col: Int): Double {
                return decode(pixels[RCToI(row, col) + 3])
            }

            override fun set(row: Double, col: Double, element: Double) {
                pixels[RCToI(row, col) + 3] = encode(element)
            }

            override fun get(row: Double, col: Double): Double {
                return decode(pixels[RCToI(row, col) + 3])
            }

            override fun set(vector: Vector, element: Double) {
                pixels[RCToI(vector) + 3] = encode(element)
            }

            override fun get(vector: Vector): Double {
                return decode(pixels[RCToI(vector) + 3])
            }
        }

        val colorArray = object : PixelArray<Color> {
            override fun set(index: Int, element: Color) {
                updateAverageFillColor(element)
                redChannel[index] = red(element)
                greenChannel[index] = green(element)
                blueChannel[index] = blue(element)
                alphaChannel[index] = alpha(element)
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
                updateAverageFillColor(element)
                redChannel[row, col] = red(element)
                greenChannel[row, col] = green(element)
                blueChannel[row, col] = blue(element)
                alphaChannel[row, col] = alpha(element)
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
                updateAverageFillColor(element)
                redChannel[row, col] = red(element)
                greenChannel[row, col] = green(element)
                blueChannel[row, col] = blue(element)
                alphaChannel[row, col] = alpha(element)
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
                updateAverageFillColor(element)
                redChannel[vector] = red(element)
                greenChannel[vector] = green(element)
                blueChannel[vector] = blue(element)
                alphaChannel[vector] = alpha(element)
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

    private object _ScalarMode {
        var scalarMode: ScalarMode = ScalarMode.XYZ
    }

    fun scalarMode(axes: ScalarMode) {
        _ScalarMode.scalarMode = axes
    }

    fun scalarMode(): ScalarMode {
        return _ScalarMode.scalarMode
    }

    operator fun Vector.plus(other: Vector) = Vector.add(this, other)
    operator fun Vector.plus(other: Number): Vector {
        return when (_ScalarMode.scalarMode) {
            ScalarMode.X -> Vector.add(this, createVector(other, 0, 0))
            ScalarMode.XY -> Vector.add(this, createVector(other, other, 0))
            ScalarMode.XYZ -> Vector.add(this, createVector(other, other, other))
        }
    }
    operator fun Number.plus(other: Vector): Vector {
        return when (_ScalarMode.scalarMode) {
            ScalarMode.X -> Vector.add(createVector(this, 0, 0), other)
            ScalarMode.XY -> Vector.add(createVector(this, this, 0), other)
            ScalarMode.XYZ -> Vector.add(createVector(this, this, this), other)
        }
    }

    operator fun Vector.rem(other: Vector) = Vector.rem(this, other)

    operator fun Vector.minus(other: Vector) = Vector.sub(this, other)
    operator fun Vector.minus(other: Number): Vector {
        return when (_ScalarMode.scalarMode) {
            ScalarMode.X -> Vector.sub(this, createVector(other, 0, 0))
            ScalarMode.XY -> Vector.sub(this, createVector(other, other, 0))
            ScalarMode.XYZ -> Vector.sub(this, createVector(other, other, other))
        }
    }
    operator fun Number.minus(other: Vector): Vector {
        return when (_ScalarMode.scalarMode) {
            ScalarMode.X -> Vector.sub(createVector(this, 0, 0), other)
            ScalarMode.XY -> Vector.sub(createVector(this, this, 0), other)
            ScalarMode.XYZ -> Vector.sub(createVector(this, this, this), other)
        }
    }

    operator fun Vector.times(other: Number) = Vector.mult(this, other)
    operator fun Vector.times(other: Vector) = Vector.mult(this, other)
    operator fun Number.times(other: Vector) = Vector.mult(other, this)

    operator fun Vector.div(other: Number) = Vector.div(this, other)
    operator fun Vector.div(other: Vector) = Vector.div(this, other)
    infix fun Vector.dot(other: Vector) = Vector.dot(this, other)
    infix fun Vector.cross(other: Vector) = Vector.cross(this, other)
    fun dist(value: Vector, other: Vector) = Vector.dist(value, other)
    infix fun Vector.dist(other: Vector) = dist(other)

    // Double Get
    val Vector.xx: Vector
        get() {
            return createVector(x, x)
        }
    val Vector.yy: Vector
        get() {
            return createVector(y, y)
        }
    val Vector.zz: Vector
        get() {
            return createVector(z, z)
        }

    // Triple Get
    val Vector.xxx: Vector
        get() {
            return createVector(x, x, x)
        }
    val Vector.xxy: Vector
        get() {
            return createVector(x, x, y)
        }
    val Vector.xxz: Vector
        get() {
            return createVector(x, x, z)
        }
    val Vector.xyx: Vector
        get() {
            return createVector(x, y, x)
        }
    val Vector.xyy: Vector
        get() {
            return createVector(x, y, y)
        }
    val Vector.xzx: Vector
        get() {
            return createVector(x, z, x)
        }
    val Vector.xzz: Vector
        get() {
            return createVector(x, z, z)
        }
    val Vector.yxx: Vector
        get() {
            return createVector(y, x, x)
        }
    val Vector.yxy: Vector
        get() {
            return createVector(y, x, y)
        }
    val Vector.yyx: Vector
        get() {
            return createVector(y, y, x)
        }
    val Vector.yyy: Vector
        get() {
            return createVector(y, y, y)
        }
    val Vector.yyz: Vector
        get() {
            return createVector(y, y, z)
        }
    val Vector.yzy: Vector
        get() {
            return createVector(y, z, y)
        }
    val Vector.yzz: Vector
        get() {
            return createVector(y, z, z)
        }
    val Vector.zxx: Vector
        get() {
            return createVector(z, x, x)
        }
    val Vector.zxz: Vector
        get() {
            return createVector(z, x, z)
        }
    val Vector.zyy: Vector
        get() {
            return createVector(z, y, y)
        }
    val Vector.zyz: Vector
        get() {
            return createVector(z, y, z)
        }
    val Vector.zzx: Vector
        get() {
            return createVector(z, z, x)
        }
    val Vector.zzy: Vector
        get() {
            return createVector(z, z, y)
        }
    val Vector.zzz: Vector
        get() {
            return createVector(z, z, z)
        }

    // Double Set
    var Vector.xy: Vector
        get() {
            return createVector(x, y)
        }
        set(other) {
            val newX = other.x;
            val newY = other.y; x = newX; y = newY
        }
    var Vector.xz: Vector
        get() {
            return createVector(x, z)
        }
        set(other) {
            val newX = other.x;
            val newZ = other.y; x = newX; z = newZ
        }
    var Vector.yx: Vector
        get() {
            return createVector(y, x)
        }
        set(other) {
            val newY = other.x;
            val newX = other.y; x = newX; y = newY
        }
    var Vector.yz: Vector
        get() {
            return createVector(y, z)
        }
        set(other) {
            val newY = other.x;
            val newZ = other.y; y = newY; z = newZ
        }
    var Vector.zx: Vector
        get() {
            return createVector(z, x)
        }
        set(other) {
            val newZ = other.x;
            val newX = other.y; x = newX; z = newZ
        }
    var Vector.zy: Vector
        get() {
            return createVector(z, y)
        }
        set(other) {
            val newZ = other.x;
            val newY = other.y; y = newY; z = newZ
        }

    // Triple Set
    var Vector.xyz: Vector
        get() {
            return createVector(x, y, z)
        }
        set(other) {
            val newX = other.x;
            val newY = other.y;
            val newZ = other.z; x = newX; y = newY; z = newZ
        }
    var Vector.xzy: Vector
        get() {
            return createVector(x, z, y)
        }
        set(other) {
            val newX = other.x;
            val newZ = other.y;
            val newY = other.z; x = newX; y = newY; z = newZ
        }
    var Vector.yxz: Vector
        get() {
            return createVector(y, x, z)
        }
        set(other) {
            val newY = other.x;
            val newX = other.y;
            val newZ = other.z; x = newX; y = newY; z = newZ
        }
    var Vector.yzx: Vector
        get() {
            return createVector(y, z, x)
        }
        set(other) {
            val newY = other.x;
            val newZ = other.y;
            val newX = other.z; x = newX; y = newY; z = newZ
        }
    var Vector.zxy: Vector
        get() {
            return createVector(z, x, y)
        }
        set(other) {
            val newZ = other.x;
            val newX = other.y;
            val newY = other.z; x = newX; y = newY; z = newZ
        }
    var Vector.zyx: Vector
        get() {
            return createVector(z, y, x)
        }
        set(other) {
            val newZ = other.x;
            val newY = other.y;
            val newX = other.z; x = newX; y = newY; z = newZ
        }

    fun Vector.toColor(): Color {
        return color(x, y, z)
    }

    fun Vector.toColor(alpha: Number): Color {
        return color(x, y, z, alpha)
    }


    operator fun Number.plus(other: Number): Double {
        @Suppress("UNUSED_VARIABLE") val t = this
        return js("t+other") as Double
    }

    operator fun Number.minus(other: Number): Double {
        @Suppress("UNUSED_VARIABLE") val t = this
        return js("t-other") as Double
    }

    operator fun Number.times(other: Number): Double {
        @Suppress("UNUSED_VARIABLE") val t = this
        return js("t*other") as Double
    }

    operator fun Number.div(other: Number): Double {
        @Suppress("UNUSED_VARIABLE") val t = this
        return js("t/other") as Double
    }

    operator fun Number.rem(other: Number): Double {
        @Suppress("UNUSED_VARIABLE") val t = this
        return js("t%other") as Double
    }

    operator fun Number.compareTo(other: Number): Int {
        @Suppress("UNUSED_VARIABLE") val dif = this - other
        return if (js("dif > 0") as Boolean) {
            1
        } else if (js("dif < 0") as Boolean) {
            -1
        } else 0
    }

    fun Number.pow(other: Number): Double {
        @Suppress("UNUSED_VARIABLE") val t = this
        return js("Math.pow(t, other)") as Double
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

    fun Color.toArray(includeAlpha: Boolean = false): Array<Number> {
        return if (includeAlpha) {
            arrayOf(
                red(this), green(this), blue(this), 255 * alpha(this)
            )
        } else {
            arrayOf(
                red(this), green(this), blue(this)
            )
        }
    }

    fun Color.toHexString(includeAlpha: Boolean = false): String {
        return "#${hex(toArray(includeAlpha), 2).joinToString("")}"
    }

    fun Color.toVector(): Vector {
        return createVector(red(this), green(this), blue(this))
    }

    fun Color.asVector(transform: Vector.()->Vector): Color {
        return transform(toVector()).toColor()
    }

    fun Color.asVector(alpha: Number, transform: Vector.()->Vector): Color {
        return transform(toVector()).toColor(alpha)
    }

    inline fun Json.setIfNotNull(propertyName: String, value: Any?) {
        if (value != null) {
            set(propertyName, value)
        }
    }

    inner class Loop(val nativeLoop: dynamic) {
        fun start(renderCallback: () -> Unit = {}) {
            nativeLoop.start(renderCallback)
        }

        val progress: Double
            get() {
                return nativeLoop.progress as Double
            }
        val theta: Double
            get() {
                return nativeLoop.theta as Double
            }

        fun noise(): Double {
            return nativeLoop.noise() as Double
        }

        fun noise1D(x: Number): Double {
            return nativeLoop.noise1D(x) as Double
        }

        fun noise2D(x: Number, y: Number): Double {
            return nativeLoop.noise1D(x, y) as Double
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
        instance: P5? = null,
        block: (Loop.() -> Unit)? = null
    ) {
        val canvas = instance?.canvasHtml ?: this@P5.canvasHtml
        val options = json(
            "canvas" to canvas,
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
            set("framesPerSecond", framesPerSecond ?: nativeP5._targetFrameRate)
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
        val nativeLoop = nativeCreateLoop(options)
        val loop = Loop(nativeLoop)
        if (autoStart) {
            loop.start {
                block?.invoke(loop)
            }
        }
    }

    object SimplexNoise {
        var simplexSeed = (kotlin.random.Random.nextDouble() * 2048.0)
        var Noise2D = OpenSimplexNoise.makeNoise2D(simplexSeed)
        var Noise3D = OpenSimplexNoise.makeNoise3D(simplexSeed)
        var Noise4D = OpenSimplexNoise.makeNoise4D(simplexSeed)
    }

    fun simplexSeed(): Double = SimplexNoise.simplexSeed
    fun simplexSeed(seed: Number) {
        SimplexNoise.simplexSeed = seed.toDouble()
        SimplexNoise.Noise2D = OpenSimplexNoise.makeNoise2D(seed)
        SimplexNoise.Noise3D = OpenSimplexNoise.makeNoise3D(seed)
        SimplexNoise.Noise4D = OpenSimplexNoise.makeNoise4D(seed)
    }

    fun simplexNoise(x: Number): Double {
        return SimplexNoise.Noise2D(x, 0)
    }

    fun simplexNoise(x: Number, y: Number): Double {
        return SimplexNoise.Noise2D(x, y)
    }

    fun simplexNoise(x: Number, y: Number, z: Number): Double {
        return SimplexNoise.Noise3D(x, y, z)
    }

    fun simplexNoise(x: Number, y: Number, u: Number, v: Number): Double {
        return SimplexNoise.Noise4D(x, y, u, v)
    }


    fun <T : Pair<Number, Number>> interpolate(vl: T? = null, v0: T, v1: T, vr: T? = null, x: Number): Double {

        val scale = 1 / (v1.first - v0.first)

        val slope0 = if (vl != null) (v1.second - vl.second) / (1 - scale * (vl.first - v0.first)) else 0
        val slope1 = if (vr != null) (vr.second - v0.second) / (scale * (vr.first - v0.first)) else 0

        val xi = scale * (x - v0.first)

        val a = 2 * v0.second - 2 * v1.second + slope0 + slope1
        val b = -3 * v0.second + 3 * v1.second - 2 * slope0 - slope1

        return ((a * xi + b) * xi + slope0) * xi + v0.second
    }

    fun <T : Pair<Number, Vector>> interpolate(vl: T? = null, v0: T, v1: T, vr: T? = null, x: Number): Vector {

        val scale = 1 / (v1.first - v0.first)

        val slope0 =
            if (vl != null) (v1.second - vl.second) / (1 - scale * (vl.first - v0.first)) else createVector(0, 0, 0)
        val slope1 =
            if (vr != null) (vr.second - v0.second) / (scale * (vr.first - v0.first)) else createVector(0, 0, 0)

        val xi = scale * (x - v0.first)

        val a = v0.second * 2 - v1.second * 2 + slope0 + slope1
        val b = v0.second * -3 + v1.second * 3 - slope0 * 2 - slope1

        return ((a * xi + b) * xi + slope0) * xi + v0.second
    }

    fun List<Pair<Number, Number>>.interpolate(x: Number): Double {

        if (isEmpty()) return 0.0
        if (size == 1) return this[0].second.toDouble()
        if (x <= this[0].first) return this[0].second.toDouble()
        if (x >= last().first) return last().second.toDouble()
        if (size == 2) return interpolate(v0 = this[0], v1 = this[1], x = x)
        if (x <= this[1].first) return interpolate(v0 = this[0], v1 = this[1], vr = this[2], x = x)
        if (x >= this[lastIndex - 1].first) return interpolate(
            vl = this[lastIndex - 2],
            v0 = this[lastIndex - 1],
            v1 = this[lastIndex],
            x = x
        )

        for (it in windowed(4)) {
            if (it[1].first <= x && x <= it[2].first) return interpolate(
                vl = it[0],
                v0 = it[1],
                v1 = it[2],
                vr = it[3],
                x = x
            )
        }

        return 0.0
    }

    fun List<Pair<Number, Vector>>.interpolate(x: Number): Vector {

        if (isEmpty()) return createVector(0, 0, 0)
        if (size == 1) return this[0].second
        if (x <= this[0].first) return this[0].second
        if (x >= last().first) return last().second
        if (size == 2) return interpolate(v0 = this[0], v1 = this[1], x = x)
        if (x <= this[1].first) return interpolate(v0 = this[0], v1 = this[1], vr = this[2], x = x)
        if (x >= this[lastIndex - 1].first) return interpolate(
            vl = this[lastIndex - 2],
            v0 = this[lastIndex - 1],
            v1 = this[lastIndex],
            x = x
        )

        for (it in windowed(4)) {
            if (it[1].first <= x && x <= it[2].first) return interpolate(
                vl = it[0],
                v0 = it[1],
                v1 = it[2],
                vr = it[3],
                x = x
            )
        }

        return createVector(0, 0, 0)
    }

    fun Map<Number, Number>.interpolate(k: Number) = toList().interpolate(k)
    fun Map<Number, Vector>.interpolate(k: Number) = toList().interpolate(k)
    fun Map<Double, Vector>.interpolate(k: Number) = toList().interpolate(k)
    fun List<Number>.interpolate(k: Number) = mapIndexed { i, n -> i to n }.interpolate(k)
    fun List<Vector>.interpolate(k: Number) = mapIndexed { i, n -> i to n }.interpolate(k)

    fun fractalNoise(x: Number, y: Number, octaves: List<Number>): Double {
        return octaves.map { noise(x * (2.pow(it)), y * (2.0.pow(it))).toDouble() }.average()
    }

    fun fractalNoise(x: Number, y: Number, octaves: List<Pair<Number, Number>>): Double {
        val weightSum = octaves.sumOf { it.second.toDouble() }
        return octaves.sumOf {
            (noise(
                x * (2.pow(it.first)),
                y * (2.0.pow(it.first))
            ) * it.second).toDouble()
        } / weightSum
    }

    fun repeatUntilNextFrame(block: () -> Unit) {
        val stopTime = millis() + 1000.0 / frameRate()
        while (millis() < stopTime) {
            block()
        }
    }

    fun <T> takeUntilNextFrame(itor: Iterator<T>) = sequence {
        val stopTime = millis() + 1000.0 / frameRate()
        while (millis() < stopTime && itor.hasNext()) {
            yield(itor.next())
        }
    }

    fun <T> Iterator<T>.takeUntilNextFrame() = sequence {
        val stopTime = millis() + 1000.0 / frameRate()
        while (millis() < stopTime && hasNext()) {
            yield(next())
        }
    }

    fun pointIterator(): Iterator<Pair<Number, Number>> {
        return iterator {
            repeat(height) { y ->
                repeat(width) { x ->
                    yield(x to y)
                }
            }
        }
    }


    fun randInt(max: Number): Int = random(max).toInt()
    fun randInt(min: Number, max: Number): Int = random(min, max).toInt()

    fun Vector.map(action: (Number) -> Number): Vector {
        return createVector(action(x), action(y), action(z))
    }

    fun List<Number>.center(): Double {
        return (fold(0 as Number) { it1, it2 -> it1 + it2 }) / size.toDouble()
    }

    fun List<Vector>.center(): Vector {
        return (fold(createVector(0, 0, 0)) { it1, it2 -> it1 + it2 }) / size.toDouble()
    }

    fun List<Number>.centerize(): List<Number> {
        val average = center()
        return map { it - average }
    }

    fun List<Vector>.centerize(): List<Vector> {
        val average = center()
        return map { it - average }
    }

    @OverloadResolutionByLambdaReturnType
    fun cache(volatile: Boolean = false, initialValue: () -> String) =
        CacheProvider(String::class, initialValue, volatile)

    fun cache(volatile: Boolean = false, initialValue: () -> Double) =
        CacheProvider(Number::class, initialValue, volatile)

    fun cache(volatile: Boolean = false, initialValue: () -> Boolean) =
        CacheProvider(Boolean::class, initialValue, volatile)

    fun cache(volatile: Boolean = false, initialValue: () -> Color) =
        CacheProvider(Color::class, initialValue, volatile)

    fun cache(volatile: Boolean = false, initialValue: () -> Vector) =
        CacheProvider(Vector::class, initialValue, volatile)

    inner class CacheProvider<T : Any>(val classType: KClass<T>, val initialValue: () -> T, val volatile: Boolean) {

        var innerVar: T? = null

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val keyString = property.name
            if (innerVar != null && !volatile) return innerVar!!
            val storedItem: T? = when (classType) {
                String::class -> getItem<String>(keyString) as T?
                Number::class -> getItem<Number>(keyString) as T?
                Boolean::class -> getItem<Boolean>(keyString) as T?
                Color::class -> getItem<Color>(keyString) as T?
                Vector::class -> getItem<Vector>(keyString) as T?
                else -> null
            }
            if (storedItem == null) {
                console.warn("Cached Value Not Found!")
                val initVal = initialValue()
                when (classType) {
                    String::class -> storeItem(keyString, initVal as String)
                    Number::class -> storeItem(keyString, initVal as Number)
                    Boolean::class -> storeItem(keyString, initVal as Boolean)
                    Color::class -> storeItem(keyString, initVal as Color)
                    Vector::class -> storeItem(keyString, initVal as Vector)
                    else -> throw IllegalStateException("Cannot cache item of type ${classType.simpleName}")
                }
                innerVar = initVal
                return initVal
            }
            innerVar = storedItem
            return storedItem
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            val keyString = property.name
            when (classType) {
                String::class -> storeItem(keyString, value as String)
                Number::class -> storeItem(keyString, value as Number)
                Boolean::class -> storeItem(keyString, value as Boolean)
                Color::class -> storeItem(keyString, value as Color)
                Vector::class -> storeItem(keyString, value as Vector)
                else -> throw IllegalStateException("Cannot cache item of type ${classType.simpleName}")
            }
            innerVar = value
        }
    }

    inline fun <reified T : @Serializable Any> cacheSerial(volatile: Boolean = false, noinline initialValue: () -> T) =
        SerialCacheProvider(typeOf<T>(), initialValue, volatile)

    inner class SerialCacheProvider<T : @Serializable Any>(
        val kType: KType,
        val initialValue: () -> T,
        val volatile: Boolean
    ) {

        var innerVar: T? = null

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val keyString = property.name
            if (!volatile) {
                innerVar.ifNotNull { return it }
            }
            val storedSerializedItem = getItem<String>(keyString)
            if (storedSerializedItem != null) {
                val storedItem: T = SerialJson.decodeFromString(kType, storedSerializedItem)
                innerVar = storedItem
                return storedItem
            }
            val initVal = initialValue()
            val serializedInitVal = SerialJson.encodeToString(kType, initVal)
            storeItem(keyString, serializedInitVal)
            innerVar = initVal
            return initVal
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            val keyString = property.name
            val serializedInitVal = SerialJson.encodeToString(kType, value)
            storeItem(keyString, serializedInitVal)
            innerVar = value
        }
    }

    infix fun Vector.cross2(other: Vector): Double {
        return x * other.y - y * other.x
    }

    fun Vector.toInts(): Vector {
        return map { it.toInt() }
    }

    fun List<Vector>.dilate(factor: Number): List<Vector> {
        val center = center()
        return map { (it - center) * factor + center }
    }

    fun List<Vector>.dilate(factor: Number, center: Vector): List<Vector> {
        return map { (it - center) * factor + center }
    }


    fun map(value: Vector, start1: Number, stop1: Number, start2: Number, stop2: Number): Vector {
        return value.map { map(it, start1, stop1, start2, stop2) }
    }

    fun map(
        value: Vector,
        start1: Number,
        stop1: Number,
        start2: Number,
        stop2: Number,
        withinBounds: Boolean
    ): Vector {
        return value.map { map(it, start1, stop1, start2, stop2, withinBounds) }
    }

    fun map(value: Vector, start1: Vector, stop1: Vector, start2: Vector, stop2: Vector): Vector {
        return createVector(
            map(value.x, start1.x, stop1.x, start2.x, stop2.x),
            map(value.y, start1.y, stop1.y, start2.y, stop2.y),
            map(value.z, start1.z, stop1.z, start2.z, stop2.z),
        )
    }

    fun map(
        value: Vector,
        start1: Vector,
        stop1: Vector,
        start2: Vector,
        stop2: Vector,
        withinBounds: Boolean
    ): Vector {
        return createVector(
            map(value.x, start1.x, stop1.x, start2.x, stop2.x, withinBounds),
            map(value.y, start1.y, stop1.y, start2.y, stop2.y, withinBounds),
            map(value.z, start1.z, stop1.z, start2.z, stop2.z, withinBounds),
        )
    }

    fun Color.toGrayscale(): Color {
        return color((red(this) + green(this) + blue(this)) / 3.0)
    }

    operator fun Number.unaryMinus(): Double {
        return -1 * this
    }

    fun forceDown(it: Number): Double {
        return floor(it.toDouble())
    }

    fun forceUp(it: Number): Double {
        return floor(it.toDouble()) + 1.0
    }

    fun Number.toVector(): Vector {
        return createVector() + this
    }


    @OverloadResolutionByLambdaReturnType
    fun url(default: () -> String) = UrlParamProvider(String::class, default)
    fun url(default: () -> Double) = UrlParamProvider(Double::class, default)
    fun url(default: () -> Int) = UrlParamProvider(Int::class, default)
    fun url(default: () -> Boolean) = UrlParamProvider(Boolean::class, default)

    inner class UrlParamProvider<T : Any>(val classType: KClass<T>, val default: () -> T) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val keyString = property.name
            val urlParamString = getURLParam(keyString)
            return when (classType) {
                String::class -> urlParamString as T?
                Double::class -> urlParamString?.toDoubleOrNull() as T?
                Int::class -> urlParamString?.toIntOrNull() as T?
                Boolean::class -> urlParamString?.toBooleanStrictOrNull() as T?
                else -> {
                    console.warn("Url Param Type ${classType::simpleName} is not supported")
                    null
                }
            } ?: default()
        }
    }

    fun urlString() = NullableUrlParamProvider(String::class)
    fun urlDouble() = NullableUrlParamProvider(Double::class)
    fun urlInt() = NullableUrlParamProvider(Int::class)
    fun urlBoolean() = NullableUrlParamProvider(Boolean::class)

    inner class NullableUrlParamProvider<T : Any>(val classType: KClass<T>) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            val keyString = property.name
            val urlParamString = getURLParam(keyString)
            return when (classType) {
                String::class -> urlParamString as T?
                Double::class -> urlParamString?.toDoubleOrNull() as T?
                Int::class -> urlParamString?.toIntOrNull() as T?
                Boolean::class -> urlParamString?.toBooleanStrictOrNull() as T?
                else -> {
                    console.warn("Url Param Type ${classType::simpleName} is not supported")
                    null
                }
            }
        }
    }

    object VectorSerializer : KSerializer<Vector> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Vector") {
            element<Double>("x")
            element<Double>("y")
            element<Double>("z")
        }

        override fun deserialize(decoder: Decoder): Vector {
            val jsonInput = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")
            val json = jsonInput.decodeJsonElement().jsonObject
            val x = json.getValue("x").jsonPrimitive.double
            val y = json.getValue("y").jsonPrimitive.double
            val z = json.getValue("z").jsonPrimitive.double
            return Vector(x, y, z)
        }

        override fun serialize(encoder: Encoder, value: Vector) {
            require(encoder is JsonEncoder)
            val element = buildJsonObject {
                put("x", value.x)
                put("y", value.y)
                put("z", value.z)
            }
            encoder.encodeJsonElement(element)
        }

    }

    fun Double.deadzone(radius: Double): Double {
        if (this in -radius..radius) {
            return 0.0
        }
        return sign(this) * sqrt(this * this - radius * radius)
    }

    fun Vector.rotate(around: Vector, angle: Double): Vector {
        val v = this
        val k = around.normalized()
        val c = cos(angle)
        val s = sin(angle)
        return v * c + (k cross v) * s + (k dot v) * (1.0 - c)
    }


    fun createLinearGradient(x0: Number, y0: Number, x1: Number, y1: Number): Gradient {
        return Gradient(drawingContext.createLinearGradient(x0, y0, x1, y1))
    }

    fun createLinearGradient(xy0: Vector, xy1: Vector): Gradient {
        return Gradient(drawingContext.createLinearGradient(xy0.x, xy0.y, xy1.x, xy1.y))
    }

    fun createRadialGradient(x0: Number, y0: Number, r0: Number, x1: Number, y1: Number, r1: Number): Gradient {
        return Gradient(drawingContext.createRadialGradient(x0, y0, r0, x1, y1, r1))
    }

    fun createRadialGradient(xy0: Vector, r0: Number, xy1: Vector, r1: Number): Gradient {
        return Gradient(drawingContext.createRadialGradient(xy0.x, xy0.y, r0, xy1.x, xy1.y, r1))
    }

    fun createConicGradient(startAngle: Number, x: Number, y: Number): Gradient {
        return Gradient(drawingContext.createConicGradient(startAngle, x, y))
    }

    fun createConicGradient(startAngle: Number, xy: Vector): Gradient {
        return Gradient(drawingContext.createConicGradient(startAngle, xy.x, xy.y))
    }

    var fillStyle = FillStyle.SOLID

    var themeColor = color(255)
    var colorFills = 0.0

    fun updateAverageFillColor(newColor: Color) {
        themeColor = lerpColor(newColor, themeColor, colorFills/(colorFills+1))
        colorFills++
    }

    private fun resetFillStyle() {
        if (fillStyle != FillStyle.SOLID) {
            drawingContext.fillStyle = "rgba(0,0,0,0)"
            fillStyle = FillStyle.SOLID
        }
    }

    fun fill(gray: Number) {
        updateAverageFillColor(color(gray))
        resetFillStyle()
        nativeP5.fill(gray)
    }

    fun fill(gray: Number, alpha: Number) {
        updateAverageFillColor(color(gray, alpha))
        resetFillStyle()
        nativeP5.fill(gray, alpha)
    }

    fun fill(v1: Number, v2: Number, v3: Number) {
        updateAverageFillColor(color(v2, v2, v3))
        resetFillStyle()
        nativeP5.fill(v1, v2, v3)
    }

    fun fill(v1: Number, v2: Number, v3: Number, alpha: Number) {
        updateAverageFillColor(color(v1, v2, v3, alpha))
        resetFillStyle()
        nativeP5.fill(v1, v2, v3, alpha)
    }

    fun fill(colorString: String) {
        updateAverageFillColor(color(colorString))
        resetFillStyle()
        nativeP5.fill(colorString)
    }

    fun fill(colorArray: Array<Number>) {
        updateAverageFillColor(color(colorArray))
        resetFillStyle()
        nativeP5.fill(colorArray)
    }

    fun fill(color: Color) {
        updateAverageFillColor(color)
        resetFillStyle()
        nativeP5.fill(color.nativeColor)
    }

    inner class Gradient(val nativeGradient: dynamic) {
        fun addColorStop(offset: Number, color: Color) {
            nativeGradient.addColorStop(offset, color.toString())
        }
    }

    fun fill(gradient: Gradient) {
        drawingContext.fillStyle = gradient.nativeGradient
        fillStyle = FillStyle.GRADIENT
    }

    val maxRed: Double get() = (nativeP5._colorMaxes.rgb[0] as Number).toDouble()
    val maxGreen: Double get() = (nativeP5._colorMaxes.rgb[1] as Number).toDouble()
    val maxBlue: Double get() = (nativeP5._colorMaxes.rgb[2] as Number).toDouble()
    val maxAlpha: Double get() = (nativeP5._colorMaxes.rgb[3] as Number).toDouble()

    fun createSlider(min: Number, max: Number, cache: Boolean): Slider {
        return createSlider(min, max).apply {
            if (cache) {
                getFromCache = true
                changed { println("storing", name!!, value()) }
            }
        }
    }

    fun createSlider(min: Number, max: Number, defaultValue: Number, cache: Boolean): Slider {
        return createSlider(min, max).apply {
            if (cache) {
                getFromCache = true
                changed { storeItem(name!!, value()) }
            }
        }
    }

    fun createSlider(min: Number, max: Number, defaultValue: Number, step: Number, cache: Boolean): Slider {
        return createSlider(min, max, defaultValue, step).apply {
            if (cache) {
                getFromCache = true
                changed { storeItem(name!!, value()) }
            }
        }
    }

    val itemContainers: MutableMap<Element, Div> = mutableMapOf()

    var treeDepth = 0
    var divUUID = 0

    sealed class StyleData(val property: String) {
        var inheritable = false
        var active = false
        class Style(property: String, val value: String): StyleData(property)
        class Size(val width: Number, val height: Number, val zoom: Number? = null): StyleData("size")

        fun copy(): StyleData {
            return when(this) {
                is Style -> Style(property, value)
                is Size -> Size(width, height, zoom)
            }.also { it.inheritable = inheritable; it.active = active }
        }
    }

    class StyleBuilder {
        val styles = mutableListOf<StyleData>()
        fun style(property: String, value: String) {
            styles.add(StyleData.Style(property, value))
        }
        fun size(width: Number, height: Number) {
            styles.add(StyleData.Size(width, height))
        }
        fun size(width: Number, height: Number, zoom: Number) {
            styles.add(StyleData.Size(width, height, zoom))
        }
    }
    fun buildStyle(inheritable: Boolean, active: Boolean, builder: StyleBuilder.()->Unit): MutableList<StyleData> {
        return StyleBuilder().also { builder(it) }.styles.onEach {
            it.inheritable = inheritable
            it.active = active
        }
    }
    fun Element.applyStyles(styles: List<StyleData>) {
        styles.filter {it.active}.forEach {
            when (it) {
                is StyleData.Style -> style(it.property, it.value)
                is StyleData.Size -> if (it.zoom == null) {
                    size(it.width, it.height)
                } else {
                    size(it.width, it.height, it.zoom)
                }
            }
        }
    }

    data class LayoutStyleModifier(
        val gridStyles:  MutableList<StyleData> = mutableListOf(),
        val itemStyles:  MutableList<StyleData> = mutableListOf()
    ) {
        fun subgridModifier(): LayoutStyleModifier {
            return LayoutStyleModifier(
                gridStyles.filter { it.inheritable }.toMutableList(),
                itemStyles.filter { it.inheritable }.toMutableList()
            ).copy().activate()
        }

        fun activate(): LayoutStyleModifier {
            gridStyles.onEach  { it.active = true }
            itemStyles.onEach  { it.active = true }
            return this
        }

        fun copy(): LayoutStyleModifier {
            return LayoutStyleModifier(
                gridStyles.map  { it.copy() }.toMutableList(),
                itemStyles.map  { it.copy() }.toMutableList()
            )
        }

        fun add(newModifier: LayoutStyleModifier) {
            gridStyles.addAll(newModifier.gridStyles)
            itemStyles.addAll(newModifier.itemStyles)
        }
    }

    abstract inner class LayoutNode {
        val divId = divUUID++
        open val logName: String = "Abstract Layout Node"
        val container = createDiv("").apply {
            id("$divId")
        }
        var modifier: LayoutStyleModifier = LayoutStyleModifier()
        open fun render(propagate: Boolean) {}
        open fun render() = render(true)
        abstract fun inheritModifier(parentModifier: LayoutStyleModifier)
        abstract fun clear()
        open fun delete() {
            clear()
            container.html("")
            container.remove()
        }
        fun update() {
            clear()
            render()
        }
        operator fun invoke() = update()
    }


    inner class LayoutItem(val element: Element): LayoutNode() {
        override val logName = "Layout Item"

        override fun inheritModifier(parentModifier: LayoutStyleModifier) {
            modifier.add(parentModifier.copy().activate())
        }

        override fun clear() {
            element.hide()
        }

        override fun render(propagate: Boolean) {
            super.render(propagate)
            container.child(element)
            container.apply { applyStyles(modifier.itemStyles) }
            element.apply {
                if (this !is Renderer) {
                    style("width", "100%")
                    style("height", "100%")
                }
                show()
            }
        }
    }

    abstract inner class Grid: LayoutNode() {
        override val logName = "Abstract Grid"

        val children = mutableListOf<LayoutNode>()
        open fun add(child: LayoutNode, localStyles: StyleBuilder.()->Unit = {}) {
            container.child(child.container)
            child.inheritModifier(modifier)
            child.modifier.itemStyles.addAll(buildStyle(false, true, localStyles))
            children.add(child)
        }

        fun add(childElement: Element, localStyles: StyleBuilder.()->Unit = {}) {
            add(LayoutItem(childElement), localStyles)
        }

        fun add(p5: P5, localStyles: StyleBuilder.()->Unit = {}) {
            add(p5.getLayout(), localStyles)
        }

        fun add(sketch: Sketch, localStyles: StyleBuilder.()->Unit = {}) {
            add(sketch.p5.getLayout(), localStyles)
        }

        fun addAll(children: List<LayoutNode>, localStyles: StyleBuilder.()->Unit = {}) {
            children.forEach { add(it, localStyles) }
        }

        fun addAll(children: List<Element>, localStyles: StyleBuilder.()->Unit = {}) {
            children.forEach { add(it, localStyles) }
        }

        fun addAll(children: List<P5>, localStyles: StyleBuilder.()->Unit = {}) {
            children.forEach { add(it, localStyles) }
        }

        fun addAll(children: List<Sketch>, localStyles: StyleBuilder.()->Unit = {}) {
            children.forEach { add(it, localStyles) }
        }

        fun addAll(children: Array<LayoutNode>, localStyles: StyleBuilder.()->Unit = {}) {
            children.forEach { add(it, localStyles) }
        }

        fun addAll(children: Array<Element>, localStyles: StyleBuilder.()->Unit = {}) {
            children.forEach { add(it, localStyles) }
        }

        fun addAll(children: Array<P5>, localStyles: StyleBuilder.()->Unit = {}) {
            children.forEach { add(it, localStyles) }
        }

        fun addAll(children: Array<Sketch>, localStyles: StyleBuilder.()->Unit = {}) {
            children.forEach { add(it, localStyles) }
        }

        var layoutCallback: Grid.()->Unit = {}

        fun Row(callback: Grid.()->Unit): Grid {
            val childRow = GridRow()
            childRow.layoutCallback = callback
            add(childRow)
            return childRow
        }

        fun Column(callback: Grid.()->Unit): Grid {
            val childColumn = GridColumn()
            childColumn.layoutCallback = callback
            add(childColumn)
            return childColumn
        }

        fun Stack(callback: Grid.()->Unit): Grid {
            val childStack = GridStack()
            childStack.layoutCallback = callback
            add(childStack)
            return childStack
        }

        init {
            modifier.gridStyles.addAll( buildStyle(false, true) {
                style("display", "grid")
                style("grid-auto-column", "min-content")
                style("grid-auto-row", "min-content")
                style("align-items", "start")
                style("justify-items", "start")
            } )
        }

        override fun inheritModifier(parentModifier: LayoutStyleModifier) {
            modifier.add(parentModifier.subgridModifier())
        }

        override fun delete() {
            super.delete()
            children.forEach { it.delete() }
        }

        override fun clear() {
            children.forEach { it.delete() }
            children.clear()
        }

        override fun render(propagate: Boolean) {
            super.render(propagate)
            container.apply {
                if(propagate) layoutCallback()
                applyStyles(modifier.gridStyles)
                applyStyles(modifier.itemStyles)
                if(propagate) {
                    treeDepth++
                    children.forEach { it.render() }
                    treeDepth--
                }
            }
        }

        fun GridStyle(inherit: Boolean = true, block: StyleBuilder.()->Unit) {
            modifier.gridStyles.addAll(buildStyle(inherit, true, block))
        }

        fun ItemStyle(inherit: Boolean = true, block: StyleBuilder.()->Unit) {
            modifier.itemStyles.addAll(buildStyle(inherit, false, block))
        }
    }

    inner class GridRow: Grid() {
        override val logName = "Grid Row"

        init {
            modifier.gridStyles.add(StyleData.Style("grid-auto-flow", "column").apply { active = true })
        }
    }
    inner class GridColumn: Grid() {
        override val logName = "Grid Column"

        init {
            modifier.gridStyles.add(StyleData.Style("grid-auto-flow", "row").apply { active = true })
        }
    }
    inner class GridStack: Grid() {
        override val logName = "Grid Stack"

        override fun add(child: LayoutNode, localStyles: StyleBuilder.()->Unit) {
            container.child(child.container)
            val childModifier = modifier.copy()
            childModifier.itemStyles.addAll(buildStyle(false, true) {
                style("grid-row", "1")
                style("grid-column", "1")
            })
            child.inheritModifier(childModifier)
            child.modifier.itemStyles.addAll(buildStyle(false, true, localStyles))
            children.add(child)
        }
    }

    var layout: Grid? = null
    fun getLayout(): Grid {
        if(layout == null) {
            makeLayout {
                add(getCanvas())
            }
        }
        return layout ?: error("Error loading default layout")
    }
    fun makeLayout(block: P5.Grid.()->Unit): Grid {
        val grid = layout ?: GridStack()
        grid.layoutCallback = block
        layout = grid
        grid.update()
        return grid
    }

    fun Number.px(): String {
        return "${this}px "
    }

    fun Number.percent(): String {
        return "${this}% "
    }

    fun Number.fr(): String {
        return "${this}fr "
    }

    fun NativeElement.style(property: String, value: Number) {
        style(property, value.toString())
    }

    fun targetFrameTime() = 1000.0/targetFrameRate
    fun frameLagTime() = deltaTime - targetFrameTime()
    fun frameLagFactor() = deltaTime/targetFrameTime()

    fun timeit(block: ()->Unit): Double {
        val before = millis()
        block()
        return (millis() - before).toDouble()
    }

    fun Vector.inFrame(): Boolean = (x.toInt() in 0 until width && y.toInt() in 0 until height)

    fun Color.isOpaque(): Boolean = alpha(this) == maxAlpha
    fun Color.isTransparent(): Boolean = !isOpaque()

    fun getBody(): Element = select("body") ?: error("No Body Element Found!")

    fun getDataURL(): String = canvasHtml.toDataURL()
    fun takeImg(altText: String): Element = createImg(getDataURL(), altText)

    fun Slider.setScrollable() {
        mouseWheel {
            println("Mouse Wheel!", it.deltaY)
            if(it.deltaY < 0) {
                value(value()+(step ?: 1))
            } else {
                value(value()-(step ?: 1))
            }
        }
    }

    fun isMouseOver(element: Element): Boolean {
        return element.isMouseOver
    }

    fun dist(d1: Double, d2: Double): Double {
        return abs(d1-d2)
    }

    fun chooseFile(callback: (File)->Unit) {
        fileChooserCallback = callback
        if(fileChooser == null) {
            fileChooser = createFileInput {
                fileChooserCallback(it)
            }.apply {
                style("display", "none")
                attribute("id", "hiddenFileInput")
            }
        }
        js("document.getElementById('hiddenFileInput').click()")
    }

    fun pickColor(openAt: Element? = null, callback: (Color)->Unit) {
        colorPickerCallback = callback
        if(colorPicker == null) {
            colorPicker = createColorPicker(lastColor ?: color(0, 0)).apply {
                attribute("id", "hiddenColorPicker")
                style("opacity", "0")
                input {
                    colorPickerCallback(this.color())
                }
            }
            colorPickerDiv = createDiv("").apply {
                child(colorPicker!!)
                style("width", "0px")
                style("height", "0px")
            }
        }
        if(openAt == null) {
            colorPickerDiv?.position(absMouseX, absMouseY)
        } else {
            val e = openAt.nativeElement
            console.log(e)
            val rect = js("e.getBoundingClientRect()")
            colorPickerDiv?.position(rect.left, rect.bottom)
        }
        js("document.getElementById('hiddenColorPicker').focus()")
        js("document.getElementById('hiddenColorPicker').click()")
    }

    fun shaderFold(image: Image, webGLIndex: Int, foldFunction: ShaderScope.(c: vec4, n: float, acc: vec4)->vec4): Color {

        val webGLCore = getWebGLCore(webGLIndex)
        val webGLRenderer = webGLCore.renderer
        var foldRows = false
        var shaderImage = image

        val foldShader = webGLRenderer.buildShader {
            Fragment {
                val img by UniformImage { shaderImage }
                val res by Uniform<vec2> { arrayOf(image.width, image.height) }
                val foldRows by UniformBool { foldRows }

                val fold by buildFunction { color: vec4, count: float, acc: vec4 ->
                    foldFunction(color, count, acc)
                }

                Main {
                    val uv by ivec2(int(floor(it.x)), int(res.y - floor(it.y) - 1.0))
                    var acc by vec4(0)
                    var count by float(0)

                    IF(foldRows) {
                        FOR(int(res.x)) { x ->
                            val color by texelFetch(img, ivec2(x, uv.y), int(0))
                            acc = fold(color, count, acc)
                            count += 1.0
                        }
                    } ELSE {
                        FOR(int(res.y)) { y ->
                            val color by texelFetch(img, ivec2(uv.x, y), int(0))
                            acc = fold(color, count, acc)
                            count += 1.0
                        }
                    }

                    acc
                }
            }
        }

        shaderImage = webGLCore.render(foldShader, width, height)
        foldRows = false

        shaderImage = webGLCore.render(foldShader, width, height)

        var result = color(0)
        shaderImage.withPixels {
            result = colorArray[0, 0]
        }

        return result
    }




    companion object {
        var fileChooser: Element? = null
        var fileChooserCallback: (File)->Unit = {}
        var colorPickerDiv: Div? = null
        var colorPicker: ColorPicker? = null
        var colorPickerCallback: (Color)->Unit = {}
        var lastColor: Color? = null
    }

}