package scripts.dancer

import p5.NativeP5
import p5.P5
import p5.Sketch
import p5.util.toFixed

fun unapologeticShader() = Sketch {

    lateinit var outShader: P5.Shader
    lateinit var pointFrameMap: MutableMap<Int, Array<Array<Number>>>
    lateinit var vid: NativeP5.MediaElement

    preload {
        pointFrameMap = mutableMapOf()
        outShader = loadShader("out.vert", "dancer/out.frag")

        loadTable("dancer/points_full.csv", P5.TableMode.CSV, true) {
            it.getRows().forEach { row->
                with(row) {
                    pointFrameMap[getNum("frame").toInt()] = arrayOf(
                        arrayOf(getNum("baseX"), getNum("baseY")),
                        arrayOf(getNum("headX"), getNum("headY")),
                        arrayOf(getNum("handX"), getNum("handY"))
                    )
                }
            }
        }


        vid = createVideo("assets/dancer/unapologetic.mp4")

    }

    setup {
        createCanvas(1920, 1080, P5.RenderMode.WEBGL2)
        noStroke()
        pixelDensity(1)
        val x = createGraphics(1920, 1080, P5.RenderMode.WEBGL2)
        x.asDynamic().noStroke()
        vid.size(1920, 1080)
        vid.hide()
        frameRate(23.98/8.0)
        shader(outShader)
        outShader["iResolution"] = arrayOf(1920, 1080)
        rect(0, 0, 1920, 1080)

        val red by createSlider(0.7, 1, 0.9, 0.005).apply {
            position(1920 + 100, 100)
            size(400, 100)
        }

        val green by createSlider(0.7, 1, 0.9, 0.005).apply {
            position(1920 + 100, 200)
            size(400, 100)
        }

        val blue by createSlider(0.7, 1, 0.9, 0.005).apply {
            position(1920 + 100, 300)
            size(400, 100)
        }

        var redParagraph by createP(red.toFixed(4)).apply {
            position(1920 + 500, 50)
            style("font-size", "50px")
        }

        var greenParagraph by createP(red.toFixed(4)).apply {
            position(1920 + 500, 150)
            style("font-size", "50px")
        }

        var blueParagraph by createP(red.toFixed(4)).apply {
            position(1920 + 500, 250)
            style("font-size", "50px")
        }

        outShader["iBase"] = arrayOf(0.0, 0.0)
        outShader["iHead"] = arrayOf(0.0, 0.0)
        outShader["iHand"] = arrayOf(0.0, 0.0)

        draw {
            vid.time((3400+frameCount).toDouble()/23.98)
            x.asDynamic().texture(vid.get())
            x.asDynamic().rect(-1920/2, -1080/2, 1920/2, 1080/2)

            val points = arrayOf<Array<Number>>(arrayOf(0.0, 0.0), arrayOf(0.0,0.0), arrayOf(0.0,0.0))
            for (k in -5..5) {
                pointFrameMap[3400+frameCount+k]?.forEachIndexed { i, p ->
                    points[i][0] += p[0].toDouble()/11.0
                    points[i][1] += p[1].toDouble()/11.0
                } ?: return@draw
            }
            //val points = pointFrameMap[frameCount] ?: return@draw



            outShader["iFrame"] = 3400+frameCount
            outShader["iBase"] = points[0]
            outShader["iHead"] = points[1]
            outShader["iHand"] = points[2]
            outShader["iTexture"] = x
            outShader["red"] = red
            outShader["green"] = green
            outShader["blue"] = blue
            outShader["iTexture"] = x

            redParagraph = red.toFixed(4)
            greenParagraph = green.toFixed(4)
            blueParagraph = blue.toFixed(4)


            rect(0, 0, 1920, 1080)
            //image(vid.get(), -1920/2, -1080/2, 1920, 1080)

        }

    }

}