package projects.huegene

import p5.Sketch
import p5.core.AUTO
import p5.core.Color
import p5.core.FilterMode
import p5.core.P5.*
import p5.core.P5.Vector.Companion.lerp
import p5.core.RenderMode
import p5.util.getValue
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun huegeneFlow() = Sketch {

    Setup {
        val canvas = createCanvas(512, 512)
        background(31, 31, 31, 0)
        pixelDensity(1)
        frameRate(1)

        val activePoints = mutableSetOf(createVector(width/2, height/2))

        val startColor = color(128, 128, 255, 255)
        console.log(startColor)
        val startColorVector = createVector(red(startColor), green(startColor), blue(startColor))

        val mutationChance = 0.1
        val colorStep = 10
        val noiseScl = 0.01

        console.log(activePoints)
        val radius = 12
        val neighborhood = buildList {
            (0..radius*4).forEach {
                val x = (12.0*sin(PI*it/(radius*2.0))).toInt()
                val y = (12.0*cos(PI*it/(radius*2.0))).toInt()
                val offset = createVector(x, y)
                console.log(x, y, offset, offset.x, offset.y)
                add(offset)
            }
        }.distinct()

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

        fun mutateColor(c: Color): Color {
            val mutations = Array(3) { if (random() < mutationChance) colorStep*((random()*2) - 1) else 0 }
            val r = red(c).toInt()
            val g = green(c).toInt()
            val b = blue(c).toInt()

            val newR = (r + mutations[0]).toInt().coerceIn(0, 255)
            val newG = (g + mutations[1]).toInt().coerceIn(0, 255)
            val newB = (b + mutations[2]).toInt().coerceIn(0, 255)

            val colorVec = (createVector(newR, newG, newB) - startColorVector).limited(64) + startColorVector

            return color(colorVec.x, colorVec.y, colorVec.z, 255)
        }

        fun flow(v: Vector): Vector {
            val xNoise = 2*noise(v.x*noiseScl, v.y*noiseScl) - 1
            val yNoise = 2*noise((v.x+42342.32)*noiseScl, (v.y+826.11)*noiseScl) - 1
            return createVector(xNoise, yNoise).normalized()
        }

        fun weightCandidate(center: Vector, offset: Vector): Number {
            return (flow(center) dot offset.normalized()) + 1.0
        }

        withPixels {
            colorArray[createVector(width/2, height/2)] = startColor
        }

        console.log(neighborhood)

        data class Plant(val oldLocation: Vector, val newLocation: Vector, val color: Color)

        val attemptsPerStep = 1
        val takeBestAttemptsNum = 1

        var addedPoints = 0
        val openNeighborhoodPreference by { 1.0-2.0*(addedPoints/(height*width)) }

        DrawWhileWithPixels( { activePoints.isNotEmpty() }, AUTO) step@{

            val weightedPlants = mutableListOf<Pair<Plant, Number>>()

            repeat(attemptsPerStep) attempt@{
                val chosenPoint = activePoints.randomOrNull() ?: return@step
                val chosenColor = colorArray[chosenPoint]
                val candidates = mutableListOf<Vector>()

                neighborhood.forEach { offset ->
                    val checkVector = chosenPoint + offset
                    if (checkVector.x.toInt() !in 0 until width || checkVector.y.toInt() !in 0 until height) {
                        return@forEach
                    }
                    val checkColor = colorArray[checkVector]
                    if (alpha(checkColor) == 0.0) {
                        candidates.add(offset)
                    }
                }

                if (candidates.isEmpty()) {
                    activePoints.remove(chosenPoint)
                }

                candidates.forEach { offset ->
                    val weight = weightCandidate(chosenPoint, offset)/2.0 + candidates.size.toDouble()*openNeighborhoodPreference
                    val newPlant = Plant(chosenPoint, chosenPoint+offset, chosenColor)
                    weightedPlants.add(newPlant to weight)
                }
            }

            for(chosenPlant in weightedPlants.sortedBy { -it.second.toDouble() }.take(takeBestAttemptsNum).map {it.first}) {
                val newColor = mutateColor(chosenPlant.color)
                (0..radius*2).forEach {
                    val plantPoint = lerp(chosenPlant.oldLocation, chosenPlant.newLocation, it / (radius*2.0)).toInts()
                    if(alpha(colorArray[plantPoint]) == 0.0) {
                        colorArray[plantPoint] = newColor
                        addedPoints++
                        activePoints.add(plantPoint)
                    }
                }
            }
        }.AfterDone {
            println("done")
        }


    }
}