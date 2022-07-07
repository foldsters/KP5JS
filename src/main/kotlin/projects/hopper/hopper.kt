package projects.hopper

import p5.NativeP5.*
import p5.P5.*
import p5.Sketch
import p5.util.*
import kotlin.math.max

fun hopper() = Sketch {

    Setup {

        createCanvas(512, 512, RenderMode.P2D)
        background(0)
        pixelDensity(1)

        fun genHopperTop(crystalSize: Int): List<Int> {

            fun spiral(): List<Int> = buildList {
                var length = randInt(2, crystalSize)
                while(length >= 2) {
                    add(length)
                    length = randInt(length/2, length-1)
                }
            }

            val hopper = spiral().reversed() + spiral()
            return hopper.map {h -> h + (h+1)%2 }
        }

        fun genHopperLayers(hopperTop: List<Int>) = buildList {
            var layer = hopperTop
            while(layer.sum() > 5) {
                add(layer)
                layer = layer.map { h -> max(0, h-2) }
            }
        }

        fun genHopperPoints(hopperLayers: List<List<Int>>, seedHeight: Int): MutableList<Vector> {

            val orientation = randInt(0, 4)
            val majorSide = hopperLayers[0].indexOfMax() - 1

            val dirs = listOf(
                createVector(1, 0, 0),
                createVector(0, 1, 0),
                createVector(-1, 0, 0),
                createVector(0, -1, 0)
            )

            fun direction(d: Int) = dirs[(d+orientation+4)%4]

            var start = createVector(0, 0, 2*randInt(0, seedHeight/2))
            start += direction(majorSide  )*randInt(seedHeight*2, seedHeight*4)
            start += direction(majorSide-1)*randInt(-seedHeight*2, seedHeight*2)
            start = start.map { it + it%2 }
            var pos: Vector
            val hopperPoints = mutableListOf<Vector>()

            hopperLayers.forEach { layer ->
                start += createVector(0, 0, 1)
                val startSide = layer.indexOfFirst { it > 0 }
                start += direction(startSide) + direction(startSide+1)
                pos = start.copy()
                layer.forEachIndexed { side, sideLength ->
                    val posCache = pos.copy()
                    repeat(sideLength) {
                        pos = posCache+direction(side)*it
                        hopperPoints.add(pos)
                    }
                }
            }

            return hopperPoints
        }

        val colors = listOf(
            createVector(255, 255, 0),
            createVector(255, 0, 255),
            createVector(0, 255, 255),
            createVector(0, 255, 128)
        )

        abstract class Point(val location: Vector): Comparable<Point> {
            val x: Double get() = location.x.toDouble()
            val y: Double get() = location.y.toDouble()
            val z: Double get() = location.z.toDouble()

            override fun compareTo(other: Point): Int = compareValuesBy(other, this, {it.z}, {-it.y}, {-it.x})
        }

        class HopperPoint(location: Vector): Point(location)
        class CloudPoint(location: Vector): Point(location)

        val crystalSize = 64
        val seedHeight = 16

        val hopperPoints = buildList {
            repeat(60) {
                genHopperPoints(genHopperLayers(genHopperTop(crystalSize)), seedHeight).forEach {
                    add(HopperPoint(it))
                }
            }
        }

        DrawForWithPixels(hopperPoints.sorted(), 1000) {
            with(it) {
                val s = location.xy.magSq()/1000.0
                val m = max(0.7, z/(7 + s)) - 0.4
                val colorVector = colors.interpolate(m)*(z%2)
                colorArray[location.xy + createVector(100, 100)] = colorVector.toColor()
            }
        }
    }



}