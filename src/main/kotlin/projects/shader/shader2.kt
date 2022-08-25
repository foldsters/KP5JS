package projects.shader

import p5.core.P5
import p5.Sketch
import p5.core.RenderMode
import p5.kglsl.*

fun uniformBridgeExample() = Sketch {

    Setup {

        createCanvas(512, 100, RenderMode.WEBGL2)

        val slider by createSlider(-1, 1, 0, 0.01).apply {
            size(width, 10)
            position(0, height)
        }

        val kshader = buildShader(debug = true) {
            Fragment {

                fun center(coord: vec2, res: vec2) = (2.0*coord - res)/max(res.x, res.y)

                val iResolution  by Uniform<vec2> { arrayOf(width, height) }
                val circleCenter by Uniform { slider.toDouble() }

                Main {
                    val uv by center(gl_FragCoord.xy, iResolution)
                    val dist by distance(vec2(circleCenter, 0.0), uv)
                    val color by smoothstep(float(0.09), float(0.11), dist)
                    vec4(color, color, color, 1.0)
                }
            }
        }

        noStroke()
        pixelDensity(1)
        frameRate(60)
        shader(kshader)

        Draw {
            kshader.update()
            rect(0, 0, width, height)
        }
    }
}

