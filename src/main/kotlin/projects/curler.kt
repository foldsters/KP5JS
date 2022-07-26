package projects

import p5.*
import kotlin.math.PI

fun curler() = Sketch {

    lateinit var outShader: P5.Shader
    lateinit var sliders: List<NativeP5.Slider>
    lateinit var paragraphs: Array<NativeP5.Element>

    Preload {
        outShader = loadShader("out.vert", "out.frag")
    }

    Setup {
        val canvas = createCanvas(128, 128, P5.RenderMode.WEBGL2)
        noStroke()
        frameRate(15)
        shader(outShader)
        pixelDensity(1)
        outShader["iResolution"] = arrayOf(width, height)
        rect(0, 0, width, height)

        //textFont(font, 100)

        sliders = arrayOf(0.0, 2.54, 0, 3.05, 0.72, 0.45, 0.63).mapIndexed { i, it ->
            createSlider(0.0, 2*PI, it, 0.01).apply {
                size(1000, 50)
                position(width + 50, 100*i)
            }
        }

        paragraphs = Array(7) {
            createP(sliders[it].value().toString()).apply {
                style("font-size", "50px")
                position(width + 1100, 100*it - 50)
            }
        }

        createButton("save").apply {
            size(200, 100)
            fontSize(50)
            mouseClicked {
                noLoop()
                frameCount = 0
                createLoop(duration = 15, framesPerSecond = 15, gif = true, gifRender = true, gifQuality = 50) {
                    draw!!()
                    frameCount++
                }
            }
        }

        Draw {
            val theta = 2.0*PI*frameCount.toDouble()/(15.0*15.0)
            outShader["iTime"] = theta
            arrayOf("rotX", "rotY", "rotZ", "swirl", "slope", "size", "sep").forEachIndexed { i, v ->
                val num = sliders[i].value()
                outShader[v] = num
                paragraphs[i].html(num.toString())
            }
            rect(0, 0, width, height)
            console.log(theta)
        }
    }


}