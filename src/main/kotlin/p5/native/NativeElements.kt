package p5.native

import kotlin.js.Json
import kotlin.js.RegExp

@JsName("Element")
open external class NativeElement(elt: String) {
    fun parent(): NativeElement
    fun parent(parentString: String)
    fun parent(parentElement: NativeElement)
    fun id(): String
    fun id(idString: String)
    @JsName("class")
    fun styleClass(): String
    @JsName("class")
    fun styleClass(classString: String)
    fun mousePressed(keepCallback: Boolean)
    fun mousePressed(callback: ()->Unit)
    fun doubleClicked(keepCallback: Boolean)
    fun doubleClicked(callback: (NativeMouseEvent)->Unit)
    fun mouseWheel(keepCallback: Boolean)
    fun mouseWheel(callback: (NativeWheelEvent)->Unit)
    fun mouseReleased(keepCallback: Boolean)
    fun mouseReleased(callback: (NativeMouseEvent)->Unit)
    fun mouseClicked(keepCallback: Boolean)
    fun mouseClicked(callback: (NativePointerEvent)->Unit)
    fun mouseMoved(keepCallback: Boolean)
    fun mouseMoved(callback: (NativeMouseEvent)->Unit)
    fun mouseOver(keepCallback: Boolean)
    fun mouseOver(callback: (NativeMouseEvent)->Unit)
    fun mouseOut(keepCallback: Boolean)
    fun mouseOut(callback: (NativeMouseEvent)->Unit)
    fun touchStarted(keepCallback: Boolean)
    fun touchStarted(callback: (dynamic)->Unit)
    fun touchMoved(keepCallback: Boolean)
    fun touchMoved(callback: (dynamic)->Unit)
    fun touchEnded(keepCallback: Boolean)
    fun touchEnded(callback: (dynamic)->Unit)
    fun dragOver(keepCallback: Boolean)
    fun dragOver(callback: (NativeDragEvent)->Unit)
    fun dragLeave(keepCallback: Boolean)
    fun dragLeave(callback: (NativeDragEvent)->Unit)
    fun changed(keepCallback: Boolean)
    fun changed(callback: (dynamic)->Unit)
    fun input(keepCallback: Boolean)
    fun input(callback: (dynamic)->Unit)
    fun addClass(classString: String)
    fun removeClass(classString: String)
    fun hasClass(classString: String): Boolean
    fun toggleClass(classString: String)
    fun child()
    fun child(classString: String)
    fun child(classElement: NativeElement)
    fun center()
    fun center(alignString: String)
    fun html(): String
    fun html(htmlString: String)
    fun html(htmlString: String, append: Boolean)
    fun position(): dynamic
    fun position(x: Number, y: Number)
    fun position(x: Number, y: Number, positionTypeString: String)
    fun style(property: String): String?
    fun style(property: String, value: String)
    fun attribute(attr: String): String
    fun attribute(attr: String, value: String)
    fun removeAttribute(attr: String)
    fun show()
    fun hide()
    fun size(): dynamic
    fun size(w: Number)
    fun size(w: String)
    fun size(w: Number, h: Number)
    fun size(w: Number, h: String)
    fun size(w: String, h: Number)
    fun remove()
    fun drop(callback: (NativeFile)->Unit)
    fun drop(callback: (NativeFile)->Unit, onDrop: ()->Unit)
    fun value(): dynamic
    fun value(v: dynamic)
}

@JsName("Color")
external class NativeColor {
    override fun toString(): String
    fun setRed(red: Number)
    fun setGreen(green: Number)
    fun setBlue(blue: Number)
    fun setAlpha(alpha: Number)
}

@JsName("Checkbox")
external class NativeCheckbox: NativeElement {
    fun checked(): Boolean
}

@JsName("Select")
external class NativeSelect: NativeElement {
    fun option(name: String)
    fun option(name: String, value: String)
    fun selected(): NativeElement
    fun selected(value: String)
    fun disable()
    fun disable(value: String)
}

@JsName("NativeRadio")
external class NativeRadio: NativeElement {
    fun option(name: String)
    fun option(name: String, value: String)
    fun selected(): NativeElement
    fun selected(value: String)
    fun disable(disabled: Boolean)
}

@JsName("ColorPicker")
external class NativeColorPicker: NativeElement {
    fun color(): NativeColor
}

