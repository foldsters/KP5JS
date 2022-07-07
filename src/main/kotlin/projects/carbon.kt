package projects

import p5.NativeP5
import p5.P5
import p5.Sketch

fun carbon(filename: String, scaleFactor: Double=1.0) = Sketch {

    lateinit var img: NativeP5.Image

    Preload {
        console.log("loading")
        img = loadImage(filename)
    }

    Setup {

        img.resize(img.width/2, img.height/2)

        val border = 400*scaleFactor
        val cornerRadius = 80*scaleFactor
        val barHeight = 120*scaleFactor
        val shadowRadius = 80*scaleFactor

        val circleSize = 60*scaleFactor
        val circleMarginX = 80*scaleFactor
        val circleMarginY = 80*scaleFactor
        val circleSpacing = 40*scaleFactor
        val circleColors = arrayOf(
            color(255, 95, 86),
            color(255, 189, 46),
            color(39, 201, 63)
        )

        val canvasWidth = img.width + 2 * (cornerRadius + border)
        val canvasHeight = img.height + barHeight + 2 * (cornerRadius + border)

        createCanvas(canvasWidth, canvasHeight)

        background(60, 63, 65)
        fill(43)
        noStroke()

        drawingContext.shadowBlur = shadowRadius
        drawingContext.shadowColor = "black"

        rect(
            border,
            border,
            img.width + 2 * cornerRadius,
            img.height + barHeight + 2 * cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius
        )

        drawingContext.shadowBlur = 0

        fill(255)
        ellipseMode(P5.CenterMode.CORNER)

        repeat(3) {
            fill(circleColors[it])
            circle(
                border + circleMarginX + (circleSize + circleSpacing) * it,
                border + circleMarginY, circleSize
            )
        }

        image(img, border + cornerRadius, border + barHeight + cornerRadius)

        createButton("Save").apply {
            fontSize(100)
            size(400, 200)
            mouseClicked {
                saveCanvas("code_snippet", P5.ImageExtension.PNG)
            }
        }

        console.log("drawn")
        console.log(img)
    }

}