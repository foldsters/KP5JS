@file:Suppress("unused", "UNUSED_PARAMETER", "CanBeParameter", "UNSUPPORTED_FEATURE")

package p5.core

import p5.native.*
import p5.util.arrayMap
import p5.util.println
import p5.util.setTimeout
import kotlin.js.Json
import kotlin.js.RegExp
import kotlin.reflect.KProperty

//context(P5)
open class Element(val nativeElement: NativeElement) {
    constructor(elt: String): this(NativeElement(elt))

    fun parent(): Element = Element(nativeElement.parent().toString())
    fun parent(parentString: String) = nativeElement.parent(parentString)
    fun parent(parentElement: Element) = nativeElement.parent(parentElement.nativeElement)
    fun id(): String = nativeElement.id()
    fun id(idString: String) = nativeElement.id(idString)
    fun addClass(classString: String) = nativeElement.addClass(classString)
    fun removeClass(classString: String) = nativeElement.removeClass(classString)
    fun hasClass(classString: String): Boolean = nativeElement.hasClass(classString)
    fun toggleClass(classString: String) = nativeElement.toggleClass(classString)
    fun styleClass(): String = nativeElement.styleClass()
    fun styleClass(classString: String) = nativeElement.styleClass(classString)
    fun child() = nativeElement.child()
    fun child(classString: String) = nativeElement.child(classString)
    fun child(classElement: Element) = nativeElement.child(classElement.nativeElement)
    fun center() = nativeElement.center()
    fun html(): String = nativeElement.html()
    fun html(htmlString: String) = nativeElement.html(htmlString)
    fun html(htmlString: String, append: Boolean) = nativeElement.html(htmlString, append)
    fun position(): dynamic = nativeElement.position() // TODO: Remove Dynamic
    fun position(x: Number, y: Number) = nativeElement.position(x, y)
    fun style(property: String): String? = nativeElement.style(property)
    fun style(property: String, value: String) = nativeElement.style(property, value)
    fun attribute(attr: String): String = nativeElement.attribute(attr)
    fun attribute(attr: String, value: String) = nativeElement.attribute(attr, value)
    fun removeAttribute(attr: String) = nativeElement.removeAttribute(attr)
    fun show() = nativeElement.show()
    fun hide() = nativeElement.hide()
    fun size(): dynamic = nativeElement.size() // TODO: Returns Object, Remove Dynamic
    fun size(w: Number) = nativeElement.size(w)
    fun size(w: Number, h: Number) = nativeElement.size(w, h)
    fun remove() = nativeElement.remove()
    fun drop(callback: (File)->Unit) = nativeElement.drop { nativeFile -> callback(File(nativeFile)) }
    fun drop(callback: (File)->Unit, onDrop: ()->Unit) = nativeElement.drop({ nativeFile -> callback(File(nativeFile)) }, onDrop)
    fun center(alignMode: AlignMode) = nativeElement.center(alignMode.nativeValue)
    fun position(x: Number, y: Number, positionMode: PositionMode) = nativeElement.position(x, y, positionMode.nativeValue)
    fun size(w: AUTO) = nativeElement.size("auto")
    fun size(w: AUTO, h: Number) = nativeElement.size("auto", h)
    fun size(w: Number, h: AUTO) = nativeElement.size(w, "auto")

    // Smarter Event Handler
    private class ActionHandler<D: Any> {
        val actions = mutableListOf<EventAction<D>>()
        fun addEvent(callback: (D)->Unit): EventAction<D> {
            val action = EventAction(callback)
            actions.add(action)
            action.remove = {
                action.remove = null
                actions.remove(action)
            }
            return action
        }
        fun clear() {
            actions.forEach { it.remove = null }
            actions.clear()
        }
        fun trigger(eventData: D) = actions.forEach {
            it.callback(eventData)
        }
    }

