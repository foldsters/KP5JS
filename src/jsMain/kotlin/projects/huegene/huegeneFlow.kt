@file:OptIn(ExperimentalJsExport::class)

package projects.huegene

import p5.Sketch
import p5.core.AUTO
import p5.core.Color
import p5.core.P5.*
import p5.util.*
import kotlin.math.abs

@JsExport
fun HuegeneFlow() = Sketch {

    Setup {
        val canvas = createCanvas(1024, 1024)
        pixelDensity(1)
        frameRate(30)
        background(31, 31, 31, 254)

        val startLocation = createVector(width/2, height/2)
        val activePoints = mutableMapOf(startLocation to 0.0)

        val startColor = color(128, 128, 255, 255)
        val startColorVector = createVector(red(startColor), green(startColor), blue(startColor))

        val mutationChance = 0.1
        val colorStep = 10
        val noiseScl = 0.01

        val outerRadius = 3
        val innerRadius = 0
        val neighborhood = buildList {
            for(x in -outerRadius..outerRadius) {
                for (y in -outerRadius..outerRadius) {
                    if ((x*x + y*y) in (innerRadius*innerRadius)..(outerRadius*outerRadius) ) {
                        add(createVector(x, y))
                    }
                }
            }
        }.distinct()

        console.log(neighborhood)

        val touching = listOf(
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

        fun PixelScope.getEndpoint(plantCenter: Vector): Pair<Vector, Double>? {
            val flowDirection = flow(plantCenter)
            return neighborhood.filter {
                (it + plantCenter).inFrame() && colorArray[it + plantCenter].isTransparent()
            }.map{ plantCenter+it to abs(it.normalized() dot flowDirection) * it.mag()}.maxByOrNull { it.second }
        }

        withPixels {
            colorArray[createVector(width/2, height/2)] = startColor
        }

        var addedPoints = 0

        val progress by { addedPoints/((height*width).toDouble()) }

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

        fun PixelScope.touchingIsTransparent(plantCenter: Vector): List<Vector> {
            return touching.map { plantCenter+it }.filter { colorArray[it].isTransparent() && it.inFrame() }
        }

        fun PixelScope.getPlant(plantCenter: Vector): Pair<Vector, Double>? {
            val plantColor = colorArray[plantCenter]
            val touchingIsTransparent = touchingIsTransparent(plantCenter)
            val plantEndToWeight = getEndpoint(plantCenter)
            if(plantEndToWeight == null || touchingIsTransparent.size <= 2) {
                touchingIsTransparent.forEach {
                    colorArray[it] = color(progress*255)//mutateColor(plantColor)
                    val plantCenterToWeight = getEndpoint(it) ?: return@forEach
                    activePoints[it] = plantCenterToWeight.second
                    addedPoints++
                }
                activePoints.remove(plantCenter)
                return null
            }
            return plantEndToWeight
        }


        DrawWhileWithPixels( { activePoints.isNotEmpty() }, AUTO) step@{
            val plantCenterToWeight = activePoints.maxByOrNull { it.value } ?: return@step
            val plantCenter = plantCenterToWeight.key
            val plantEnd = getPlant(plantCenter) ?: return@step
            activePoints[plantCenter] = plantEnd.second
            val newColor = mutateColor(colorArray[plantCenter])
            (0..outerRadius*2).map {
                Vector.lerp(plantCenter, plantEnd.first, it / (outerRadius * 2.0)).toInts()
            }.distinct().forEach { plantPoint ->
                if (colorArray[plantPoint].isTransparent()) {
                    colorArray[plantPoint] = color(progress*255)
                    addedPoints++
                    activePoints[plantPoint] = plantEnd.second
                }
            }
        }.AfterFrame {
            progressText.html("Progress: ${(progress*100.0).toFixed(2)}%")
            percentActive.html("Percent Active: ${(100.0*activePoints.size/(width*height.toDouble())).toFixed(2)}%")
        }.AfterDone {
            println("done")
        }

//        DrawWhileWithPixels( { activePoints.isNotEmpty() }, AUTO) step@{
//            val plantCenter = activePoints.randomOrNull() ?: return@step
//            var plant = plants[plantCenter] ?: makePlant(plantCenter)
//            plant = prunePlant(plant) ?: return@step
//            plant.branches.withEach {
//                val testWeight = weight*points.size
//                if (testWeight >= weightAcceptanceLevel) {
//                    points.forEach { branchPoint ->
//                        if (colorArray[branchPoint].isTransparent()) {
//                            addedPoints++
//                            activePoints.add(branchPoint)
//                        }
//                        colorArray[branchPoint] = color
//                    }
//                    plants[center]?.branches?.remove(this)
//                    accepted += 1
//                }
//                totalSeen += 1
//                updateAcceptanceLevel()
//            }
//        }.AfterFrame {
//            updateLayout()
//        }.AfterDone {
//            println("done")
//        }




    }
}