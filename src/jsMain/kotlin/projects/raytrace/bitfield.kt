@file:OptIn(ExperimentalJsExport::class)

package projects.raytrace

import kotlinx.serialization.Serializable
import p5.Sketch
import p5.core.MouseButton
import p5.core.RenderMode
import p5.core.P5.*
import p5.ksl.*
import kotlin.math.asin
import kotlin.math.atan2

@JsExport
fun Bitfield() = Sketch {

    @Serializable
    data class RayTracerData(
        var cameraPos: Vector,
        var cameraForward: Vector,
        var cameraUp: Vector,
        var cameraRight: Vector,
        var planeDist: Double,
        var planeWidth: Double
    )

    Setup {

        frameRate(60)
        pixelDensity(1)


        val canvas = createCanvas(1920, 1920, RenderMode.WEBGL2)
        noStroke()

        val tracerData = RayTracerData(
            createVector(0.0, 0.0, 0.0),
            createVector(0.0, 0.0, 1.0),
            createVector(0.0, 1.0, 0.0),
            createVector(1.0, 0.0, 0.0),
            10.0,
            10.0
        )

        val shaderProgram = buildShader(debug = true) {
            Fragment {

                val resolution by Uniform<vec2> { arrayOf(width, height) }
                val cameraPos by Uniform<vec3> { arrayOf(tracerData.cameraPos.x, tracerData.cameraPos.y, tracerData.cameraPos.z) }
                val planeDist by Uniform { tracerData.planeDist }
                val planeWidth by Uniform { tracerData.planeWidth }
                val azimuth by Uniform { atan2(tracerData.cameraForward.x, tracerData.cameraForward.z)  }
                val zenith by Uniform { asin(tracerData.cameraForward.y) }

                Main {
                    val uv by planeWidth*(gl_FragCoord.xy/resolution - 0.5)
                    var rayDir by vec3(uv, planeDist)

                    val azimuthAxis by vec3(0, 1, 0)
                    var zenithAxis by vec3(1, 0, 0)

                    rayDir = rayDir.rotate(azimuthAxis, azimuth)
                    zenithAxis = zenithAxis.rotate(azimuthAxis, azimuth)
                    rayDir = rayDir.rotate(zenithAxis, -zenith)

                    val floorPos by cameraPos.map { floor(it) }
                    val signPos by rayDir.map { sign(it) }

                    val deltaDist by vec3(1e9, 1e9, 1e9)
                    IF(rayDir.x NE 0.0) { deltaDist.x = abs(1.0/rayDir.x) }
                    IF(rayDir.y NE 0.0) { deltaDist.y = abs(1.0/rayDir.y) }
                    IF(rayDir.z NE 0.0) { deltaDist.z = abs(1.0/rayDir.z) }

                    val sideDist by deltaDist*(signPos*(floorPos-cameraPos + 0.5) + 0.5)
                    var hit by bool(false)
                    var side by int(0)
                    var i by int(0)
                    var wallDistVec by (floorPos-cameraPos+(1.0 - signPos)*0.5)/rayDir
                    var wallDist by float(0.0)
                    var hitType by float(0.0)

                    WHILE(!hit) {
                        val smallestSide by sideDist.min()

                        IF(sideDist.x EQ smallestSide) {
                            sideDist.x += deltaDist.x
                            floorPos.x += signPos.x
                            wallDistVec = (floorPos-cameraPos+(1.0 - signPos)*0.5)/rayDir
                            wallDist = wallDistVec.x
                            side = int(1)
                        }.ELSEIF(sideDist.y EQ smallestSide) {
                            sideDist.y += deltaDist.y
                            floorPos.y += signPos.y
                            wallDistVec = (floorPos-cameraPos+(1.0 - signPos)*0.5)/rayDir
                            wallDist = wallDistVec.y
                            side = int(2)
                        }.ELSE {
                            sideDist.z += deltaDist.z
                            floorPos.z += signPos.z
                            wallDistVec = (floorPos-cameraPos+(1.0 - signPos)*0.5)/rayDir
                            wallDist = wallDistVec.z
                            side = int(3)
                        }

                        i += 1
                        IF(i GT 1) {
                            val p by floorPos
                            val k by float(uint(abs(p.x + p.y))
                                    BIT_XOR uint(abs(p.x - p.y))
                                    BIT_XOR uint(abs(p.y + p.z))
                                    BIT_XOR uint(abs(p.y - p.z))
                                    BIT_XOR uint(abs(p.z + p.x))
                                    BIT_XOR uint(abs(p.z - p.x)))
                            val e by pow(k, float(5))
                            hitType = e - 31.0*floor(e/31.0)
//                            int(abs(mod(pow(k, float(13)), float (7))))
                            hit = (hitType GT 5.0) AND (hitType LT 25.0)
                        }

                        IF(i GT 150) {
                            side = int(0)
                            hitType = float(0.0)
                            Break
                        }
                    }

                    var sideColor by vec3(0.0, 0.0, 0.0)
                    IF(side EQ 1) {
                        wallDist = wallDistVec.x
                        sideColor = vec3(0.2, 0.8, 0.9)
                    }.ELSEIF(side EQ 2) {
                        wallDist = wallDistVec.y
                        sideColor = vec3(0.05, 0.8, 0.9)
                    }.ELSEIF(side EQ 3) {
                        wallDist = wallDistVec.z
                        sideColor = vec3(0.1, 0.8, 0.9)
                    } 

                    val x by wallDist*wallDist
                    val color by sideColor.map { a -> 1.0 - x/(x+(a/(1.0-a))) }
                    vec4(color, 1.0)
                }

            }
        }

        shader(shaderProgram)
        shaderProgram.update()
        rect(0, 0, width, height)

        var speed = 0.5

        Draw {
            background(0, 0)

            if (mouseButton == MouseButton.LEFT) {
                with(tracerData) {
                    val deltaYaw = (mouseX/width.toDouble() - 0.5).deadzone(0.02) * 0.05
                    val deltaPitch =  (mouseY/height.toDouble() - 0.5).deadzone(0.02) * 0.05
                    val oldRight = cameraRight.copy()
                    val oldUp = cameraUp.copy()
                    cameraRight = cameraRight.rotate(oldUp, deltaYaw)
                    cameraUp = cameraUp.rotate(oldRight, deltaPitch)
                    cameraForward = cameraRight cross cameraUp
                    cameraPos += cameraForward*speed
                }
            }

            shaderProgram.update()
            rect(0, 0, width, height)
        }

        KeyPressed {
            console.log(key)
            when(key) {
                "Shift" -> speed = 2.0
                " " -> speed = if(speed == 0.0) 0.5 else 0.0
            }
        }

        KeyReleased {
            if(key == "Shift") {
                speed = 0.5
            }
        }

//        MouseWheel {
//            speed = if (deltaY < 0) speed/0.9 else speed*0.9
//        }

    }

}