    private val mousePressedHandler  = ActionHandler<Unit>()
    private val doubleClickedHandler = ActionHandler<MouseEvent>()
    private val mouseReleasedHandler = ActionHandler<MouseEvent>()
    private val mouseWheelHandler    = ActionHandler<WheelEvent>()
    private val mouseClickedHandler  = ActionHandler<PointerEvent>()
    private val mouseMovedHandler    = ActionHandler<MouseEvent>()
    private val mouseOverHandler     = ActionHandler<MouseEvent>()
    private val mouseOutHandler      = ActionHandler<MouseEvent>()
    private val touchStartedHandler  = ActionHandler<dynamic>()
    private val touchMovedHandler    = ActionHandler<dynamic>()
    private val touchEndedHandler    = ActionHandler<dynamic>()
    private val dragOverHandler      = ActionHandler<DragEvent>()
    private val dragLeaveHandler     = ActionHandler<DragEvent>()
    private val changedHandler       = ActionHandler<dynamic>()
    private val inputHandler         = ActionHandler<dynamic>()

    fun mousePressed(callback: (Unit)->Unit) = mousePressedHandler.addEvent(callback)
    fun doubleClicked(callback: (MouseEvent)->Unit) = doubleClickedHandler.addEvent(callback)
    fun mouseReleased(callback: (MouseEvent)->Unit) = mouseReleasedHandler.addEvent(callback)
    fun mouseWheel(callback: (WheelEvent)->Unit) = mouseWheelHandler.addEvent(callback)
    fun mouseClicked(callback: (PointerEvent)->Unit) = mouseClickedHandler.addEvent(callback)
    fun mouseMoved(callback: (MouseEvent)->Unit) = mouseMovedHandler.addEvent(callback)
    fun mouseOver(callback: (MouseEvent)->Unit) = mouseOverHandler.addEvent(callback)
    fun mouseOut(callback: (MouseEvent)->Unit) = mouseOutHandler.addEvent(callback)
    fun touchStarted(callback: (dynamic)->Unit) = touchStartedHandler.addEvent(callback)
    fun touchMoved(callback: (dynamic)->Unit) = touchMovedHandler.addEvent(callback)
    fun touchEnded(callback: (dynamic)->Unit) = touchEndedHandler.addEvent(callback)
    fun dragOver(callback: (DragEvent)->Unit) = dragOverHandler.addEvent(callback)
    fun dragLeave(callback: (DragEvent)->Unit) = dragLeaveHandler.addEvent(callback)
    fun changed(callback: (Event)->Unit) = changedHandler.addEvent(callback)
    fun input(callback: (Event)->Unit) = inputHandler.addEvent(callback)

    fun clearMousePressed() = mousePressedHandler.clear()
    fun clearDoubleClicked() = doubleClickedHandler.clear()
    fun clearMouseReleased() = mouseReleasedHandler.clear()
    fun clearMouseWheel() = mouseWheelHandler.clear()
    fun clearMouseClicked() = mouseClickedHandler.clear()
    fun clearMouseMoved() = mouseMovedHandler.clear()
    fun clearMouseOver() = mouseOverHandler.clear()
    fun clearMouseOut() = mouseOutHandler.clear()
    fun clearTouchStarted() = touchStartedHandler.clear()
    fun clearTouchMoved() = touchMovedHandler.clear()
    fun clearTouchEnded() = touchEndedHandler.clear()
    fun clearDragOver() = dragOverHandler.clear()
    fun clearDragLeave() = dragLeaveHandler.clear()
    fun clearChanged() = changedHandler.clear() 
    fun clearInput() = inputHandler.clear()

    var isMouseOver: Boolean = false
    var isClicked: Boolean = false
    var isTouched: Boolean = false
    var isDraggedOver: Boolean = false

