package scripts.pseudoPerlin

import p5.Sketch

fun pseudoPerlin() = Sketch {

    setup {
        createCanvas(512, 512)

        drawFragment { x, y, t ->
            val n = (simplexNoise(x*10/width, y*10.0/height, t/10.0) + 1.0)/2.0
            color(n*255)
        }
    }
}
