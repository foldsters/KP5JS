package p5.openSimplexNoise

@JsModule("open-simplex-noise")
@JsNonModule
external object OpenSimplexNoise {
    fun makeNoise2D(seed: Number): (Number, Number) -> Number
    fun makeNoise3D(seed: Number): (Number, Number, Number) -> Number
    fun makeNoise4D(seed: Number): (Number, Number, Number, Number) -> Number
}