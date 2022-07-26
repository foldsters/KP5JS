package projects.hopper

import kotlinx.browser.window
import kotlinx.serialization.Serializable
import p5.NativeP5.*
import p5.P5
import p5.P5.*
import p5.P5.MouseButton.*
import p5.Sketch
import p5.kglsl.buildShader
import p5.kglsl.vec2
import p5.util.*
import kotlin.math.*

//fun P5.genHopperTop(minCrystalSize: Int, maxCrystalSize: Int): List<Int> {
//    fun spiral(): List<Int> = buildList {
//        var length = randInt(minCrystalSize, maxCrystalSize)
//        while(length >= 2) {
//            add(length)
//            length = randInt(length/2, length-1)
//        }
//    }
//    val hopper = spiral().reversed() + spiral()
//    return hopper.map {h -> h + (h+1)%2 }
//}
//
//fun P5.genHopperLayers(hopperTop: List<Int>) = buildList {
//    var layer = hopperTop
//    val minLayerLength = randInt(10, 20)
//    while(true) {
//        add(layer)
//        if (layer.sum() <= minLayerLength) break
//        layer = layer.map { h -> max(0, h-2) }
//    }
//}
//
//fun P5.cloudHeight(minHeight: Int, maxHeight: Int, x: Int, y: Int): Double {
//    val n = simplexNoise(x/20.0, y/20.0)*3 + simplexNoise(x/10.0, y/10.0)
//    return map(n/4.0, -1, 1, -maxHeight, -minHeight).toDouble()
//}
//
//fun P5.genClouds(minHeight: Int, maxHeight: Int, radius: Int, step: Int) = buildList {
//    for(x in -radius until radius step step) {
//        for(y in -radius until radius step step) {
//            if(x*x + y*y > radius*radius) continue
//            val z = cloudHeight(minHeight, maxHeight, x, y)
//            val id = (z+minHeight)/minHeight
//            add(createVector(x, y, z) to id)
//        }
//    }
//}
//
//fun P5.genHopperCrystal(minCrystalSize: Int, maxCrystalSize: Int, jitter: Int) = buildList {
//
//    val hopperLayers = genHopperLayers(genHopperTop(minCrystalSize, maxCrystalSize))
//
//    val orientation = randInt(0, 4)
//
//    val dirs = listOf(
//        createVector(1, 0, 0),
//        createVector(0, 1, 0),
//        createVector(-1, 0, 0),
//        createVector(0, -1, 0)
//    )
//
//    fun direction(d: Int) = dirs[(d + orientation + 4) % 4]
//
//    var start = createVector(randInt(jitter), randInt(jitter), randInt(jitter))
//    start = start.map { it + it % 2 }
//    var pos: Vector
//
//    hopperLayers.forEach { layer ->
//        val startSide = layer.indexOfFirst { it > 0 }
//        start += direction(startSide) + direction(startSide + 1) + createVector(0, 0, 1)
//        pos = start
//        layer.forEachIndexed { side, sideLength ->
//            val posCache = pos.copy()
//            repeat(sideLength) {
//                pos = posCache + direction(side) * it
//                add(pos to pos.z/hopperLayers.size)
//            }
//        }
//    }
//}
//
//fun P5.genHopperPoints(crystalSize: Int, jitter: Int, numCrystals: Int) = buildList {
//
//    val macroHopperLayers = genHopperCrystal(128, 128, 0).map { it.first }.centerize()
//    val seedPoints = macroHopperLayers.shuffled().take(numCrystals)
//
//    seedPoints.forEach {seedPoint ->
//        addAll(genHopperCrystal(2, crystalSize, jitter).map { (it.first+seedPoint*1.5) to it.second} )
//    }
//}
//
//
//
//fun hopperClouds() = Sketch {
//
//    Setup {
//
//        // Crystal and Clouds
//        val canvas = createCanvas(512, 512, RenderMode.P2D).apply {
//            hide()
//        }
//        background(0, 255)
//        pixelDensity(1)
//        noStroke()
//        val screenCenter = createVector(width/2, height/2)
//
//        // Crystal Without Clouds
//        val hopperMask = createGraphics(width, height, RenderMode.P2D, hide=true).apply {
//            noStroke()
//            pixelDensity(1)
//        }
//
//        // Post-Processing
//        val edgeBuffer = createGraphics(width, height, RenderMode.WEBGL2, hide=false).apply {
//            noStroke()
//            pixelDensity(1)
//        }
//
//        val lineThickness by url { 1.0 }
//
//        @Serializable
//        data class Camera(var elevation: Double, var azimuth: Double, var scale: Double, var hScale: Double, var center: Vector) {
//            var UP = Vector(0.0, 0.0)
//            var DOWN = Vector(0.0, 0.0)
//            var DOWN_RIGHT = Vector(0.0, 0.0)
//            var DOWN_LEFT = Vector(0.0, 0.0)
//            var UP_LEFT = Vector(0.0, 0.0)
//            var UP_RIGHT = Vector(0.0, 0.0)
//        }
//
//        fun Camera.update() {
//            azimuth = (azimuth + 8.0)%4.0
//            val tf = azimuth - azimuth.toInt()
//            val directions =  Array(4) { (Vector.fromAngle(2*PI*(tf+it)/4.0)*createVector(1.0, sin(PI*0.5*elevation))*scale) }
//            UP = createVector(0, -scale*hScale*cos(PI*0.5*elevation))
//            DOWN = createVector(0, scale*hScale*cos(PI*0.5*elevation))
//            DOWN_RIGHT = directions[0]
//            DOWN_LEFT = directions[1]
//            UP_LEFT = directions[2]
//            UP_RIGHT = directions[3]
//        }
//
//        fun Camera.reset() {
//            scale = 4.0
//            azimuth = 2.5
//            elevation = 0.5
//            center = createVector(0, 0)
//            update()
//        }
//
//        var camera by cacheSerial { Camera(0.5, 3.5, 4.0, 1.1, createVector(0, 0)) }
//
//        val edgeDetect = edgeBuffer.buildShader(useWEBGL2 = true, debug = true) {
//
//            Fragment {
//                val mask by Uniform { hopperMask }
//                val hopper by Uniform { canvas }
//                val resolution by Uniform<vec2> { arrayOf(width, height) }
//                val thickness by Uniform { lineThickness }
//
//                Main {
//                    val uv by gl_FragCoord.xy/resolution
//                    uv.y = 1.0-uv.y
//                    val level by texture(hopper, uv).rgb
//                    var edge by vec3(0, 0, 0)
//
//                    val kernel = listOf(listOf(1, 0, -1), listOf(2, 0, -2), listOf(1, 0, -1))
//                    kernel.forEachIndexed { y, row ->
//                        row.forEachIndexed { x, k ->
//                            edge += texture(mask, uv+vec2(x-1, y-1)*thickness/resolution).rgb*float(k)
//                        }
//                    }
//                    edge = (edge.rrr + edge.ggg + edge.bbb)/9.0 + texture(mask, uv).rgb
//                    If(edge.r `<` 0.0) {
//                        edge = vec3(0.01, 0.01, 0.01)
//                    }
//                    edge = clamp(edge, float(0), float(1))
//                    edge = mix(edge, level, float(edge.r `==` 0.0))
//
//                    vec4(edge, 1.0)
//                }
//            }
//        }
//
//        edgeBuffer.shader(edgeDetect)
//
//
//        fun P5.drawBlock(location: Vector, sideLengths: Vector, leftColor: Color, rightColor: Color, topColor: Color) {
//            with(camera) {
//                val pos = center + DOWN_LEFT*location.x + DOWN_RIGHT*location.y + DOWN*location.z + screenCenter
//                val rightSide = UP_RIGHT * sideLengths.x
//                val leftSide  = UP_LEFT * sideLengths.y
//                val upSide    = UP * sideLengths.z
//                fill(leftColor)
//                quad(pos, pos+leftSide, pos+leftSide+upSide, pos+upSide)
//                fill(rightColor)
//                quad(pos, pos+rightSide, pos+rightSide+upSide, pos+upSide)
//                fill(topColor)
//                buildShape2D { listOf(pos+upSide, pos+upSide+leftSide, pos+upSide+leftSide+rightSide, pos+upSide+rightSide).dilate(1.1).addVertices() }
//            }
//        }
//
//        val hopperColors = mapOf(
//            0.2 to createVector(255, 240, 100),
//            0.6 to createVector(255, 0, 255),
//            1.0 to createVector(0, 255, 255),
//            2.0 to createVector(0, 255, 128)
//        )
//
//        val cloudColors = listOf(
//            createVector(38, 85, 113),
//            createVector(66, 113, 169),
//            createVector(142, 157, 206),
//            createVector(198, 180, 221),
//            createVector(245, 205, 225)
//        )
//
//        abstract class Point(val location: Vector, var id: Double) {
//            val x: Double get() = location.x
//            val y: Double get() = location.y
//            val z: Double get() = location.z
//            abstract val color: Vector
//        }
//        class HopperPoint(location: Vector, id: Double): Point(location, id) {
//            override val color get() = hopperColors.interpolate(id)
//        }
//        class CloudPoint(location: Vector, id: Double): Point(location, id) {
//            override val color get() = cloudColors.interpolate(3*id)
//        }
//
//        val crystalSize by url { 64 }
//        val jitter by url { 2 }
//        val numCrystals by url { 32 }
//
//        val hopperPoints = genHopperPoints(crystalSize, jitter, numCrystals).traverse({ first }, { centerize() }) { HopperPoint(it, second) }
//        val cloudPoints = genClouds(-30, 10, 128, 1).map { CloudPoint(it.first, it.second) }
//        val allPoints = hopperPoints + cloudPoints
//
//        val sort1 = allPoints.sortedWith(compareBy {- it.x + it.y - it.z})
//        val sort2 = allPoints.sortedWith(compareBy {- it.x - it.y - it.z})
//        val sort3 = allPoints.sortedWith(compareBy {  it.x - it.y - it.z})
//        val sort4 = allPoints.sortedWith(compareBy {  it.x + it.y - it.z})
//
//        val sorts = listOf(sort1, sort2, sort3, sort4)
//
//        var stepsPerFrame = 500
//
//        fun drawPoint(it: Point) {
//            val ti = camera.azimuth.toInt()
//            val tc = (1 + (camera.azimuth * 0.25)) * 0.25
//            with(it) {
//                val (px, py) = when(ti) {
//                    0 -> y to -x
//                    1 -> -x to -y
//                    2 -> -y to x
//                    else -> x to y
//                }
//                val newLoc = createVector(px, py, z)
//
//                if (it is HopperPoint) {
//                    val sides = createVector(1.1, 1.1, 1.0)
//                    val leftColor = (color * tc).toColor()
//                    val rightColor = (color * (0.75 - tc)).toColor()
//                    val topColor = color.toColor()
//                    drawBlock(newLoc, sides, leftColor, rightColor, topColor)
//                    hopperMask.drawBlock(newLoc, sides, leftColor, rightColor, topColor)
//
//                } else {
//                    newLoc.z = sqrt(128*128 - x*x - y*y)
//                    val sides = createVector(1.1, 1.1, newLoc.z - z)
//
//                    val cloudColor = color.toColor(128)
//                    val maskColor = color(0, 255)
//                    drawBlock(newLoc, sides, cloudColor, cloudColor, cloudColor)
//                    hopperMask.drawBlock(newLoc, sides, maskColor, maskColor, maskColor)
//                }
//            }
//        }
//
//        fun draw() {
//            background(0, 255)
//            hopperMask.background(0, 255)
//            camera = camera // force caching
//            camera.update()
//            val ti = camera.azimuth.toInt()
//            DrawFor(sorts[ti], 500) {
//                drawPoint(it)
//            }.AfterFrame {
//                edgeDetect.update()
//                edgeBuffer.rect(0, 0, width, height)
//            }
//        }
//
//        draw()
//
//        createButton("Save Gif").apply {
//            size(250, 100)
//            fontSize(50)
//            mouseClicked {
//                console.log(window)
//                stepsPerFrame = sort1.size
//                camera.center = createVector(0, 0)
//                noLoop()
//                createLoop(duration = 15, framesPerSecond = 15, gif = true, gifRender = true, gifQuality = 50, htmlCanvas = edgeBuffer.htmlCanvas) {
//                    background(0, 255)
//                    hopperMask.background(0, 255)
//                    camera.update()
//                    println(progress)
//                    camera.azimuth = (progress * 4.0).toDouble()
//                    val ti = camera.azimuth.toInt()
//                    sorts[ti].forEach {
//                        drawPoint(it)
//                    }
//                    edgeDetect.update()
//                    edgeBuffer.rect(0, 0, width, height)
//                }
//            }
//        }
//
//        MouseDragged {
//            when(mouseButton) {
//                CENTER -> {
//                    camera.azimuth = ((camera.azimuth - map(mouseY, 0, height, -4, 4)*movedX/width + 4)%4.0).toDouble()
//                    camera.elevation = (camera.elevation + 4.0*(movedY/height)).toDouble().coerceIn(0.0, 1.0)
//                }
//                LEFT -> {
//                    camera.center += createVector(movedX, movedY)
//                }
//                else -> return@MouseDragged
//            }
//            draw()
//        }
//
//        MouseWheel {
//            val mouseSC2 = createVector(mouseX-width/2, mouseY-height/2)
//            val factor = if(delta > 0) 0.95 else 1.05
//            camera.scale *= factor
//            camera.center = (camera.center-mouseSC2)*factor+mouseSC2
//            draw()
//        }
//
//        createButton("Reset").apply {
//            fontSize(50)
//            size(250, 100)
//            mouseClicked {
//                camera.reset()
//                draw()
//            }
//        }
//    }
//}