package projects.huegene

import p5.native.NativeP5
import p5.Sketch
import p5.core.Color
import p5.core.P5.*

fun huegene() = Sketch {

    Setup {
        createCanvas(1024, 1024)
        background(0, 0, 0, 0)

        val points = mutableListOf(createVector(0, 0))

        val startColor = color(255, 255, 255, 255)
        val mutationChance = 0.5
        val colorStep = 10

        frameRate(60)

        val neighborhood = listOf(
            createVector(-1, 0),
            createVector(1, 0),
            createVector(-1, 1),
            createVector(1, 1),
            createVector(-2, 1),
            createVector(2, 1)
        )

        fun mutateColor(c: Color): Color {
            val mutations = Array(3) { if (random() < mutationChance) colorStep*((random()*2) - 1) else 0 }
            val newColor = color(
                (red(c) + mutations[0]).toInt().coerceIn(0, 255),
                (red(c) + mutations[1]).toInt().coerceIn(0, 255),
                (red(c) + mutations[2]).toInt().coerceIn(0, 255),
                255)
            //console.log("newColor", newColor)
            return newColor
        }

        var pointsAdded = 1

//        withPixels {
//            var r = 0.0
//            var theta = 0.0
//            var newColor = startColor
//            while (r < width/2) {
//                val sVec = points[0] + NativeP5.Vector.fromAngle(theta)*r
//                sVec.x = sVec.x.toInt()
//                sVec.y = sVec.y.toInt()
//                newColor = mutateColor(newColor)
//                r += 0.001
//                theta += 0.0001
//                colorArray[sVec] = newColor
//                points.add(sVec)
//                pointsAdded++
//            }
//        }

        withPixels {
            for (x in 0 until width) {
                val newVec = createVector(x, 0)
                colorArray[newVec] = startColor
                points.add(newVec)
                pointsAdded++
            }
        }

        DrawWhileWithPixels( { points.isNotEmpty() }, 5000) {

            val chosenPoint = points.random()
            val chosenColor = colorArray[chosenPoint]
            val candidates = mutableListOf<Vector>()

            neighborhood.forEach { offset ->
                val checkVector = chosenPoint + offset
                if (checkVector.x.toInt() !in 0 until width || checkVector.y.toInt() !in 0 until height) {
                    return@forEach
                }
                val checkColor = colorArray[checkVector]
                if (alpha(checkColor) == 0.0) {
                    candidates.add(checkVector)
                }
            }

            if (candidates.size < 2) {
                points.remove(chosenPoint)
            }

            if (candidates.isNotEmpty()) {
                //console.log("new point")
                val newPoint = candidates.random()
                val newColor = mutateColor(chosenColor)
                colorArray[newPoint] = newColor
                points.add(newPoint)
                pointsAdded++
            }
        }
    }
}