package projects.testing

import p5.Sketch
import p5.core.P5

fun icons() = Sketch {
    Setup {
        createCanvas(1000, 1000)
        strokeWeight(25)
        noFill()

        fun share() {
            val t = createVector(800, 200)
            val l = createVector(200, 500)
            val b = createVector(800, 800)

            circle(t, 190)
            circle(l, 190)
            circle(b, 190)
            line(P5.Vector.lerp(l, t, 0.15), P5.Vector.lerp(l, t, 0.85))
            line(P5.Vector.lerp(l, b, 0.15), P5.Vector.lerp(l, b, 0.85))
        }

        fun download() {

        }

    }
}