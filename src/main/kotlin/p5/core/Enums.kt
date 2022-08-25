package p5.core

object CLOSE
object AUTO

enum class DescriptionMode(val nativeValue: String) {
    LABEL("label"),
    FALLBACK("fallback")
}

enum class ColorMode(val nativeValue: String) {
    RGB("rgb"),
    HSB("hsb"),
    HSL("hsl")
}

enum class FillStyle {
    GRADIENT,
    SOLID
}

enum class ArcMode(val nativeValue: String) {
    CHORD("chord"),
    PIE("pie"),
    OPEN("open")
}

enum class CenterMode(val nativeValue: String) {
    CENTER("center"),
    RADIUS("radius"),
    CORNER("corner"),
    CORNERS("corners")
}

enum class CapMode(val nativeValue: String) {
    ROUND("round"),
    SQUARE("butt"),
    PROJECT("square")
}

enum class JoinMode(val nativeValue: String) {
    MITER("miter"),
    BEVEL("bevel"),
    ROUND("round")
}

enum class PathMode(val nativeValue: Any) {
    POINTS(0),
    LINES(1),
    TRIANGLES(4),
    TRIANGLE_FAN(6),
    TRIANGLE_STRIP(5),
    QUADS("quads"),
    QUAD_STRIP("quad_strip"),
    TESS("tess")
}

enum class AlignMode(val nativeValue: String) {
    VERTICAL("vertical"),
    HORIZONTAL("horizontal")
}

enum class PositionMode(val nativeValue: String) {
    STATIC("static"),
    FIXED("fixed"),
    RELATIVE("relative"),
    STICKY("sticky"),
    INITIAL("initial"),
    INHERIT("inherit")
}

enum class CrossOriginMode(val nativeValue: String) {
    ANONYMOUS("anonymous"),
    USE_CREDENTIALS("use-credentials"),
    NONE("")
}

enum class TargetMode(val nativeValue: String) {
    BLANK("_blank"),
    SELF("_self"),
    PARENT("_parent"),
    TOP("_top")
}

enum class InputMode(val nativeValue: String) {
    TEXT("text"),
    PASSWORD("password")
}

enum class CaptureMode(val nativeValue: String) {
    AUDIO("audio"),
    VIDEO("video"),
    BOTH("")
}

enum class RenderMode(val nativeValue: String) {
    P2D("p2d"),
    WEBGL("webgl"),
    WEBGL2("webgl")
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

enum class AngleMode(val nativeValue: String) {
    RADIANS("radians"),
    DEGREES("degrees")
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

enum class DeviceOrientation(val nativeValue: String) {
    LANDSCAPE("landscape"),
    PORTRAIT("portrait")
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

enum class ImageExtension(val nativeValue: String) {
    PNG("png"),
    JPG("jpg")
}

enum class ImageMode(val nativeValue: String) {
    CENTER("center"),
    CORNER("corner"),
    CORNERS("corners")
}

enum class ScalarMode {
    X,
    XY,
    XYZ;
}

enum class TableMode(val nativeValue: String) {
    TSV("tsv"),
    CSV("csv"),
    HTML("html")
}

enum class MouseButton(val nativeValue: String) {
    CENTER("center"),
    LEFT("left"),
    RIGHT("right")
}

enum class JsonType(val nativeValue: String){
    JSON("json"),
    JSONP("jsonp")
}

