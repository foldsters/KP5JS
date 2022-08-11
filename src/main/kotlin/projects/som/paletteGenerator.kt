package projects.som

import p5.*
import p5.util.getValue
import kotlin.math.max

fun paletteGenerator() = Sketch {
    lateinit var sourceImage: NativeP5.Image

    Preload {
        sourceImage = loadImage("stock/flower.png")
    }

    Setup {
        val paletteRows by url { 8.0 }
        val paletteColumns by url { 8.0 }

        val swatchMaxDimension = 1024.0/max(paletteRows, paletteColumns)

        val sourceCanvas = createGraphics(1024, 1024, P5.RenderMode.P2D, hide = false).apply {
            image(sourceImage, 0, 0, width, height)
            colorMode(P5.ColorMode.RGB, 1, 1, 1, 255)
        }

        val canvas = createCanvas(
            paletteColumns*swatchMaxDimension,
            paletteRows*swatchMaxDimension,
            P5.RenderMode.P2D)
        colorMode(P5.ColorMode.RGB, 1, 1, 1, 255)
        pixelDensity(1)
        noStroke()
        noSmooth()

        val palette = createGraphics(paletteColumns, paletteRows, P5.RenderMode.P2D)
        palette.noStroke()
        palette.pixelDensity(1)
        palette.colorMode(P5.ColorMode.RGB, 1, 1, 1, 255)

        val quality     by createSlider(0, 10, 3, 0.1, true).apply { size(width, 200, 3) }
        val blendStart  by createSlider(0, 1, 0.3, 1.0/255.0, true).apply { size(width, 200, 3) }
        val blendEnd    by createSlider(0, 1, 0.3, 1.0/255.0, true).apply { size(width, 200, 3) }
        val radiusStart by createSlider(0, 1, 0.3, 1.0/255.0, true).apply { size(width, 200, 3) }
        val radiusEnd   by createSlider(0, 1, 0.3, 1.0/255.0, true).apply { size(width, 200, 3) }

        val imageSize = createVector(sourceImage.width, sourceImage.height)
        val diagonal = createVector(paletteRows, paletteColumns).mag()
        val count by { 2.5.pow(quality).toInt() }

        val button = createButton("Redraw")
        var buttonOnClick: ()->Unit = {}
        button.apply {
            text("Draw")
            size(width, 200, 3)
            mouseClicked { buttonOnClick() }
        }

        fun drawPalette() {
            clear()
            sourceCanvas.clear()
            palette.clear()
            sourceCanvas.image(sourceImage, 0, 0, sourceCanvas.width, sourceCanvas.height)
            buttonOnClick = {
                noLoop()
                button.text("Redraw")
                buttonOnClick = ::drawPalette
            }
            button.text("Stop")

            DrawFor(0 until count) {frame ->

                val progress = frame/count.toDouble()
                val radius = map(progress, 0, 1, radiusStart, radiusEnd)*diagonal
                val randomPixelId = randInt(imageSize.x*imageSize.y)

                val randomPixelColor = sourceImage.withPixels { colorArray[randomPixelId].toVector() }
                val pixelBag: MutableList<Pair<NativeP5.Vector, Double>> = mutableListOf()

                palette.withPixels {
                    for (row in 0 until paletteRows.toInt()) {
                        for (col in 0 until paletteColumns.toInt()) {
                            val pixelColor = colorArray[row, col].toVector()
                            val colorDistance = dist(pixelColor, randomPixelColor).toDouble()
                            pixelBag.add(createVector(col, row) to colorDistance)
                        }
                    }
                }

                val circleCenter = pixelBag.shuffled().minBy { it.second }.first
                val blendColor = randomPixelColor.toColor(map(progress, 0, 1, blendStart, blendEnd)*255.0)
                palette.fill(blendColor)
                palette.circle(circleCenter, radius)

                image(palette, 0, 0, width, height)

            }.AfterDone {
                println("Done")
                button.text("Redraw")
                buttonOnClick = ::drawPalette
            }
        }

        drawPalette()
        buttonOnClick = ::drawPalette

        sourceCanvas.getCanvas().drop { file ->
            loadImage(file.data) {
                sourceImage = it
                drawPalette()
            }
        }
    }
}