    init {
        with(nativeElement) {
            mousePressed {
                isClicked = true
                mousePressedHandler.trigger(Unit)
            }
            doubleClicked { doubleClickedHandler.trigger(MouseEvent(it)) }
            mouseReleased {
                isClicked = false
                mouseReleasedHandler.trigger(MouseEvent(it))
            }
            mouseWheel { mouseWheelHandler.trigger(WheelEvent(it)) }
            mouseClicked { mouseClickedHandler.trigger(PointerEvent(it)) }
            mouseMoved { mouseMovedHandler.trigger(MouseEvent(it)) }
            mouseOver {
                isMouseOver = true
                mouseOverHandler.trigger(MouseEvent(it))
            }
            mouseOut {
                isMouseOver = false
                mouseOutHandler.trigger(MouseEvent(it))
            }
            touchStarted {
                isTouched = true
                console.log("touchStarted", it)
                touchStartedHandler.trigger(it)
            }
            touchMoved {
                console.log("touchMoved", it)
                touchMovedHandler.trigger(it)
            }
            touchEnded {
                isTouched = false
                console.log("touchEnded", it)
                touchEndedHandler.trigger(it)
            }
            dragOver {
                isDraggedOver = true
                dragOverHandler.trigger(DragEvent(it))
            }
            dragLeave {
                isDraggedOver = false
                dragLeaveHandler.trigger(DragEvent(it))
            }
            changed {
                changedHandler.trigger(it)
            }
            input {
                inputHandler.trigger(it)
            }
        }
    }

    fun mouseOverDelay(delayMillis: Number, block: () -> Unit): EventAction<MouseEvent> {
        return mouseOver {
            setTimeout(delayMillis) {
                if(isMouseOver) {
                    block()
                }
            }
        }
    }

    var name: String? = null
    var getFromCache = false

    fun size(w: Number, h: Number, scale: Number) {
        size(w.toDouble()/scale.toDouble(), h.toDouble()/scale.toDouble())
        style("zoom", "${scale.toDouble()*100.0}%")
    }

    fun size(w: AUTO, h: Number, scale: Number) {
        size(w, h.toDouble()/scale.toDouble())
        style("zoom", "${scale.toDouble()*100.0}%")
    }

    fun size(w: Number, h: AUTO, scale: Number) {
        size(w.toDouble()/scale.toDouble(), h)
        style("zoom", "${scale.toDouble()*100.0}%")
    }

    fun style(property: String, value: Number) = style(property, value.toString())
}

open class ValueElement<T>(nativeElement: NativeElement): Element(nativeElement) {
    fun value(): T = nativeElement.value() as T
    fun value(v: T) = nativeElement.value(v)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        name = property.name
        return value()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        name = property.name
        value(value)
    }
}

class Color(val nativeColor: NativeColor) {
    override fun toString(): String = nativeColor.toString()
    fun setRed(red: Number) = nativeColor.setRed(red)
    fun setGreen(green: Number) = nativeColor.setGreen(green)
    fun setBlue(blue: Number) = nativeColor.setBlue(blue)
    fun setAlpha(alpha: Number) = nativeColor.setAlpha(alpha)
    override fun equals(other: Any?) =
        toString() == other.toString()
}

class Checkbox(val nativeCheckbox: NativeCheckbox): ValueElement<Boolean>(nativeCheckbox) {
    fun checked(): Boolean = nativeCheckbox.checked()
}

class Select(val nativeSelect: NativeSelect): ValueElement<String>(nativeSelect) {
    fun option(name: String) = nativeSelect.option(name)
    fun option(name: String, value: String) = nativeSelect.option(name, value)
    fun selected(): Element = Element(nativeSelect.selected())
    fun selected(value: String) = nativeSelect.selected(value)
    fun disable() = nativeSelect.disable()
    fun disable(value: String) = nativeSelect.disable(value)
}

class Radio(val nativeRadio: NativeRadio): ValueElement<String>(nativeRadio) {
    fun option(name: String) = nativeRadio.option(name)
    fun option(name: String, value: String) = nativeRadio.option(name, value)
    fun selected(): Element = Element(nativeRadio.selected())
    fun selected(value: String) = nativeRadio.selected(value)
    fun disable(value: Boolean) = nativeRadio.disable(value)
}

class ColorPicker(val nativeColorPicker: NativeColorPicker) {
    fun color(): Color = Color(nativeColorPicker.color())
}

