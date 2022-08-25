package spectro

import p5.core.P5

fun P5.wavToTri(wv: Number): Array<Number> {

    val red = listOf(
        Pair(500, 0),
        Pair(550, 0.45),
        Pair(600, 1.15),
        Pair(650, 0.30),
        Pair(700, 0)
    ).interpolate(wv)

    val green = listOf(
        Pair(400, 0),
        Pair(450, 0.06),
        Pair(500, 0.35),
        Pair(550, 0.99),
        Pair(600, 0.69),
        Pair(650, 0.12),
        Pair(700, 0),
    ).interpolate(wv)

    val blue = listOf(
        Pair(350, 0),
        Pair(400, 0.11),
        Pair(450, 1.85),
        Pair(500, 0.24),
        Pair(550, 0),
    ).interpolate(wv)

    return arrayOf(red, green, blue)
}