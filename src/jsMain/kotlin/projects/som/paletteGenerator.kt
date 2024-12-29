@file:OptIn(ExperimentalJsExport::class)

package projects.som

import p5.*
import p5.core.*
import kotlin.math.max
import p5.core.P5.*
import p5.util.*
import kotlin.io.println

@JsExport
class PaletteGeneratorProps(
    val imageSource: String
)

@JsExport
fun PaletteGenerator(props: PaletteGeneratorProps) = Sketch {

    lateinit var sourceImage: Image

    Preload {
        sourceImage = loadImage(props.imageSource)
    }

    Setup {

        println(isDarkMode())
        val paletteRows by url { 8.0 }
        val paletteColumns by url { 8.0 }

        val swatchMaxDimension = 512.0/max(paletteRows, paletteColumns)

        val imageCanvas = createGraphics(512, 512, RenderMode.P2D, hide = false).apply {
            image(sourceImage, 0, 0, width, height)
            colorMode(ColorMode.RGB, 1, 1, 1, 255)
        }

        val canvas = createCanvas(
            paletteColumns*swatchMaxDimension,
            paletteRows*swatchMaxDimension,
            RenderMode.P2D
        )
        colorMode(ColorMode.RGB, 1, 1, 1, 255)
        pixelDensity(1)
        noStroke()
        noSmooth()
        frameRate(1000)

        val palette = createGraphics(paletteColumns, paletteRows, RenderMode.P2D)
        palette.noStroke()
        palette.pixelDensity(1)
        palette.colorMode(ColorMode.RGB, 1, 1, 1, 255)

        val qualitySlider     = createSlider(0, 10, 3, 0.1).apply { setScrollable() }
        val blendStartSlider  = createSlider(0, 1, 0.3, 1.0/255.0).apply { setScrollable() }
        val blendEndSlider    = createSlider(0, 1, 0.3, 1.0/255.0).apply { setScrollable() }
        val radiusStartSlider = createSlider(0, 1, 0.3, 1.0/255.0).apply { setScrollable() }
        val radiusEndSlider   = createSlider(0, 1, 0.3, 1.0/255.0).apply { setScrollable() }

        val quality     by qualitySlider
        val blendStart  by blendStartSlider
        val blendEnd    by blendEndSlider
        val radiusStart by radiusStartSlider
        val radiusEnd   by radiusEndSlider

        val diagonal = createVector(paletteRows, paletteColumns).mag()
        val count by { 10.0.pow(quality/2.0).toInt() }

        var tileable = true
        val smoothingCanvas = if(tileable) createGraphics(3*width, 3*height, RenderMode.P2D) else createGraphics(width, height, RenderMode.P2D)
        smoothingCanvas.noSmooth()

        fun smooth() {
            if(tileable) {
                for(xOffset in 0..2) {
                    for(yOffset in 0..2) {
                        val offset = createVector(xOffset*width, yOffset*height)
                        smoothingCanvas.image(this, offset, width, height)
                    }
                }
                smoothingCanvas.filter(FilterMode.BLUR, 5)
                image(smoothingCanvas, -width, -height, 3*width, 3*height)
            } else {
                smoothingCanvas.image(this, 0, 0, width, height)
                smoothingCanvas.filter(FilterMode.BLUR, 5)
                image(smoothingCanvas, 0, 0, width, height)
            }
        }

        var drawButtonAction: ()->Unit = {}
        val drawButton = createButton("Redraw").apply {
            text("Draw")
            mouseClicked { drawButtonAction() }
        }

        val smoothButton = createButton("Smooth").apply {
            mouseClicked { smooth() }
        }


        fun drawPalette() {
            val imageSize = createVector(sourceImage.width, sourceImage.height)
            clear()
            imageCanvas.clear()
            palette.clear()
            smoothingCanvas.clear()
            smoothingCanvas.resizeCanvas(if(tileable) createVector(3*width, 3*height) else createVector(width, height))
            imageCanvas.image(sourceImage, 0, 0, imageCanvas.width, imageCanvas.height)
            drawButtonAction = {
                noLoop()
                drawButton.text("Redraw")
                drawButtonAction = ::drawPalette
            }
            drawButton.text("Stop")

            DrawFor(0 until count) {frame ->

                val progress = frame/count.toDouble()
                val radius = map(progress, 0, 1, radiusStart, radiusEnd)*diagonal
                val randomPixelId = randInt(imageSize.x*imageSize.y)

                val randomPixelColor = sourceImage.withPixels { colorArray[randomPixelId].toVector() }
                val pixelBag: MutableList<Pair<Vector, Double>> = mutableListOf()

                palette.withPixels {
                    for (row in 0 until paletteRows.toInt()) {
                        for (col in 0 until paletteColumns.toInt()) {
                            val pixelColor = colorArray[row, col].toVector()
                            val colorDistance = dist(pixelColor, randomPixelColor)
                            pixelBag.add(createVector(col, row) to colorDistance)
                        }
                    }
                }

                val circleCenter = pixelBag.shuffled().minBy { it.second }.first
                val blendColor = randomPixelColor.toColor(map(progress, 0, 1, blendStart, blendEnd)*255.0)
                palette.fill(blendColor)
                if(tileable) {
                    for(xOffset in -1..1) {
                        for(yOffset in -1..1) { 
                            val offset = createVector(xOffset*palette.width, yOffset*palette.height)
                            palette.circle(circleCenter+offset, radius)
                        }
                    }
                } else {
                    palette.circle(circleCenter, radius)
                }
                image(palette, 0, 0, width, height)

            }.AfterDone {
                println("Done")
                drawButton.text("Redraw")
                drawButtonAction = ::drawPalette
            }
        }

        drawPalette()
        drawButtonAction = ::drawPalette

        imageCanvas.getCanvas().drop { file ->
            loadImage(file.data) {
                sourceImage = it
                drawPalette()
            }
        }

        Layout {
            ItemStyle { style("color", "#A9B7C5") }
            Row {
                GridStyle { style("grid-gap", "32px 32px") }
                GridStyle(false) {
                    style("margin-left", "32px")
                    style("margin-top", "32px")
                }
                Column {// Image Sources
                    GridStyle(false) {
                        style("background-color", "#2b2b2b")
                        style("padding", "32px ".repeat(4))
                        style("border-radius", "32px")
                    }
                    add(imageCanvas)
                    add(canvas)
                }
                Column { // Controls
                    GridStyle(false) {
                        style("background-color", "#2b2b2b")
                        style("padding", "32px ".repeat(4))
                        style("border-radius", "32px")
                    }
                    Column {
                        fun addSlider(name: String, slider: Slider, useCount: Boolean = false) {
                            Row {
                                val sliderTextValue by { if (useCount) count else slider.value().toFixed(2) }
                                val text = createP("${name}<br>$sliderTextValue")
                                GridStyle {
                                    size(width, 64, 1.5)
                                    style("grid-template-columns", 5.fr() + 2.fr())
                                    style("align-items", "center")
                                }
                                ItemStyle { style("width", "100%") }
                                add(slider)
                                add(text) { style("text-align", "start") }
                                slider.apply {
                                    input   { text.html("${name}<br>$sliderTextValue") }
                                    changed { text.html("${name}<br>$sliderTextValue") }
                                }
                            }
                        }
                        addSlider("Iterations", qualitySlider, true)
                        addSlider("Blend Start", blendStartSlider)
                        addSlider("Blend End", blendEndSlider)
                        addSlider("Radius Start", radiusStartSlider)
                        addSlider("Radius End", radiusEndSlider)
                    }
                    val radio = createRadio().apply {
                        option("Traditional")
                        option("Tileable")
                        value("Traditional")
                        changed {
                            tileable = value() == "Tileable"
                        }
                    }
                    addAll(arrayOf(radio, drawButton, smoothButton)) {
                        size(width, 64, 2)
                    }
                }
            }
        }


    }
}