@JsName("NativeMediaElement")
external class NativeMediaElement: NativeElement {
    val src: String
    fun play()
    fun stop()
    fun pause()
    fun loop()
    fun noLoop()
    fun autoplay(shouldAutoplay: Boolean)
    fun volume(): Double
    fun volume(value: Number)
    fun speed(): Double
    fun speed(multiplier: Number)
    fun time(): Double
    fun time(timeSeconds: Number)
    fun duration(): Double
    fun onended(callback: (NativeMediaElement)->Unit)
    // fun connect() // TODO: Implement when integrating p5.sound
    fun disconnect()
    fun showControls()
    fun hideControls()
    fun addCue(time: Number, callback: (Double)->Unit): Double
    fun <T> addCue(time: Number, callback: (Double, T)->Unit, value: T): Double
    fun removeCue(id: Number)
    fun clearCues()
    fun get(): NativeImage
}

@JsName("File")
external class NativeFile {
    val file: dynamic
    val type: String
    val subtype: String
    val name: String
    val size: dynamic // TODO: Figure out what type this really is
    val data: String
}

@JsName("Image")
external class NativeImage {
    val width: Double
    val height: Double
    val pixels: Array<Int>
    fun loadPixels()
    fun updatePixels()
    fun get(): NativeImage
    fun get(x: Number, y: Number): NativeImage
    fun get(x: Number, y: Number, w: Number, h: Number): NativeImage
    fun set(x: Number, y: Number, a: Number)
    fun set(x: Number, y: Number, a: Array<Number>)
    fun set(x: Number, y: Number, a: NativeColor)
    fun set(x: Number, y: Number, a: NativeImage)
    fun resize(width: Number, height: Number)
    fun copy(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int)
    fun copy(srcImage: NativeImage, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int)
    fun mask(srcImage: NativeImage)
    fun filter(filterType: String)
    fun filter(filterType: String, filterParam: Number)
    fun blend(sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: String)
    fun blend(srcImage: NativeImage, sx: Int, sy: Int, sw: Int, sh: Int, dx: Int, dy: Int, dw: Int, dh: Int, blendMode: String)
    fun save(filename: String, extension: String)
    fun reset()
    fun getCurrentFrame(): Int
    fun setFrame(index: Int)
    fun numFrames(): Int
    fun play()
    fun pause()
    fun delay(d: Number)
    fun delay(d: Number, index: Number)
}

@JsName("Font")
external class NativeFont {
    val font: dynamic // TODO: Remove Dynamic
    fun textBounds(text: String, x: Number, y: Number): dynamic // TODO: Remove Dynamic
    fun textBounds(text: String, x: Number, y: Number, fontSize: Number): dynamic // TODO: Remove Dynamic
    fun textBounds(text: String, x: Number, y: Number, fontSize: Number, options: dynamic): dynamic // TODO: Remove Dynamic
    fun textToPoints(text: String, x: Number, y: Number): Array<dynamic> // TODO: Remove Dynamic
    fun textToPoints(text: String, x: Number, y: Number, fontSize: Number): Array<dynamic> // TODO: Remove Dynamic
    fun textToPoints(text: String, x: Number, y: Number, fontSize: Number, options: dynamic): Array<dynamic> // TODO: Remove Dynamic
}

@JsName("Table")
external class NativeTable() {
    constructor(rows: Array<NativeTableRow>)

    val columns: Array<String>
    val rows: Array<NativeTableRow>

    fun addRow(): NativeTableRow
    fun addRow(row: NativeTableRow): NativeTableRow
    fun removeRow(id: Int)
    fun getRow(id: Int): NativeTableRow
    fun getRows(): Array<NativeTableRow>
    fun findRow(value: String, id: Int): NativeTableRow
    fun findRow(value: String, header: String): NativeTableRow
    fun findRows(value: String, columnId: Int): Array<NativeTableRow>
    fun findRows(value: String, columnHeader: String): Array<NativeTableRow>
    fun matchRow(regExp: RegExp, columnId: Int): NativeTableRow
    fun matchRow(string: String, columnId: Int): NativeTableRow
    fun matchRow(regExp: RegExp, columnHeader: String): NativeTableRow
    fun matchRow(string: String, columnHeader: String): NativeTableRow
    fun matchRows(regExp: RegExp, columnId: Int): Array<NativeTableRow>
    fun matchRows(string: String, columnId: Int): Array<NativeTableRow>
    fun matchRows(regExp: RegExp, columnHeader: String): Array<NativeTableRow>
    fun matchRows(string: String, columnHeader: String): Array<NativeTableRow>
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
    fun getNum(row: Int, columnId: Int): Double
    fun getNum(row: Int, columnHeader: String): Double
    fun getString(row: Int, columnId: Int): String
    fun getString(row: Int, columnHeader: String): String
    fun getObject(): Json
    fun getObject(headerColumn: String): Json
    fun getArray(): Array<Array<Any>>
}

