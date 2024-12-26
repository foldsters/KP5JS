package p5.native.openSimplexNoise


@JsModule("open-simplex-noise")
@JsNonModule
external object OpenSimplexNoise {
    fun makeNoise2D(seed: Number): (Number, Number) -> Double
    fun makeNoise3D(seed: Number): (Number, Number, Number) -> Double
    fun makeNoise4D(seed: Number): (Number, Number, Number, Number) -> Double
}