package scripts.huegene

import p5.NativeP5
import p5.Sketch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun huegeneFlow() = Sketch {

    setup {
        createCanvas(1024, 1024)
        background(0, 0, 0, 0)

        val points = mutableListOf(createVector(width/2, height/2))

        val startColor = color(200, 200, 200, 255)
        val mutationChance = 0.1
        val colorStep = 10
        val stepsPerFrame = 1000
        val noiseScl = 0.01
        val flowAttempts = 10

        frameRate(60)

        val neighborhood = listOf(
            createVector(0, 1),
//            createVector(1, 1),
            createVector(1, 0),
//            createVector(1, -1),
            createVector(0, -1),
//            createVector(-1, -1),
            createVector(-1, 0),
//            createVector(-1, 1)
        )

        fun mutateColor(c: NativeP5.Color): NativeP5.Color {
            val mutations = Array(3) { if (random() < mutationChance) colorStep*((random()*2) - 1) else 0 }
            val r = red(c).toInt()
            val g = green(c).toInt()
            val b = blue(c).toInt()

            val minC = 255 - max(r, max(g, b))

            val newColor = color(
                (r + mutations[0] + minC).toInt().coerceIn(0, 255),
                (g + mutations[1] + minC).toInt().coerceIn(0, 255),
                (b + mutations[2] + minC).toInt().coerceIn(0, 255),
                255)
            //console.log("newColor", newColor)
            return newColor
        }

        data class PlantInfo(val vector: NativeP5.Vector, val color: NativeP5.Color, val distance: Number)

        fun flow(v: NativeP5.Vector): NativeP5.Vector {
            val angle = noise(v.x*noiseScl, v.y*noiseScl)*4*PI
            return NativeP5.Vector.fromAngle(angle)
        }

        fun candidatesInfo(
            center: NativeP5.Vector,
            color: NativeP5.Color,
            neighbors: List<NativeP5.Vector>
        ): List<PlantInfo> {
            val flowDirection = flow(center)
            return neighbors.map { offset ->
                val distance = abs((flowDirection dot offset.limit(1)).toDouble())
                PlantInfo(center + offset, color, distance)
            }
        }

        var pointsAdded = 1

        withPixels {
            colorArray[points[0]] = startColor
            pointsAdded++
        }

        draw {

            console.log("draw!", pointsAdded)
            val frameStartTime = millis()

            withPixels {
                var steps = stepsPerFrame
                while (steps > 0) {

                    val plantInfo = mutableListOf<PlantInfo>()

                    repeat(flowAttempts) {

                        val chosenPoint = points.random()
                        val chosenColor = colorArray[chosenPoint]
                        val candidates = mutableListOf<NativeP5.Vector>()

                        neighborhood.forEach { offset ->
                            val checkVector = chosenPoint + offset
                            if (checkVector.x !in 0 until width || checkVector.y !in 0 until height) {
                                return@forEach
                            }
                            val checkColor = colorArray[checkVector]
                            if (alpha(checkColor) == 0) {
                                candidates.add(offset)
                            }
                        }

                        if (candidates.isEmpty()) {
                            points.remove(chosenPoint)
                        } else {
                            val localPlantInfo = candidatesInfo(chosenPoint, mutateColor(chosenColor), candidates)
                            plantInfo.addAll(localPlantInfo)
                        }

                    }

                    val newPlant = plantInfo.minByOrNull { it.distance.toDouble() } ?: return@withPixels
                    colorArray[newPlant.vector] = newPlant.color
                    points.add(newPlant.vector)
                    pointsAdded++
                    steps--

                    if ((millis() - frameStartTime) > 500) {
                        return@withPixels
                    }
                }
            }

            if (points.isEmpty()) {
                noLoop()
                console.log("Done: Points Added: ", pointsAdded)
            }

        }
    }
}