class MediaElement(val nativeMediaElement: NativeMediaElement): Element(nativeMediaElement) {
    val src: String by nativeMediaElement::src
    fun play() = nativeMediaElement.play()
    fun stop() = nativeMediaElement.stop()
    fun pause() = nativeMediaElement.pause()
    fun loop() = nativeMediaElement.loop()
    fun noLoop() = nativeMediaElement.noLoop()
    fun autoplay(shouldAutoplay: Boolean) = nativeMediaElement.autoplay(shouldAutoplay)
    fun volume(): Double = nativeMediaElement.volume()
    fun volume(value: Number) = nativeMediaElement.volume(value)
    fun speed(): Double = nativeMediaElement.speed()
    fun speed(multiplier: Number) = nativeMediaElement.speed(multiplier)
    fun time(): Double = nativeMediaElement.time()
    fun time(timeSeconds: Number) = nativeMediaElement.time(timeSeconds)
    fun duration(): Double = nativeMediaElement.duration()
    fun onended(callback: (MediaElement)->Unit) = nativeMediaElement.onended { callback(MediaElement(it)) }
    // fun connect() // TODO: Implement when integrating p5.sound
    fun disconnect() = nativeMediaElement.disconnect()
    fun showControls() = nativeMediaElement.showControls()
    fun hideControls() = nativeMediaElement.hideControls()
    fun addCue(time: Number, callback: (Double)->Unit): Double = nativeMediaElement.addCue(time, callback)
    fun <T> addCue(time: Number, callback: (Double, T)->Unit, value: T): Double = nativeMediaElement.addCue(time, callback, value)
    fun removeCue(id: Number) = nativeMediaElement.removeCue(id)
    fun clearCues() = nativeMediaElement.clearCues()
    fun get(): Image = Image(nativeMediaElement.get())
}

class File(val nativeFile: NativeFile) {
    val file: dynamic by nativeFile::file
    val type: String by nativeFile::type
    val subtype: String by nativeFile::subtype
    val name: String by nativeFile::name
    val size: dynamic by nativeFile::size// TODO: Figure out what type this really is
    val data: String by nativeFile::data
}

class Image(val nativeImage: NativeImage) {
    val width: Double by nativeImage::width
    val height: Double by nativeImage::height
    val pixels: Array<Int> by nativeImage::pixels
    fun loadPixels() = nativeImage.loadPixels()
    fun updatePixels() = nativeImage.updatePixels()
    fun get(): Image = Image(nativeImage.get())
    fun get(x: Number, y: Number): Image = Image(nativeImage.get(x, y))
    fun get(x: Number, y: Number, w: Number, h: Number): Image = Image(nativeImage.get(x, y, w, h))
    fun set(x: Number, y: Number, a: Number) = nativeImage.set(x, y, a)
    fun set(x: Number, y: Number, a: Array<Number>) = nativeImage.set(x, y, a)
    fun set(x: Number, y: Number, a: Color) = nativeImage.set(x, y, a.nativeColor)
    fun set(x: Number, y: Number, a: Image) = nativeImage.set(x, y, a.nativeImage)
    fun resize(width: Number, height: Number) = nativeImage.resize(width, height)
    fun copy(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int) = nativeImage.copy(sx, sy, sw, sh, dx, dy, dw, dh)
    fun copy(srcImage: Image, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int) =
        nativeImage.copy(srcImage.nativeImage, sx, sy, sw, sh, dx, dy, dw, dh)
    fun mask(srcImage: Image) = nativeImage.mask(srcImage.nativeImage)
    fun filter(filterType: FilterMode) = nativeImage.filter(filterType.nativeValue)
    fun filter(filterType: FilterMode, filterParam: Number) = nativeImage.filter(filterType.nativeValue, filterParam)
    fun blend(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: BlendMode) =
        nativeImage.blend(sx, sy, sw, sh, dx, dy, dw, dh, blendMode.nativeValue)
    fun blend(srcImage: Image, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: BlendMode) =
        nativeImage.blend(srcImage.nativeImage, sx, sy, sw, sh, dx, dy, dw, dh, blendMode.nativeValue)
    fun save(filename: String, extension: String) = nativeImage.save(filename, extension)
    fun reset() = nativeImage.reset()
    fun getCurrentFrame(): Int = nativeImage.getCurrentFrame()
    fun setFrame(index: Int) = nativeImage.setFrame(index)
    fun numFrames(): Int = nativeImage.numFrames()
    fun play() = nativeImage.play()
    fun pause() = nativeImage.pause()
    fun delay(d: Number) = nativeImage.delay(d)
    fun delay(d: Number, index: Number) = nativeImage.delay(d, index)
}

