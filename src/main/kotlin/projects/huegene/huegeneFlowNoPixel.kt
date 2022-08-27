package projects.huegene

import p5.Sketch
import p5.core.AUTO
import p5.core.Color
import p5.core.P5.*
import p5.core.P5.Vector.Companion.lerp
import p5.util.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

fun huegeneFlowNoPixel() = Sketch {

    Setup {
        val canvas = createCanvas(512, 512)
        pixelDensity(1)
        frameRate(30)
        background(31, 31, 31, 254)
        strokeWeight(2)

        val activePoints = mutableSetOf(createVector(width/2, height/2))

        val startColor = color(128, 128, 255, 255)
        val startColorVector = createVector(red(startColor), green(startColor), blue(startColor))

        val mutationChance = 0.1
        val colorStep = 10
        val noiseScl = 0.01

        val radius = 12 
        val sectors = 60
        val neighborhood = buildList {
            (0..sectors).forEach {
                val x = (12.0*sin(2.0*PI*it/(sectors))).toInt()
                val y = (12.0*cos(2.0*PI*it/(sectors))).toInt()
                val offset = createVector(x, y)
                add(offset)
            }
        }.distinct()

        val touching = listOf(
            createVector(0, 0),
            createVector(0, -1),
            createVector(0, 1),
            createVector(-1, 0),
            createVector(1, 0)
        )

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

            return ((createVector(newR, newG, newB) - startColorVector).limited(64) + startColorVector).toColor()
        }

        fun flow(v: Vector): Vector {
            val xNoise = 2*noise(v.x*noiseScl, v.y*noiseScl) - 1
            val yNoise = 2*noise((v.x+42342.32)*noiseScl, (v.y+826.11)*noiseScl) - 1
            return createVector(xNoise, yNoise).normalized()
        }

        fun weightBranch(center: Vector, offset: Vector): Double {
            return flow(center) dot offset.normalized()
        }

        withPixels {
            colorArray[createVector(width/2, height/2)] = startColor
        }

        data class Branch(var points: List<Vector>, val center: Vector, val weight: Double, val color: Color)
        data class Plant(val plantCenter: Vector, var branches: MutableList<Branch>)

        var addedPoints = 0

        val progress by { addedPoints/((height*width).toDouble()) }

        val attemptsPerStep = 15
        val takeBestAttemptsNum = 2//by { map(progress, 0, 1, 5, attemptsPerStep).toInt() }

        val progressText = createP("Progress: ")
        val percentActive = createP("Percent Active: ")

        Layout {
            Column {
                add(canvas)
                progressText.html("Progress: ${(progress*100.0).toFixed(2)}%")
                percentActive.html("Percent Active: ${(100.0*activePoints.size/(width*height.toDouble())).toFixed(2)}%")
                add(progressText) {
                    size(400, 100, 2)
                }
                add(percentActive) {
                    size(400, 100, 2)
                }
            }
        }

        val plants = mutableMapOf<Vector, Plant>()
        val candidateBranches = mutableListOf<Branch>()
        //val pointAttempts = mutableMapOf<Vector, Double>()

        fun makePlant(plantCenter: Vector): Plant {
            val plantColor = get(plantCenter)
            val branches = neighborhood.mapTo(mutableListOf()) { offset ->
                val weight = weightBranch(plantCenter, offset)
                val branchPoints = (0..radius*2).map {
                    lerp(plantCenter, plantCenter+offset, it / (radius * 2.0)).toInts()
                }.distinct().filter { it.inFrame() }.toMutableList()
                Branch(branchPoints, plantCenter, weight, mutateColor(plantColor))
            }
            return Plant(plantCenter, branches)
        }

        fun prunePlant(plant: Plant): Plant? {
            plant.branches.forEach {branch ->
                branch.points = branch.points.filter { point ->
                    get(point).isTransparent()
                }
            }
            plant.branches = plant.branches.filter { branch -> branch.points.isNotEmpty() }.toMutableList()
            if(plant.branches.isEmpty()) {
                plants.remove(plant.plantCenter)
                activePoints.remove(plant.plantCenter)
                return null
            }
            plants[plant.plantCenter] = plant
            return plant
        }

        DrawWhile( { activePoints.isNotEmpty() }, AUTO) step@{

            candidateBranches.clear()

            repeat(attemptsPerStep) attempt@{
                val plantCenter = activePoints.randomOrNull() ?: return@step
                val plant = plants[plantCenter] ?: makePlant(plantCenter)
                prunePlant(plant)?.apply { candidateBranches += branches }
            }
            candidateBranches.takeMaxBy(takeBestAttemptsNum) { it.weight*it.points.size }.withEach {
                stroke(color)
                points.forEach {
                    activePoints.add(it)
                    if(get(it).isTransparent()) addedPoints++
                }
                line2D(points.first(), points.last())
                plants[center]?.branches?.remove(this)
            }
        }.AfterFrame {
            updateLayout()
        }.AfterDone {
            println("done")
        }




    }
}