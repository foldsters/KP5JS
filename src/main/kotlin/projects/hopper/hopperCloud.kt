package projects.hopper

import kotlinx.serialization.Serializable
import p5.NativeP5.*
import p5.P5
import p5.P5.*
import p5.P5.MouseButton.*
import p5.Sketch
import p5.kglsl.buildShader
import p5.kglsl.vec2
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
        val startSide = layer.indexOfFirst { it > 0 }
        start += direction(startSide) + direction(startSide+1) + createVector(0, 0, 1)
        pos = start
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
        val canvas = createCanvas(1920, 1080, RenderMode.P2D).apply {
            hide()
        }
        background(0, 255)
        pixelDensity(1)
        noStroke()
        val screenCenter = createVector(width/2, height/2)
        val crystalSize = 64
        val seedHeight = 16

        val hopperMask = createGraphics(width, height, RenderMode.P2D, hide=true).apply {
            noStroke()
            pixelDensity(1)
        }

        val edgeBuffer = createGraphics(width, height, RenderMode.WEBGL2, hide=false).apply {
            noStroke()
            pixelDensity(1)
        }

        var lineThickness = 1.0

        val edgeDetect = edgeBuffer.buildShader(useWEBGL2 = true, debug = true) {

            Fragment {
                val mask by Uniform { hopperMask }
                val hopper by Uniform { canvas }
                val resolution by Uniform<vec2> { arrayOf(width, height) }
                val thickness by Uniform { lineThickness }

                Main {
                    val uv by gl_FragCoord.xy/resolution
                    uv.y = 1.0-uv.y
                    val level by texture(hopper, uv).rgb
                    var edge by vec3(0, 0, 0)

                    val kernel = listOf(listOf(1, 0, -1), listOf(2, 0, -2), listOf(1, 0, -1))
                    kernel.forEachIndexed { y, row ->
                        row.forEachIndexed { x, k ->
                            edge += texture(mask, uv+vec2(x-1, y-1)*thickness/resolution).rgb*float(k)
                        }
                    }
                    edge = (edge.rrr + edge.ggg + edge.bbb)/9.0 + texture(mask, uv).rgb
                    If(edge.r `<` 0.0) {
                        edge = vec3(0.01, 0.01, 0.01)
                    }
                    edge = clamp(edge, float(0), float(1))
                    If(edge.r `==` float(0.0)) {
                        edge = level
                    }
                    vec4(edge, 1.0)
                }
            }
        }

        edgeBuffer.shader(edgeDetect)

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

        abstract class Point(val location: Vector, var id: Double) {
            val x: Double get() = location.x
            val y: Double get() = location.y
            val z: Double get() = location.z
            abstract val color: Vector
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

        val hopperPoints = buildList {
            repeat(30) { id ->
                genHopperPoints(genHopperLayers(genHopperTop(crystalSize)), seedHeight).forEach {
                    add(it to id.toDouble())
                }
            }
        }.traverse({ first }, { centerize() }) { HopperPoint(it, second) }

        val clouds = genClouds(-30, 10, 120)

        val allPoints = hopperPoints + clouds

        val sort1 = allPoints.sortedWith(compareBy<Point> {-it.z}.thenBy {- it.x + it.y })
        val sort2 = allPoints.sortedWith(compareBy<Point> {-it.z}.thenBy {- it.x - it.y })
        val sort3 = allPoints.sortedWith(compareBy<Point> {-it.z}.thenBy {  it.x - it.y })
        val sort4 = allPoints.sortedWith(compareBy<Point> {-it.z}.thenBy {  it.x + it.y })

        val sorts = listOf(sort1, sort2, sort3, sort4)

        @Serializable
        class Camera(var elevation: Double, var azimuth: Double, var scale: Double, var hScale: Double, var center: Vector) {
            var UP = Vector(0.0, 0.0)
            var DOWN = Vector(0.0, 0.0)
            var DOWN_RIGHT = Vector(0.0, 0.0)
            var DOWN_LEFT = Vector(0.0, 0.0)
            var UP_LEFT = Vector(0.0, 0.0)
            var UP_RIGHT = Vector(0.0, 0.0)
        }

        fun Camera.update() {
            azimuth = (azimuth + 8.0)%4.0
            val tf = azimuth - azimuth.toInt()
            val directions =  Array(4) { (Vector.fromAngle(2*PI*(tf+it)/4.0)*createVector(1.0, elevation)*scale) }
            UP = createVector(0, -scale*hScale)
            DOWN = createVector(0, scale*hScale)
            DOWN_RIGHT = directions[0]
            DOWN_LEFT = directions[1]
            UP_LEFT = directions[2]
            UP_RIGHT = directions[3]
            val thicknessFactor by url { 4.0 }
            lineThickness = scale/thicknessFactor
        }

        fun Camera.reset() {
            scale = 4.0
            azimuth = 2.5
            elevation = 0.5
            center = createVector(0, 0)
            update()
        }

        var camera by cacheSerial { Camera(0.5, 3.5, 4.0, 1.1, createVector(0, 0)) }
        camera.update()

        fun draw() {
            background(0, 255)
            hopperMask.background(0, 255)
            camera = camera
            with(camera) {
                update()
                val ti = azimuth.toInt()
                val tc = (1 + (azimuth * 0.25)) * 0.25
                DrawFor(sorts[ti], 1000) {
                    with(it) {
                        val (px, py) = when(ti) {
                            0 -> y to -x
                            1 -> -x to -y
                            2 -> -y to x
                            else -> x to y
                        }

                        val pos = center + DOWN_LEFT * px + DOWN_RIGHT * py + DOWN * z + screenCenter

                        if (it is HopperPoint) {
                            var c = (color * tc).toColor()
                            fill(c)
                            hopperMask.fill(c)
                            quad(pos, pos+UP_LEFT*1.1, pos+UP_LEFT*1.1+UP, pos+UP)
                            hopperMask.quad(pos, pos+UP_LEFT*1.1, pos+UP_LEFT*1.1+UP, pos+UP)
                            c = (color * (0.75 - tc)).toColor()
                            fill(c)
                            hopperMask.fill(c)
                            quad(pos, pos+UP_RIGHT*1.1, pos+UP_RIGHT*1.1+UP, pos+UP)
                            hopperMask.quad(pos, pos+UP_RIGHT*1.1, pos+UP_RIGHT*1.1+UP, pos+UP)
                            c = color.toColor()
                            fill(c)
                            hopperMask.fill(c)
                            buildShape2D { listOf(pos+UP, pos+UP+UP_LEFT, pos+UP+UP_RIGHT+UP_LEFT, pos+UP+UP_RIGHT).dilate(1.1).addVertices() }
                            hopperMask.buildShape2D { listOf(pos+UP, pos+UP+UP_LEFT, pos+UP+UP_RIGHT+UP_LEFT, pos+UP+UP_RIGHT).dilate(1.1).addVertices() }

                        } else {
                            val U = UP * 8
                            val UR = UP_RIGHT
                            val UL = UP_LEFT
                            fill(color.toColor(128))
                            buildShape2D(CLOSE) {
                                addVertices(pos, pos+UL, pos+UL+U, pos+UL+U+UR, pos+U+UR, pos+UR)
                            }
                            hopperMask.fill(0, 255)
                            hopperMask.buildShape2D {
                                listOf(pos, pos+UL, pos+UL+U, pos+UL+U+UR, pos+U+UR, pos+UR).dilate(1.1).addVertices()
                            }
                        }
                    }
                }.AfterFrame {
                    edgeDetect.update()
                    edgeBuffer.rect(0, 0, width, height)
                }
            }
        }

        draw()

        MouseDragged {
            with(camera) {
                when(mouseButton) {
                    CENTER -> {
                        azimuth = ((azimuth - map(mouseY, 0, height, -4, 4)*movedX/width + 4)%4.0).toDouble()
                        elevation = (elevation + 4.0*(movedY/height)).toDouble().coerceIn(0.0, 1.0)
                    }
                    LEFT -> {
                        center += createVector(movedX, movedY)
                    }
                    else -> return@MouseDragged
                }
            }
            draw()
        }

        MouseWheel {
            with(camera) {
                val mouseSC2 = createVector(mouseX-width/2, mouseY-height/2)
                val factor = if(delta > 0) 0.95 else 1.05
                val delta = (center-mouseSC2)*factor+mouseSC2-center
                scale *= factor
                center += delta
                draw()
            }
        }

        createButton("Reset").apply {
            mouseClicked {
                camera.reset()
                draw()
            }
        }
    }


}