class Font(val nativeFont: NativeFont) {
    val font: dynamic by nativeFont::font // TODO: Remove Dynamic
    fun textBounds(text: String, x: Number, y: Number): dynamic = nativeFont.textBounds(text, x, y) // TODO: Remove Dynamic
    fun textBounds(text: String, x: Number, y: Number, fontSize: Number): dynamic = nativeFont.textBounds(text, x, y, fontSize) // TODO: Remove Dynamic
    fun textBounds(text: String, x: Number, y: Number, fontSize: Number, options: dynamic): dynamic = nativeFont.textBounds(text, x, y, fontSize, options) // TODO: Remove Dynamic
    fun textToPoints(text: String, x: Number, y: Number): Array<dynamic> = nativeFont.textToPoints(text, x, y) // TODO: Remove Dynamic
    fun textToPoints(text: String, x: Number, y: Number, fontSize: Number): Array<dynamic> = nativeFont.textToPoints(text, x, y, fontSize) // TODO: Remove Dynamic
    fun textToPoints(text: String, x: Number, y: Number, fontSize: Number, options: dynamic): Array<dynamic> = nativeFont.textToPoints(text, x, y, fontSize, options) // TODO: Remove Dynamic
}

class Slider(val nativeSlider: NativeElement, val min: Number, val max: Number?, val step: Number?): ValueElement<Double>(nativeSlider)

class Div(val nativeDiv: NativeElement): Element(nativeDiv)

class Button(val nativeButton: NativeElement): Element(nativeButton) {
    fun text(string: String) = html(string)
    fun fontSize(sizePx: Number) = style("font-size", "${sizePx}px")
}

class Table(val nativeTable: NativeTable) {
    constructor(): this(NativeTable())
    constructor(rows: Array<TableRow>): this(NativeTable(rows.arrayMap {it.nativeTableRow} ))

    val columns: Array<String> by nativeTable::columns
    val rows: Array<TableRow> get() = nativeTable.rows.arrayMap(::TableRow)

