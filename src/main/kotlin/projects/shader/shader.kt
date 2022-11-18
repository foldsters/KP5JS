package projects.shader

import p5.Sketch
import p5.core.RenderMode
import p5.ksl.*

fun shader() = Sketch {

    Setup {

        createCanvas(512, 512, RenderMode.WEBGL2)

        val outShader = buildShader(debug = true) {
            Fragment {

                var m by float(1.0)
                FOR(2.0, 4.0) {
                    m = it
                }

                val iTime by Uniform<float>()
                val iResolution by Uniform<vec2>()

                Main {
                    val uv by gl_FragCoord.xy/iResolution.xy
                    val col by 0.5 + (0.5 * cos(iTime/2.0 + uv.xyx + vec3(0, 2, 4)))
                    vec4(col, 1.0)
                }

            }
        }

        noStroke()
        shader(outShader)
        pixelDensity(1)
        frameRate(60)
        outShader["iResolution"] = arrayOf(width, height)
        rect(0, 0, width, height)

        Draw {
            outShader["iTime"] = millis()/1000.0
            rect(0, 0, width, height)
        }
    }

}









