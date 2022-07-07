package projects.hopper

import p5.NativeP5.*
import p5.P5
import p5.P5.*
import p5.Sketch
import p5.util.*
import kotlin.math.PI
import kotlin.math.max

fun P5.genHopperTop(crystalSize: Int): List<Int> {

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

fun P5.genHopperPoints(hopperLayers: List<List<Int>>, seedHeight: Int): MutableList<Vector> {

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

fun hopperClouds() = Sketch {

    Setup {

        createCanvas(512, 512, RenderMode.P2D)
        background(0)
        pixelDensity(1)

        val crystalSize = 64
        val seedHeight = 16
        val elevation = 0.6
        val height = 5
        val scale = 4
        val t = 0.5
        val UP = createVector(height, 0)
        val DOWN = createVector(-height, 0)
        val center = createVector(200, 520)

        val hopperColors = listOf(
            createVector(255, 255, 0),
            createVector(255, 0, 255),
            createVector(0, 255, 255),
            createVector(0, 255, 128)
        )

        val cloudColors = listOf(
            createVector(38, 85, 113),
            createVector(66, 113, 169),
            createVector(142, 157, 206),
            createVector(198, 180, 221),
            createVector(245, 205, 225)
        )

        abstract class Point(val location: Vector, val id: Double): Comparable<Point> {
            val x: Double get() = location.x.toDouble()
            val y: Double get() = location.y.toDouble()
            val z: Double get() = location.z.toDouble()

            override fun compareTo(other: Point): Int = compareValuesBy(this, other, {-it.z}, {id}, {it.x+it.y})
        }

        class HopperPoint(location: Vector, id: Double): Point(location, id)
        class CloudPoint(location: Vector, id: Double): Point(location, id)

        fun genClouds(minHeight: Int, maxHeight: Int) = buildList {
            for(x in -300 until 300) {
                for(y in -300 until 300) {
                    val n = simplexNoise(x/20.0, y/20.0)*3 + simplexNoise(x/10.0, y/10.0)
                    val zHeight = map(-1, 1, minHeight, maxHeight, n)
                    repeat(4) {
                        val z = it + zHeight
                        val id = (maxHeight-z)/maxHeight
                        add(CloudPoint(createVector(x, y, z), id))
                    }
                }
            }
        }
        val clouds = genClouds(10, 30)

        val hopperPoints = buildList {
            repeat(60) {id ->
                genHopperPoints(genHopperLayers(genHopperTop(crystalSize)), seedHeight).forEach {
                    add(HopperPoint(it, id.toDouble()))
                }
            }
        }

        var rise = UP
        val (UP_RIGHT, DOWN_RIGHT, DOWN_LEFT, UP_LEFT) = Array(4) { Vector.fromAngle(2*PI*(it+t)/4.0)*createVector(elevation, 1.0)*scale }




        val leftPanel  = listOf(createVector(0, 0), UP_LEFT, UP_LEFT+rise, rise)
        val rightPanel = listOf(createVector(0, 0), UP+UP_RIGHT, UP_RIGHT+rise, rise)
        val topPanel   = listOf(rise, rise+UP_LEFT, rise+UP_LEFT+UP_RIGHT, rise+UP_RIGHT)

        fun drawPanel(panel: List<Vector>, offset: Number) {

        }

        DrawFor(hopperPoints.sorted(), 100) {
            with(it) {
                val pos = center + DOWN_LEFT*(x-128) + DOWN_RIGHT*(y-128) + DOWN*z

                quad(pos.x, pos.y, pos+UP_LEFT, pos+UP_LEFT+rise, pos+rise)
            }
        }
    }



}