    fun addRow(): TableRow = TableRow(nativeTable.addRow())
    fun addRow(row: TableRow) = nativeTable.addRow(row.nativeTableRow)
    fun removeRow(id: Int) = nativeTable.removeRow(id)
    fun getRow(id: Int): TableRow = TableRow(nativeTable.getRow(id))
    fun getRows(): Array<TableRow> = nativeTable.getRows().arrayMap(::TableRow)
    fun findRow(value: String, id: Int): TableRow = TableRow(nativeTable.findRow(value, id))
    fun findRow(value: String, header: String): TableRow = TableRow(nativeTable.findRow(value, header))
    fun findRows(value: String, columnId: Int): Array<TableRow> = nativeTable.findRows(value, columnId).arrayMap(::TableRow)
    fun findRows(value: String, columnHeader: String): Array<TableRow> = nativeTable.findRows(value, columnHeader).arrayMap(::TableRow)
    fun matchRow(regExp: RegExp, columnId: Int): TableRow = TableRow(nativeTable.matchRow(regExp, columnId))
    fun matchRow(string: String, columnId: Int): TableRow = TableRow(nativeTable.matchRow(string, columnId))
    fun matchRow(regExp: RegExp, columnHeader: String): TableRow = TableRow(nativeTable.matchRow(regExp, columnHeader))
    fun matchRow(string: String, columnHeader: String): TableRow = TableRow(nativeTable.matchRow(string, columnHeader))
    fun matchRows(regExp: RegExp, columnId: Int): Array<TableRow> = nativeTable.matchRows(regExp, columnId).arrayMap(::TableRow)
    fun matchRows(string: String, columnId: Int): Array<TableRow> = nativeTable.matchRows(string, columnId).arrayMap(::TableRow)
    fun matchRows(regExp: RegExp, columnHeader: String): Array<TableRow> = nativeTable.matchRows(regExp, columnHeader).arrayMap(::TableRow)
    fun matchRows(string: String, columnHeader: String): Array<TableRow> = nativeTable.matchRows(string, columnHeader).arrayMap(::TableRow)
    fun getColumn(columnId: Int): Array<Int> = nativeTable.getColumn(columnId)
    fun getColumn(columnHeader: String): Array<Int> = nativeTable.getColumn(columnHeader)
    fun clearRows() = nativeTable.clearRows()
    fun addColumn() = nativeTable.addColumn()
    fun addColumn(title: String) = nativeTable.addColumn(title)
    fun getColumnCount(): Int = nativeTable.getColumnCount()
    fun getRowCount(): Int = nativeTable.getRowCount()
    fun removeTokens(chars: String, columnId: Int) = nativeTable.removeTokens(chars, columnId)
    fun removeTokens(chars: String, columnHeader: String) = nativeTable.removeTokens(chars, columnHeader)
    fun trim(columnId: Int) = nativeTable.trim(columnId)
    fun trim(columnHeader: String) = nativeTable.trim(columnHeader)
    fun removeColumn(columnId: Int) = nativeTable.removeColumn(columnId)
    fun removeColumn(columnHeader: String) = nativeTable.removeColumn(columnHeader)
    fun set(row: Int, columnId: Int, value: String) = nativeTable.set(row, columnId, value)
    fun set(row: Int, columnId: Int, value: Number) = nativeTable.set(row, columnId, value)
    fun set(row: Int, columnHeader: String, value: String) = nativeTable.set(row, columnHeader, value)
    fun set(row: Int, columnHeader: String, value: Number) = nativeTable.set(row, columnHeader, value)
    fun setNum(row: Int, columnId: Int, value: Number) = nativeTable.setNum(row, columnId, value)
    fun setNum(row: Int, columnHeader: String, value: Number) = nativeTable.setNum(row, columnHeader, value)
    fun setString(row: Int, columnId: Int, value: String) = nativeTable.setString(row, columnId, value)
    fun setString(row: Int, columnHeader: String, value: String) = nativeTable.setString(row, columnHeader, value)
    fun <T> get(row: Int, columnId: Int): T = nativeTable.get(row, columnId)
    fun <T> get(row: Int, columnHeader: String): T = nativeTable.get(row, columnHeader)
    fun getNum(row: Int, columnId: Int): Double = nativeTable.getNum(row, columnId)
    fun getNum(row: Int, columnHeader: String): Double = nativeTable.getNum(row, columnHeader)
    fun getString(row: Int, columnId: Int): String = nativeTable.getString(row, columnId)
    fun getString(row: Int, columnHeader: String): String = nativeTable.getString(row, columnHeader)
    fun getObject() = nativeTable.getObject()
    fun getObject(headerColumn: String): Json = nativeTable.getObject(headerColumn)
    fun getArray(): Array<Array<Any>> = nativeTable.getArray()
}

class TableRow(val nativeTableRow: NativeTableRow) {
    fun set(columnId: Int, value: Number) = nativeTableRow.set(columnId, value)
    fun set(columnId: Int, value: String) = nativeTableRow.set(columnId, value)
    fun set(columnHeader: String, value: Number) = nativeTableRow.set(columnHeader, value)
    fun set(columnHeader: String, value: String) = nativeTableRow.set(columnHeader, value)
    fun setNum(columnId: Int, value: Number) = nativeTableRow.setNum(columnId, value)
    fun setNum(columnHeader: String, value: Number) = nativeTableRow.setNum(columnHeader, value)
    fun setString(columnId: Int, value: String) = nativeTableRow.setString(columnId, value)
    fun setString(columnHeader: String, value: String) = nativeTableRow.setString(columnHeader, value)
    fun <T> get(columnId: Int): T = nativeTableRow.get(columnId)
    fun <T> get(columnHeader: String): T = nativeTableRow.get(columnHeader)
    fun getNum(columnId: Int): Double = nativeTableRow.getNum(columnId)
    fun getNum(columnHeader: String): Double = nativeTableRow.getNum(columnHeader)
    fun getString(columnId: Int): String = nativeTableRow.getString(columnId)
    fun getString(columnHeader: String): String = nativeTableRow.getString(columnHeader)
}

class Texture(val nativeTexture: NativeTexture)

open class Shader(val nativeShader: NativeShader, var uniformCallbacks: MutableMap<String, ()->Any>? = null) {

