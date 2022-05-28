package spectro

import p5.P5

fun P5.wavToTri(wv: Number): Array<Number> {

    val red = listOf(
        createVector(500, 0),
        createVector(550, 0.45),
        createVector(600, 1.15),
        createVector(650, 0.30),
        createVector(700, 0)
    ).interpolate(wv)

    val green = listOf(
        createVector(400, 0),
        createVector(450, 0.06),
        createVector(500, 0.35),
        createVector(550, 0.99),
        createVector(600, 0.69),
        createVector(650, 0.12),
        createVector(700, 0),
    ).interpolate(wv)

    val blue = listOf(
        createVector(350, 0),
        createVector(400, 0.11),
        createVector(450, 1.85),
        createVector(500, 0.24),
        createVector(550, 0),
    ).interpolate(wv)

    return arrayOf(red, green, blue)
}