@JsName("TableRow")
external class NativeTableRow(str: String = definedExternally, separator: String = definedExternally) { // TODO: Move and Lift
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
    fun getNum(columnId: Int): Double
    fun getNum(columnHeader: String): Double
    fun getString(columnId: Int): String
    fun getString(columnHeader: String): String
}

@JsName("Texture")
external class NativeTexture

@JsName("Shader")
external class NativeShader {
    fun setUniform(uniformName: String, data: Boolean)
    fun setUniform(uniformName: String, data: Number)
    fun setUniform(uniformName: String, data: Array<Number>)
    fun setUniform(uniformName: String, data: NativeImage)
    fun setUniform(uniformName: String, data: NativeMediaElement)
    fun setUniform(uniformName: String, data: NativeTexture)
    fun setUniform(uniformName: String, data: NativeP5.NativeRenderer2D)
    fun setUniform(uniformName: String, data: NativeP5.NativeRendererGL)
}

@JsName("PrintWriter")
external class NativePrintWriter {
    fun write(data: dynamic)
    fun print(data: dynamic)
    fun clear()
    fun close()
}

// TODO: Add CanvasRenderingContexts
//drawingContext: CanvasRenderingContext2D
//    canvas: canvas#defaultCanvas0.p5Canvas
//    direction: "ltr"
//    fillStyle: "#ffffff"
//    filter: "none"
//    font: "12px sans-serif"
//    fontKerning: "auto"
//    fontStretch: "normal"
//    fontVariantCaps: "normal"
//    globalAlpha: 1
//    globalCompositeOperation: "source-over"
//    imageSmoothingEnabled: true
//    imageSmoothingQuality: "low"
//    letterSpacing: "0px"
//    lineCap: "round"
//    lineDashOffset: 0
//    lineJoin: "miter"
//    lineWidth: 1
//    miterLimit: 10
//    shadowBlur: 0
//    shadowColor: "rgba(0, 0, 0, 0)"
//    shadowOffsetX: 0
//    shadowOffsetY: 0
//    strokeStyle: "#000000"
//    textAlign: "start"
//    textBaseline: "alphabetic"
//    textRendering: "auto"
//    wordSpacing: "0px"
//    [[Prototype]]: CanvasRenderingContext2D
//        arc: ƒ arc()
//        arcTo: ƒ arcTo()
//        beginPath: ƒ beginPath()
//        bezierCurveTo: ƒ bezierCurveTo()
//        canvas: (...)
//        clearRect: ƒ clearRect()
//        clip: ƒ clip()
//        closePath: ƒ closePath()
//        createConicGradient: ƒ createConicGradient()
//        createImageData: ƒ createImageData()
//        createLinearGradient: ƒ createLinearGradient()
//        createPattern: ƒ createPattern()
//        createRadialGradient: ƒ createRadialGradient()
//        direction: (...)
//        drawFocusIfNeeded: ƒ drawFocusIfNeeded()
//        drawImage: ƒ drawImage()
//        ellipse: ƒ ellipse()
//        fill: ƒ fill()
//        fillRect: ƒ fillRect()
//        fillStyle: (...)
//        fillText: ƒ fillText()
//        filter: (...)
//        font: (...)
//        fontKerning: (...)
//        fontStretch: (...)
//        fontVariantCaps: (...)
//        getContextAttributes: ƒ getContextAttributes()
//        getImageData: ƒ getImageData()
//        getLineDash: ƒ getLineDash()
//        getTransform: ƒ getTransform()
//        globalAlpha: (...)
//        globalCompositeOperation: (...)
//        imageSmoothingEnabled: (...)
//        imageSmoothingQuality: (...)
//        isContextLost: ƒ isContextLost()
//        isPointInPath: ƒ isPointInPath()
//        isPointInStroke: ƒ isPointInStroke()
//        letterSpacing: (...)
//        lineCap: (...)
//        lineDashOffset: (...)
//        lineJoin: (...)
//        lineTo: ƒ lineTo()
//        lineWidth: (...)
//        measureText: ƒ measureText()
//        miterLimit: (...)
//        moveTo: ƒ moveTo()
//        putImageData: ƒ putImageData()
//        quadraticCurveTo: ƒ quadraticCurveTo()
//        rect: ƒ rect()
//        reset: ƒ reset()
//        resetTransform: ƒ resetTransform()
//        restore: ƒ restore()
//        rotate: ƒ rotate()
//        roundRect: ƒ roundRect()
//        save: ƒ save()
//        scale: ƒ scale()
//        setLineDash: ƒ setLineDash()
//        setTransform: ƒ setTransform()
//        shadowBlur: (...)
//        shadowColor: (...)
//        shadowOffsetX: (...)
//        shadowOffsetY: (...)
//        stroke: ƒ stroke()
//        strokeRect: ƒ strokeRect()
//        strokeStyle: (...)
//        strokeText: ƒ strokeText()
//        textAlign: (...)
//        textBaseline: (...)
//        textRendering: (...)
//        transform: ƒ transform()
//        translate: ƒ translate()
//        wordSpacing: "0px"