    val uniforms: MutableMap<String, Any> = mutableMapOf()
    var magFilterMode = MagFilterMode.LINEAR
    var minFilterMode = MinFilterMode.NEAREST_MIPMAP_LINEAR

    operator fun set(uniformName: String, data: Boolean) {
        val oldValue = uniforms[uniformName]
        if (data != oldValue) {
            uniforms[uniformName] = data
            nativeShader.setUniform(uniformName, data)
        }
    }
    operator fun set(uniformName: String, data: Number) {
        val oldValue = uniforms[uniformName]
        if (data != oldValue) {
            uniforms[uniformName] = data
            nativeShader.setUniform(uniformName, data)
        }
    }
    operator fun set(uniformName: String, data: Array<Number>) {
        val oldValue = uniforms[uniformName]
        if (data != oldValue) {
            uniforms[uniformName] = data
            nativeShader.setUniform(uniformName, data)
        }
    }
    operator fun set(uniformName: String, data: Image) {
        val oldValue = uniforms[uniformName]
        if (data != oldValue) {
            uniforms[uniformName] = data
            nativeShader.setUniform(uniformName, data.nativeImage)
        }
    }
    operator fun set(uniformName: String, data: P5) {
        val oldValue = uniforms[uniformName]
        if (data != oldValue) {
            uniforms[uniformName] = data
            when(val nativeCanvas = data.getCanvas().nativeElement) {
                is NativeP5.NativeRenderer2D -> nativeShader.setUniform(uniformName, nativeCanvas)
                is NativeP5.NativeRendererGL -> nativeShader.setUniform(uniformName, nativeCanvas)
            }
        }
    }
    operator fun set(uniformName: String, data: MediaElement) {
        val oldValue = uniforms[uniformName]
        if (data != oldValue) {
            uniforms[uniformName] = data
            nativeShader.setUniform(uniformName, data.nativeMediaElement)
        }
    }
    operator fun set(uniformName: String, data: Texture) {
        val oldValue = uniforms[uniformName]
        if (data != oldValue) {
            uniforms[uniformName] = data
            nativeShader.setUniform(uniformName, data.nativeTexture)
        }
    }
    operator fun set(uniformName: String, data: P5.Renderer) {
        uniforms[uniformName] = data
        when(data) {
            is P5.Renderer2D -> nativeShader.setUniform(uniformName, data.nativeRenderer2D)
            is P5.RendererGL -> nativeShader.setUniform(uniformName, data.nativeRendererGl)
        }
    }

    inline operator fun <reified T> get(uniformName: String): T? {
        return uniforms[uniformName] as? T
    }

    fun updateUniforms(uniforms: Map<String, Any>) {
        for( (k, v) in uniforms) {
            when(v) {
                is Boolean -> set(k, v)
                is Number -> set(k, v)
                is Array<*> -> set(k, v as? Array<Number> ?: arrayOf())
                is Image -> set(k, v)
                is P5 -> set(k, v)
                is MediaElement -> set(k, v)
                is Texture -> set(k, v)
                is P5.Renderer -> set(k ,v)
                else -> console.warn("Invalid Shader projects.testing.Uniform Type: $v")
            }
        }
    }

//    fun update() {
//        uniformCallbacks?.mapValues { (_, value) -> value() }?.let { updateUniforms(it) }
//    }

    fun updateUniformCallbacks() {
        uniformCallbacks?.mapValues { (_, value) -> value() }?.let { updateUniforms(it) }
    }
}

class MultiShader(nativeShader: NativeShader, uniformCallbacks: MutableMap<String, ()->Any>? = null): Shader(nativeShader, uniformCallbacks)



class PrintWriter(val nativePrintWriter: NativePrintWriter) { // TODO: Remove dynamic
    fun write(data: dynamic) = nativePrintWriter.write(data)
    fun print(data: dynamic) = nativePrintWriter.print(data)
    fun clear() = nativePrintWriter.clear()
    fun close() = nativePrintWriter.close()
}

class Input(nativeElement: NativeElement): ValueElement<String>(nativeElement)

class Paragraph(nativeElement: NativeElement): Element(nativeElement) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        name = property.name
        return html()
    }
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        name = property.name
        html(value)
    }
}