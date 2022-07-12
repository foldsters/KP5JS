package projects.dancer

import p5.NativeP5
import p5.P5
import p5.Sketch
import p5.util.ifFalse
import p5.util.toFixed

fun unapologeticMocap() = Sketch {

    Preload {

        val pointFrameMap: MutableMap<Number, Array<NativeP5.Vector>> = mutableMapOf()

        loadTable("dancer/points.csv", P5.TableMode.CSV, true) {
            it.getRows().forEach { row->
                with(row) {
                    pointFrameMap[getNum("frame")] = arrayOf(
                        createVector(getNum("baseX"), getNum("baseY")),
                        createVector(getNum("headX"), getNum("headY")),
                        createVector(getNum("handX"), getNum("handY"))
                    )
                }
            }
        }


        Setup {

            createCanvas(1920, 1080)
            val vid = createVideo("assets/dancer/unapologetic.mp4")
            vid.size(1920, 1080)
            vid.hide()
            vid.volume(0.0)

            var time = 0.0
            val videoFPS = 23.98
            var videoFrame = 0.0

            var frameSkip by createInput("1").apply {
                position(width + 100, 200)
                size(400, 100)
                style("font-size", "60px")
            }

            var frameCountDisplay by createP("Frame: 0").apply {
                position(width + 100, 300)
                size(400, 100)
                style("font-size", "60px")
            }

            var timeDisplay by createP("Time: 0").apply {
                position(width + 100, 400)
                size(400, 100)
                style("font-size", "60px")
            }

            fun updateFrame() {
                time = videoFrame/videoFPS
                vid.time(time)
                frameCountDisplay = "Frame: $videoFrame"
                timeDisplay = "Time: ${time.toFixed(2)}"
            }

            createButton("prev frame").apply {
                position(width + 100, 100)
                size(200, 100)
                style("font-size", "40px")
                mouseClicked {
                    videoFrame -= frameSkip.toDouble()
                    updateFrame()
                }
            }

            createButton("next frame").apply {
                position(width + 300, 100)
                size(200, 100)
                style("font-size", "40px")
                mouseClicked {
                    videoFrame += frameSkip.toDouble()
                    updateFrame()
                }
            }


            var hasPoints = false
            fun getPoints(frame: Number? = null): Array<NativeP5.Vector> {
                val vFrame = frame ?: videoFrame
                if (vFrame !in pointFrameMap) {
                    hasPoints = false
                    return Array(3) {i -> pointFrameMap.mapValues { it.value[i] }.interpolate(vFrame) }
                }
                hasPoints = true
                return pointFrameMap[vFrame]!!
            }

            fun pointsToTable(): NativeP5.Table {

                val table = NativeP5.Table().apply {
                    addColumn("frame")
                    addColumn("baseX")
                    addColumn("baseY")
                    addColumn("headX")
                    addColumn("headY")
                    addColumn("handX")
                    addColumn("handY")
                }

                repeat(3600) { frame ->
                    val points = getPoints(frame)
                    console.log(frame)
                    table.addRow().apply {
                        set("frame", frame)
                        set("baseX", points[0].x)
                        set("baseY", points[0].y)
                        set("headX", points[1].x)
                        set("headY", points[1].y)
                        set("handX", points[2].x)
                        set("handY", points[2].y)
                    }
                }

                return table

            }

            createButton("Save Table").apply {
                position(width + 100, 600)
                size(400, 100)
                style("font-size", "40px")
                mouseClicked {
                    saveTable(pointsToTable(), "points", P5.TableMode.CSV)
                }
            }

            stroke(0)
            strokeWeight(50)
            noFill()

            var nearestPoint = -1

            fun moveNearestPoint() {
                var minDist = 100 as Number
                val points = getPoints()
                val mouseVec = createVector(
                    mouseX.toInt().coerceIn(0..width),
                    mouseY.toInt().coerceIn(0..height)
                )
                if (nearestPoint > -1) {
                    points[nearestPoint] = mouseVec
                    pointFrameMap[videoFrame] = points
                }
                points.forEachIndexed { i, point ->
                    val d = dist(mouseVec, point)
                    console.log(d)
                    if (d < minDist) {
                        minDist = d
                        nearestPoint = i
                    }
                }
                if (nearestPoint > -1) {
                    points[nearestPoint] = mouseVec
                    pointFrameMap[videoFrame] = points
                }
            }

            MouseWheel {
                keyIsDown(17).ifFalse {
                    when {
                        delta > 0 -> {
                            videoFrame -= frameSkip.toDouble()
                            updateFrame()
                        }
                        delta < 0 -> {
                            videoFrame += frameSkip.toDouble()
                            updateFrame()
                        }
                    }
                }
            }

            KeyPressed {
                console.log(keyCode)
                if (keyCode == 46) {
                    pointFrameMap.remove(videoFrame)
                }
            }

            var isDragging = false

            Draw {

                val (base, head, hand) = getPoints()
                stroke(0, if (hasPoints) 255 else 0, 0)

                background(220)
                val img = vid.get()

                image(img, 0, 0)

                strokeWeight(50)
                point(head)
                point(base)
                point(hand)

                strokeWeight(10)
                stroke(0, 0, 255)
                line(head.x, head.y, base.x, base.y)
                stroke(255,0, 0)
                line(head.x, head.y, hand.x, hand.y)

                if (mouseIsPressed) {
                    moveNearestPoint()
                } else {
                    nearestPoint = -1
                }
            }
        }
    }



}