// TODO: Add WebGLRenderingContext
//drawingContext: WebGLRenderingContext
//canvas: canvas#defaultCanvas0.p5Canvas
//drawingBufferColorSpace: "srgb"
//drawingBufferHeight: 100
//drawingBufferWidth: 100
//unpackColorSpace: "srgb"
//    [[Prototype]]: WebGLRenderingContext
//    ACTIVE_ATTRIBUTES: 35721
//    ACTIVE_TEXTURE: 34016
//    ACTIVE_UNIFORMS: 35718
//    ALIASED_LINE_WIDTH_RANGE: 33902
//    ALIASED_POINT_SIZE_RANGE: 33901
//    ALPHA: 6406
//    ALPHA_BITS: 3413
//    ALWAYS: 519
//    ARRAY_BUFFER: 34962
//    ARRAY_BUFFER_BINDING: 34964
//    ATTACHED_SHADERS: 35717
//    BACK: 1029
//    BLEND: 3042
//    BLEND_COLOR: 32773
//    BLEND_DST_ALPHA: 32970
//    BLEND_DST_RGB: 32968
//    BLEND_EQUATION: 32777
//    BLEND_EQUATION_ALPHA: 34877
//    BLEND_EQUATION_RGB: 32777
//    BLEND_SRC_ALPHA: 32971
//    BLEND_SRC_RGB: 32969
//    BLUE_BITS: 3412
//    BOOL: 35670
//    BOOL_VEC2: 35671
//    BOOL_VEC3: 35672
//    BOOL_VEC4: 35673
//    BROWSER_DEFAULT_WEBGL: 37444
//    BUFFER_SIZE: 34660
//    BUFFER_USAGE: 34661
//    BYTE: 5120
//    CCW: 2305
//    CLAMP_TO_EDGE: 33071
//    COLOR_ATTACHMENT0: 36064
//    COLOR_BUFFER_BIT: 16384
//    COLOR_CLEAR_VALUE: 3106
//    COLOR_WRITEMASK: 3107
//    COMPILE_STATUS: 35713
//    COMPRESSED_TEXTURE_FORMATS: 34467
//    CONSTANT_ALPHA: 32771
//    CONSTANT_COLOR: 32769
//    CONTEXT_LOST_WEBGL: 37442
//    CULL_FACE: 2884
//    CULL_FACE_MODE: 2885
//    CURRENT_PROGRAM: 35725
//    CURRENT_VERTEX_ATTRIB: 34342
//    CW: 2304
//    DECR: 7683
//    DECR_WRAP: 34056
//    DELETE_STATUS: 35712
//    DEPTH_ATTACHMENT: 36096
//    DEPTH_BITS: 3414
//    DEPTH_BUFFER_BIT: 256
//    DEPTH_CLEAR_VALUE: 2931
//    DEPTH_COMPONENT: 6402
//    DEPTH_COMPONENT16: 33189
//    DEPTH_FUNC: 2932
//    DEPTH_RANGE: 2928
//    DEPTH_STENCIL: 34041
//    DEPTH_STENCIL_ATTACHMENT: 33306
//    DEPTH_TEST: 2929
//    DEPTH_WRITEMASK: 2930
//    DITHER: 3024
//    DONT_CARE: 4352
//    DST_ALPHA: 772
//    DST_COLOR: 774
//    DYNAMIC_DRAW: 35048
//    ELEMENT_ARRAY_BUFFER: 34963
//    ELEMENT_ARRAY_BUFFER_BINDING: 34965
//    EQUAL: 514
//    FASTEST: 4353
//    FLOAT: 5126
//    FLOAT_MAT2: 35674
//    FLOAT_MAT3: 35675
//    FLOAT_MAT4: 35676
//    FLOAT_VEC2: 35664
//    FLOAT_VEC3: 35665
//    FLOAT_VEC4: 35666
//    FRAGMENT_SHADER: 35632
//    FRAMEBUFFER: 36160
//    FRAMEBUFFER_ATTACHMENT_OBJECT_NAME: 36049
//    FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE: 36048
//    FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE: 36051
//    FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL: 36050
//    FRAMEBUFFER_BINDING: 36006
//    FRAMEBUFFER_COMPLETE: 36053
//    FRAMEBUFFER_INCOMPLETE_ATTACHMENT: 36054
//    FRAMEBUFFER_INCOMPLETE_DIMENSIONS: 36057
//    FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT: 36055
//    FRAMEBUFFER_UNSUPPORTED: 36061
//    FRONT: 1028
//    FRONT_AND_BACK: 1032
//    FRONT_FACE: 2886
//    FUNC_ADD: 32774
//    FUNC_REVERSE_SUBTRACT: 32779
//    FUNC_SUBTRACT: 32778
//    GENERATE_MIPMAP_HINT: 33170
//    GEQUAL: 518
//    GREATER: 516
//    GREEN_BITS: 3411
//    HIGH_FLOAT: 36338
//    HIGH_INT: 36341
//    IMPLEMENTATION_COLOR_READ_FORMAT: 35739
//    IMPLEMENTATION_COLOR_READ_TYPE: 35738
//    INCR: 7682
//    INCR_WRAP: 34055
//    INT: 5124
//    INT_VEC2: 35667
//    INT_VEC3: 35668
//    INT_VEC4: 35669
//    INVALID_ENUM: 1280
//    INVALID_FRAMEBUFFER_OPERATION: 1286
//    INVALID_OPERATION: 1282
//    INVALID_VALUE: 1281
//    INVERT: 5386
//    KEEP: 7680
//    LEQUAL: 515
//    LESS: 513
//    LINEAR: 9729
//    LINEAR_MIPMAP_LINEAR: 9987
//    LINEAR_MIPMAP_NEAREST: 9985
//    LINES: 1
//    LINE_LOOP: 2
//    LINE_STRIP: 3
//    LINE_WIDTH: 2849
//    LINK_STATUS: 35714
//    LOW_FLOAT: 36336
//    LOW_INT: 36339
//    LUMINANCE: 6409
//    LUMINANCE_ALPHA: 6410
//    MAX_COMBINED_TEXTURE_IMAGE_UNITS: 35661
//    MAX_CUBE_MAP_TEXTURE_SIZE: 34076
//    MAX_FRAGMENT_UNIFORM_VECTORS: 36349
//    MAX_RENDERBUFFER_SIZE: 34024
//    MAX_TEXTURE_IMAGE_UNITS: 34930
//    MAX_TEXTURE_SIZE: 3379
//    MAX_VARYING_VECTORS: 36348
//    MAX_VERTEX_ATTRIBS: 34921
//    MAX_VERTEX_TEXTURE_IMAGE_UNITS: 35660
//    MAX_VERTEX_UNIFORM_VECTORS: 36347
//    MAX_VIEWPORT_DIMS: 3386
//    MEDIUM_FLOAT: 36337
//    MEDIUM_INT: 36340
//    MIRRORED_REPEAT: 33648
//    NEAREST: 9728
//    NEAREST_MIPMAP_LINEAR: 9986
//    NEAREST_MIPMAP_NEAREST: 9984
//    NEVER: 512
//    NICEST: 4354
//    NONE: 0
//    NOTEQUAL: 517
//    NO_ERROR: 0
//    ONE: 1
//    ONE_MINUS_CONSTANT_ALPHA: 32772
//    ONE_MINUS_CONSTANT_COLOR: 32770
//    ONE_MINUS_DST_ALPHA: 773
//    ONE_MINUS_DST_COLOR: 775
//    ONE_MINUS_SRC_ALPHA: 771
//    ONE_MINUS_SRC_COLOR: 769
//    OUT_OF_MEMORY: 1285
//    PACK_ALIGNMENT: 3333
//    POINTS: 0
//    POLYGON_OFFSET_FACTOR: 32824
//    POLYGON_OFFSET_FILL: 32823
//    POLYGON_OFFSET_UNITS: 10752
//    RED_BITS: 3410
//    RENDERBUFFER: 36161
//    RENDERBUFFER_ALPHA_SIZE: 36179
//    RENDERBUFFER_BINDING: 36007
//    RENDERBUFFER_BLUE_SIZE: 36178
//    RENDERBUFFER_DEPTH_SIZE: 36180
//    RENDERBUFFER_GREEN_SIZE: 36177
//    RENDERBUFFER_HEIGHT: 36163
//    RENDERBUFFER_INTERNAL_FORMAT: 36164
//    RENDERBUFFER_RED_SIZE: 36176
//    RENDERBUFFER_STENCIL_SIZE: 36181
//    RENDERBUFFER_WIDTH: 36162
//    RENDERER: 7937
//    REPEAT: 10497
//    REPLACE: 7681
//    RGB: 6407
//    RGB5_A1: 32855
//    RGB565: 36194
//    RGBA: 6408
//    RGBA4: 32854
//    SAMPLER_2D: 35678
//    SAMPLER_CUBE: 35680
//    SAMPLES: 32937
//    SAMPLE_ALPHA_TO_COVERAGE: 32926
//    SAMPLE_BUFFERS: 32936
//    SAMPLE_COVERAGE: 32928
//    SAMPLE_COVERAGE_INVERT: 32939
//    SAMPLE_COVERAGE_VALUE: 32938
//    SCISSOR_BOX: 3088
//    SCISSOR_TEST: 3089
//    SHADER_TYPE: 35663
//    SHADING_LANGUAGE_VERSION: 35724
//    SHORT: 5122
//    SRC_ALPHA: 770
//    SRC_ALPHA_SATURATE: 776
//    SRC_COLOR: 768
//    STATIC_DRAW: 35044
//    STENCIL_ATTACHMENT: 36128
//    STENCIL_BACK_FAIL: 34817
//    STENCIL_BACK_FUNC: 34816
//    STENCIL_BACK_PASS_DEPTH_FAIL: 34818
//    STENCIL_BACK_PASS_DEPTH_PASS: 34819
//    STENCIL_BACK_REF: 36003
//    STENCIL_BACK_VALUE_MASK: 36004
//    STENCIL_BACK_WRITEMASK: 36005
//    STENCIL_BITS: 3415
//    STENCIL_BUFFER_BIT: 1024
//    STENCIL_CLEAR_VALUE: 2961
//    STENCIL_FAIL: 2964
//    STENCIL_FUNC: 2962
//    STENCIL_INDEX8: 36168
//    STENCIL_PASS_DEPTH_FAIL: 2965
//    STENCIL_PASS_DEPTH_PASS: 2966
//    STENCIL_REF: 2967
//    STENCIL_TEST: 2960
//    STENCIL_VALUE_MASK: 2963
//    STENCIL_WRITEMASK: 2968
//    STREAM_DRAW: 35040
//    SUBPIXEL_BITS: 3408
//    TEXTURE: 5890
//    TEXTURE0: 33984
//    TEXTURE1: 33985
//    TEXTURE2: 33986
//    TEXTURE3: 33987
//    TEXTURE4: 33988
//    TEXTURE5: 33989
//    TEXTURE6: 33990
//    TEXTURE7: 33991
//    TEXTURE8: 33992
//    TEXTURE9: 33993
//    TEXTURE10: 33994
//    TEXTURE11: 33995
//    TEXTURE12: 33996
//    TEXTURE13: 33997
//    TEXTURE14: 33998
//    TEXTURE15: 33999
//    TEXTURE16: 34000
//    TEXTURE17: 34001
//    TEXTURE18: 34002
//    TEXTURE19: 34003
//    TEXTURE20: 34004
//    TEXTURE21: 34005
//    TEXTURE22: 34006
//    TEXTURE23: 34007
//    TEXTURE24: 34008
//    TEXTURE25: 34009
//    TEXTURE26: 34010
//    TEXTURE27: 34011
//    TEXTURE28: 34012
//    TEXTURE29: 34013
//    TEXTURE30: 34014
//    TEXTURE31: 34015
//    TEXTURE_2D: 3553
//    TEXTURE_BINDING_2D: 32873
//    TEXTURE_BINDING_CUBE_MAP: 34068
//    TEXTURE_CUBE_MAP: 34067
//    TEXTURE_CUBE_MAP_NEGATIVE_X: 34070
//    TEXTURE_CUBE_MAP_NEGATIVE_Y: 34072
//    TEXTURE_CUBE_MAP_NEGATIVE_Z: 34074
//    TEXTURE_CUBE_MAP_POSITIVE_X: 34069
//    TEXTURE_CUBE_MAP_POSITIVE_Y: 34071
//    TEXTURE_CUBE_MAP_POSITIVE_Z: 34073
//    TEXTURE_MAG_FILTER: 10240
//    TEXTURE_MIN_FILTER: 10241
//    TEXTURE_WRAP_S: 10242
//    TEXTURE_WRAP_T: 10243
//    TRIANGLES: 4
//    TRIANGLE_FAN: 6
//    TRIANGLE_STRIP: 5
//    UNPACK_ALIGNMENT: 3317
//    UNPACK_COLORSPACE_CONVERSION_WEBGL: 37443
//    UNPACK_FLIP_Y_WEBGL: 37440
//    UNPACK_PREMULTIPLY_ALPHA_WEBGL: 37441
//    UNSIGNED_BYTE: 5121
//    UNSIGNED_INT: 5125
//    UNSIGNED_SHORT: 5123
//    UNSIGNED_SHORT_4_4_4_4: 32819
//    UNSIGNED_SHORT_5_5_5_1: 32820
//    UNSIGNED_SHORT_5_6_5: 33635
//    VALIDATE_STATUS: 35715
//    VENDOR: 7936
//    VERSION: 7938
//    VERTEX_ATTRIB_ARRAY_BUFFER_BINDING: 34975
//    VERTEX_ATTRIB_ARRAY_ENABLED: 34338
//    VERTEX_ATTRIB_ARRAY_NORMALIZED: 34922
//    VERTEX_ATTRIB_ARRAY_POINTER: 34373
//    VERTEX_ATTRIB_ARRAY_SIZE: 34339
//    VERTEX_ATTRIB_ARRAY_STRIDE: 34340
//    VERTEX_ATTRIB_ARRAY_TYPE: 34341
//    VERTEX_SHADER: 35633
//    VIEWPORT: 2978
//    ZERO: 0
//    activeTexture: ƒ activeTexture()
//    attachShader: ƒ attachShader()
//    bindAttribLocation: ƒ bindAttribLocation()
//    bindBuffer: ƒ bindBuffer()
//    bindFramebuffer: ƒ bindFramebuffer()
//    bindRenderbuffer: ƒ bindRenderbuffer()
//    bindTexture: ƒ bindTexture()
//    blendColor: ƒ blendColor()
//    blendEquation: ƒ blendEquation()
//    blendEquationSeparate: ƒ blendEquationSeparate()
//    blendFunc: ƒ blendFunc()
//    blendFuncSeparate: ƒ blendFuncSeparate()
//    bufferData: ƒ bufferData()
//    bufferSubData: ƒ bufferSubData()
//    canvas: (...)
//    checkFramebufferStatus: ƒ checkFramebufferStatus()
//    clear: ƒ clear()
//    clearColor: ƒ clearColor()
//    clearDepth: ƒ clearDepth()
//    clearStencil: ƒ clearStencil()
//    colorMask: ƒ colorMask()
//    compileShader: ƒ compileShader()
//    compressedTexImage2D: ƒ compressedTexImage2D()
//    compressedTexSubImage2D: ƒ compressedTexSubImage2D()
//    copyTexImage2D: ƒ copyTexImage2D()
//    copyTexSubImage2D: ƒ copyTexSubImage2D()
//    createBuffer: ƒ createBuffer()
//    createFramebuffer: ƒ createFramebuffer()
//    createProgram: ƒ createProgram()
//    createRenderbuffer: ƒ createRenderbuffer()
//    createShader: ƒ createShader()
//    createTexture: ƒ createTexture()
//    cullFace: ƒ cullFace()
//    deleteBuffer: ƒ deleteBuffer()
//    deleteFramebuffer: ƒ deleteFramebuffer()
//    deleteProgram: ƒ deleteProgram()
//    deleteRenderbuffer: ƒ deleteRenderbuffer()
//    deleteShader: ƒ deleteShader()
//    deleteTexture: ƒ deleteTexture()
//    depthFunc: ƒ depthFunc()
//    depthMask: ƒ depthMask()
//    depthRange: ƒ depthRange()
//    detachShader: ƒ detachShader()
//    disable: ƒ disable()
//    disableVertexAttribArray: ƒ disableVertexAttribArray()
//    drawArrays: ƒ drawArrays()
//    drawElements: ƒ drawElements()
//    drawingBufferColorSpace: (...)
//    drawingBufferHeight: (...)
//    drawingBufferWidth: (...)
//    enable: ƒ enable()
//    enableVertexAttribArray: ƒ enableVertexAttribArray()
//    finish: ƒ finish()
//    flush: ƒ flush()
//    framebufferRenderbuffer: ƒ framebufferRenderbuffer()
//    framebufferTexture2D: ƒ framebufferTexture2D()
//    frontFace: ƒ frontFace()
//    generateMipmap: ƒ generateMipmap()
//    getActiveAttrib: ƒ getActiveAttrib()
//    getActiveUniform: ƒ getActiveUniform()
//    getAttachedShaders: ƒ getAttachedShaders()
//    getAttribLocation: ƒ getAttribLocation()
//    getBufferParameter: ƒ getBufferParameter()
//    getContextAttributes: ƒ getContextAttributes()
//    getError: ƒ getError()
//    getExtension: ƒ getExtension()
//    getFramebufferAttachmentParameter: ƒ getFramebufferAttachmentParameter()
//    getParameter: ƒ getParameter()
//    getProgramInfoLog: ƒ getProgramInfoLog()
//    getProgramParameter: ƒ getProgramParameter()
//    getRenderbufferParameter: ƒ getRenderbufferParameter()
//    getShaderInfoLog: ƒ getShaderInfoLog()
//    getShaderParameter: ƒ getShaderParameter()
//    getShaderPrecisionFormat: ƒ getShaderPrecisionFormat()
//    getShaderSource: ƒ getShaderSource()
//    getSupportedExtensions: ƒ getSupportedExtensions()
//    getTexParameter: ƒ getTexParameter()
//    getUniform: ƒ getUniform()
//    getUniformLocation: ƒ getUniformLocation()
//    getVertexAttrib: ƒ getVertexAttrib()
//    getVertexAttribOffset: ƒ getVertexAttribOffset()
//    hint: ƒ hint()
//    isBuffer: ƒ isBuffer()
//    isContextLost: ƒ isContextLost()
//    isEnabled: ƒ isEnabled()
//    isFramebuffer: ƒ isFramebuffer()
//    isProgram: ƒ isProgram()
//    isRenderbuffer: ƒ isRenderbuffer()
//    isShader: ƒ isShader()
//    isTexture: ƒ isTexture()
//    lineWidth: ƒ lineWidth()
//    linkProgram: ƒ linkProgram()
//    makeXRCompatible: ƒ makeXRCompatible()
//    pixelStorei: ƒ pixelStorei()
//    polygonOffset: ƒ polygonOffset()
//    readPixels: ƒ readPixels()
//    renderbufferStorage: ƒ renderbufferStorage()
//    sampleCoverage: ƒ sampleCoverage()
//    scissor: ƒ scissor()
//    shaderSource: ƒ shaderSource()
//    stencilFunc: ƒ stencilFunc()
//    stencilFuncSeparate: ƒ stencilFuncSeparate()
//    stencilMask: ƒ stencilMask()
//    stencilMaskSeparate: ƒ stencilMaskSeparate()
//    stencilOp: ƒ stencilOp()
//    stencilOpSeparate: ƒ stencilOpSeparate()
//    texImage2D: ƒ texImage2D()
//    texParameterf: ƒ texParameterf()
//    texParameteri: ƒ texParameteri()
//    texSubImage2D: ƒ texSubImage2D()
//    uniform1f: ƒ uniform1f()
//    uniform1fv: ƒ uniform1fv()
//    uniform1i: ƒ uniform1i()
//    uniform1iv: ƒ uniform1iv()
//    uniform2f: ƒ uniform2f()
//    uniform2fv: ƒ uniform2fv()
//    uniform2i: ƒ uniform2i()
//    uniform2iv: ƒ uniform2iv()
//    uniform3f: ƒ uniform3f()
//    uniform3fv: ƒ uniform3fv()
//    uniform3i: ƒ uniform3i()
//    uniform3iv: ƒ uniform3iv()
//    uniform4f: ƒ uniform4f()
//    uniform4fv: ƒ uniform4fv()
//    uniform4i: ƒ uniform4i()
//    uniform4iv: ƒ uniform4iv()
//    uniformMatrix2fv: ƒ uniformMatrix2fv()
//    uniformMatrix3fv: ƒ uniformMatrix3fv()
//    uniformMatrix4fv: ƒ uniformMatrix4fv()
//    unpackColorSpace: (...)
//    useProgram: ƒ useProgram()
//    validateProgram: ƒ validateProgram()
//    vertexAttrib1f: ƒ vertexAttrib1f()
//    vertexAttrib1fv: ƒ vertexAttrib1fv()
//    vertexAttrib2f: ƒ vertexAttrib2f()
//    vertexAttrib2fv: ƒ vertexAttrib2fv()
//    vertexAttrib3f: ƒ vertexAttrib3f()
//    vertexAttrib3fv: ƒ vertexAttrib3fv()
//    vertexAttrib4f: ƒ vertexAttrib4f()
//    vertexAttrib4fv: ƒ vertexAttrib4fv()
//    vertexAttribPointer: ƒ vertexAttribPointer()
//    viewport: ƒ viewport()
//    constructor: ƒ WebGLRenderingContext()
//    Symbol(Symbol.toStringTag): "WebGLRenderingContext"
//    get canvas: ƒ canvas()
//    get drawingBufferColorSpace: ƒ drawingBufferColorSpace()
//    set drawingBufferColorSpace: ƒ drawingBufferColorSpace()
//    get drawingBufferHeight: ƒ drawingBufferHeight()
//    get drawingBufferWidth: ƒ drawingBufferWidth()
//    get unpackColorSpace: ƒ unpackColorSpace()
//    set unpackColorSpace: ƒ unpackColorSpace()
//    [[Prototype]]: Object


