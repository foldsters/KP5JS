package projects.hopper

import p5.NativeP5.*
import p5.P5
import p5.P5.*
import p5.P5.MouseButton.*
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

fun P5.genHopperLayers(hopperTop: List<Int>) = buildList {
    var layer = hopperTop
    val minLayerLength = randInt(10, 20)
    while(true) {
        add(layer)
        if (layer.sum() <= minLayerLength) break
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
    start += direction(majorSide  )*randInt(seedHeight*2, seedHeight*4)*1.3
    start += direction(majorSide-1)*randInt(-seedHeight*2, seedHeight*2)*1.3
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
        val canvas = createCanvas(1024, 512, RenderMode.P2D)
        background(0)
        pixelDensity(1)
        noStroke()
        val screenCenter = createVector(width/2, height/2)

        val coordBuffer = createGraphics(1024, 512, RenderMode.P2D, hide=false).apply {
            noStroke()
            pixelDensity(1)
        }
        val mouseBuffer = createGraphics(1024, 512, RenderMode.P2D, hide=false).apply {
            noStroke()
            pixelDensity(1)
        }

        val hopperColors = mapOf(
            0.0 to createVector(255, 240, 100),
            0.8 to createVector(255, 0, 255),
            2.0 to createVector(0, 255, 255),
            3.0 to createVector(0, 255, 128)
        )

        val cloudColors = listOf(
            createVector(38, 85, 113),
            createVector(66, 113, 169),
            createVector(142, 157, 206),
            createVector(198, 180, 221),
            createVector(245, 205, 225)
        )

        abstract class Point(val location: Vector, var id: Double): Comparable<Point> {
            val x: Double get() = location.x.toDouble()
            val y: Double get() = location.y.toDouble()
            val z: Double get() = location.z.toDouble()
            abstract val color: Vector

            override fun compareTo(other: Point): Int = compareValuesBy(this, other, {-it.z}, {it.id}, {it.x+it.y})
        }

        class HopperPoint(location: Vector, id: Double): Point(location, id) {
            override val color get() = hopperColors.interpolate((z/5.0)-id/60 + 2)
        }
        class CloudPoint(location: Vector, id: Double): Point(location, id) {
            override val color get() = cloudColors.interpolate(4*id)
        }

        fun genClouds(minHeight: Int, maxHeight: Int, radius: Int) = buildList {
            for(x in -radius until radius) {
                for(y in -radius until radius) {
                    if(x*x + y*y > radius*radius) continue
                    val n = simplexNoise(x/20.0, y/20.0)*3 + simplexNoise(x/10.0, y/10.0)
                    val z = map(n/4.0, -1, 1, -maxHeight, -minHeight).toDouble()
                    val id = (z+minHeight)/minHeight
                    add(CloudPoint(createVector(x, y, z), id))
                }
            }
        }

        val clouds = genClouds(-30, 10, 120)

        val crystalSize = 64
        val seedHeight = 16

        val hopperPoints = buildList {
            repeat(30) { id ->
                genHopperPoints(genHopperLayers(genHopperTop(crystalSize)), seedHeight).forEach {
                    add(it)
                }
            }
        }.centerize().map { HopperPoint(it, 15.0) }

        var elevation = 0.6
        var scale = 1.0
        var azimuth by cache { 3.5 }
        azimuth = (azimuth + 8.0)%4.0

        var center = createVector(0, 0)

        val allPoints = hopperPoints + clouds

        val sort1 = allPoints.sortedWith(compareBy<Point> {-it.z}.thenBy {- it.x + it.y })
        val sort2 = allPoints.sortedWith(compareBy<Point> {-it.z}.thenBy {- it.x - it.y })
        val sort3 = allPoints.sortedWith(compareBy<Point> {-it.z}.thenBy {  it.x - it.y })
        val sort4 = allPoints.sortedWith(compareBy<Point> {-it.z}.thenBy {  it.x + it.y })

        val sorts = listOf(sort1, sort2, sort3, sort4)

        var ti = azimuth.toInt()
        var tf = azimuth-ti
        var tc = (1+(azimuth*0.25))*0.25
        var UP = createVector(0, -scale)
        var DOWN = createVector(0, scale)
        var (UP_RIGHT, DOWN_RIGHT, DOWN_LEFT, UP_LEFT) = Array(4) { (Vector.fromAngle(2*PI*(tf+3.0+it)/4.0)*createVector(1.0, elevation)*scale) }

        fun updateCamera() {
            ti = azimuth.toInt()
            tf = azimuth-ti
            tc = (1+(azimuth*0.25))*0.25
            UP = createVector(0, -scale)
            DOWN = createVector(0, scale)
            val directions =  Array(4) { (Vector.fromAngle(2*PI*(tf+3.0+it)/4.0)*createVector(1.0, elevation)*scale) }
            UP_RIGHT = directions[0]
            DOWN_RIGHT = directions[1]
            DOWN_LEFT = directions[2]
            UP_LEFT = directions[3]
        }
        strokeWeight(5)
        updateCamera()

        fun draw() {

            loop()
            background(255, 255)
            coordBuffer.background(0, 255)
            updateCamera()
            noStroke()

            DrawFor(sorts[ti], 500) {
                with(it) {
                    val (px, py) = when(ti) {
                        0 -> y to -x
                        1 ->  -x to -y
                        2 -> -y to x
                        else -> x to y
                    }

                    val pos = center + DOWN_LEFT*px + DOWN_RIGHT*py + DOWN*z + screenCenter
                    coordBuffer.fill((createVector(px, py, z) + 128).toColor())

                    if (it is HopperPoint) {
                        var c = (color*(0.75-tc)).toColor()
                        fill(c)
                        quad(pos, pos+UP_RIGHT, pos+UP_RIGHT+UP, pos+UP)
                        c = (color*tc).toColor()
                        fill(c)
                        quad(pos+1, pos+UP_LEFT, pos+UP_LEFT+UP, pos+UP)
                        c = color.toColor()
                        fill(c)
                        buildShape2D { listOf(pos+UP, pos+UP+UP_LEFT, pos+UP+UP_RIGHT+UP_LEFT, pos+UP+UP_RIGHT).dilate(1.1).addVertices() }

                        coordBuffer.buildShape2D { addVertices(pos, pos+UP_LEFT, pos+UP_LEFT+UP, pos+UP_LEFT+UP+UP_RIGHT, pos+UP+UP_RIGHT, pos+UP_RIGHT) }
                    } else {
                        val U = UP*8
                        val UR = UP_RIGHT
                        val UL = UP_LEFT
                        fill(color.toColor(128))
                        buildShape2D(CLOSE) { addVertices(pos, pos+UL, pos+UL+U, pos+UL+U+UR, pos+U+UR, pos+UR) }

                        coordBuffer.buildShape2D { addVertices(pos, pos+UL, pos+UL+U, pos+UL+U+UR, pos+U+UR, pos+UR) }
                    }

                }

            }
        }

        draw()

        fun updateMouseBuffer() {
            mouseBuffer.background(0, 255)
            mouseBuffer.withPixels {
                for(x in 0..width step 3) {
                    for(y in 0 .. height step 3) {
                        val XYSC = createVector(x, y)
                        val XYSC2 = XYSC - createVector(width/2, height/2)
                        val XYWC = createVector(XYSC2 cross2 DOWN_RIGHT, DOWN_LEFT cross2 XYSC2)/(DOWN_LEFT cross2 DOWN_RIGHT)
                        if (-128 <= XYWC.x && XYWC.x <= 128 && -128 <= XYWC.y && XYWC.y <= 128)
                            colorArray[XYSC] = (XYWC + 128).toColor()
                    }
                }
            }
        }


        MouseDragged {
            when(mouseButton) {
                CENTER -> {
                    azimuth = (azimuth - map(mouseY, 0, height, -4, 4)*movedX/width + 4)%4.0
                    elevation = (elevation + 4.0*(movedY/height)).toDouble().coerceIn(0.0, 1.0)
                }
                LEFT -> {
                    center += createVector(movedX, movedY)
                }
                else -> return@MouseDragged
            }
            updateMouseBuffer()

            draw()
        }

        MouseWheel {
            updateCamera()
            val mouseSC = createVector(mouseX, mouseY)
            val mouseSC2 = createVector(mouseX-width/2, mouseY-height/2)
            val mousePlaneWC = createVector(mouseSC2 cross2 DOWN_RIGHT, DOWN_LEFT cross2 mouseSC2)/(DOWN_LEFT cross2 DOWN_RIGHT)
            var coordMapWC: Vector = createVector(0, 0)
            coordBuffer.withPixels {
                val bufferColor = colorArray[mouseSC]
                coordMapWC = bufferColor.toVector() - 128
            }
            updateMouseBuffer()
            println(coordMapWC, mousePlaneWC)
            val factor = if(delta > 0) 0.95 else 1.05
            val delta = (center-mouseSC2)*factor+mouseSC2-center
            scale *= factor
            updateCamera()
            val coordMapSC   = DOWN_LEFT*coordMapWC.x   + DOWN_RIGHT*coordMapWC.y
            val mousePlaneSC = DOWN_LEFT*mousePlaneWC.x + DOWN_RIGHT*mousePlaneWC.y
//            console.log(mouse2, p2, p3, mouse2-p2)
            //center += (mouseSC2-coordMapSC)*scale
            center += delta
            draw()
        }

        createButton("Reset").apply {
            mouseClicked {
                scale = 4.0
                azimuth = 2.0
                elevation = 0.5
                center = createVector(0, 0)
                draw()
            }
        }
    }



}