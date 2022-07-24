package projects.huegene

import p5.NativeP5
import p5.Sketch

fun huegeneFlow() = Sketch {

    Setup {
        createCanvas(512, 512)
        background(0, 0, 0, 0)

        val points = mutableListOf(createVector(width/2, height/2))

        val startColor = color(128, 128, 255, 255)
        val startColorVector = createVector(red(startColor), green(startColor), blue(startColor))

        val mutationChance = 0.1
        val colorStep = 10
        val stepsPerFrame = 1000
        val noiseScl = 0.01
        val flowAttempts = 10

        console.log(points)

        val neighborhood = mutableListOf(
            createVector(0, 1),
            createVector(1, 1),
            createVector(1, 0),
            createVector(1, -1),
            createVector(0, -1),
            createVector(-1, -1),
            createVector(-1, 0),
            createVector(-1, 1)
        )

        neighborhood += neighborhood.map {it*2}

//        fun mutateColor(c: NativeP5.Color): NativeP5.Color {
//            val mutations = Array(3) { if (random() < mutationChance) colorStep*((random()*2) - 1) else 0 }
//            val r = red(c).toInt()
//            val g = green(c).toInt()
//            val b = blue(c).toInt()
//
//            val minC = listOf(r, g, b).minByOrNull { it }!!
//            val maxC = listOf(r, g, b).maxByOrNull { it }!!
//
//            val newR = map(r + mutations[0], minC, maxC, 0, 255, true)
//            val newG = map(g + mutations[1], minC, maxC, 0, 255, true)
//            val newB = map(b + mutations[2], minC, maxC, 0, 255, true)
//
//            val newColor = color(newR, newG, newB, 255)
//            //console.log("newColor", newColor)
//            return newColor
//        }

        fun mutateColor(c: NativeP5.Color): NativeP5.Color {
            val mutations = Array(3) { if (random() < mutationChance) colorStep*((random()*2) - 1) else 0 }
            val r = red(c).toInt()
            val g = green(c).toInt()
            val b = blue(c).toInt()

            val newR = (r + mutations[0]).toInt().coerceIn(0, 255)
            val newG = (g + mutations[1]).toInt().coerceIn(0, 255)
            val newB = (b + mutations[2]).toInt().coerceIn(0, 255)

            val colorVec = (createVector(newR, newG, newB) - startColorVector).limit(64) + startColorVector

            val newColor = color(colorVec.x, colorVec.y, colorVec.z, 255)
            //console.log("newColor", newColor)
            return newColor
        }

        fun flow(v: NativeP5.Vector): NativeP5.Vector {
            val xNoise = 2*noise(v.x*noiseScl, v.y*noiseScl) - 1
            val yNoise = 2*noise((v.x+42342.32)*noiseScl, (v.y+826.11)*noiseScl) - 1
            val flowVector = createVector(xNoise, yNoise).normalize()
            return flowVector
        }

        fun <T> weightedChoice(weightedCandidates: List<Pair<T, Number>>): T? {
            if (weightedCandidates.isEmpty()) return null

            val weightSum = weightedCandidates.sumOf { it.second.toDouble() }
            var thresholdWeight = random()*weightSum
            for ((candidate, weight) in weightedCandidates) {
                if (thresholdWeight <= weight) return candidate
                thresholdWeight -= weight
            }

            return null
        }

        fun weightCandidate(center: NativeP5.Vector, offset: NativeP5.Vector): Number {
            return (flow(center) dot offset.normalize()) + 1
        }

        var pointsAdded = 0

        withPixels {
            colorArray[points[0]] = startColor
            pointsAdded++
        }

        data class Plant(val location: NativeP5.Vector, val color: NativeP5.Color)

        Draw {

            console.log("draw!", pointsAdded)

            withPixels {

                repeat(stepsPerFrame) {

                    val weightedPlants = mutableListOf<Pair<Plant, Number>>()

                    repeat(flowAttempts) attempt@{

                        if (points.isEmpty()) {
                            noLoop()
                            console.log("Done: Points Added: ", pointsAdded)
                            return@withPixels
                        }

                        val chosenPoint = points.random()
                        val chosenColor = colorArray[chosenPoint]
                        val candidates = mutableListOf<NativeP5.Vector>()

                        neighborhood.forEach { offset ->
                            val checkVector = chosenPoint + offset
                            if (checkVector.x.toInt() !in 0 until width || checkVector.y.toInt() !in 0 until height) {
                                return@forEach
                            }
                            val checkColor = colorArray[checkVector]
                            if (alpha(checkColor) == 0) {
                                candidates.add(offset)
                            }
                        }

                        if (candidates.isEmpty()) {
                            points.remove(chosenPoint)
                        }

                        candidates.forEach { offset ->
                            val weight = weightCandidate(chosenPoint, offset)/2 + candidates.size.toDouble()
                            val newPlant = Plant(chosenPoint+offset, chosenColor)
                            weightedPlants.add(newPlant to weight)
                        }
                    }

                    val chosenPlant = weightedPlants.maxByOrNull { it.second.toDouble() }?.first ?: return@repeat

                    colorArray[chosenPlant.location] = mutateColor(chosenPlant.color)
                    points.add(chosenPlant.location)
                    pointsAdded++

                }
            }
        }
    }
}