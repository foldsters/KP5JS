@file:Suppress("FunctionName")

package projects.testing

import SvgIcons
import p5.SimpleSketch
import p5.Sketch
import p5.core.*
import p5.kglsl.buildShader
import p5.kglsl.vec2
import p5.util.MutableValue
import p5.util.getTimestamp
import p5.util.mutableValueOf
import p5.core.P5.Vector
import projects.testing.CardWrapperData.loadIcons
import projects.testing.CardWrapperData.icons

object CardWrapperData {
    val icons = mutableMapOf<String, Image>()

    fun P5.loadIcons() {
        icons["download"]     = loadImage(SvgIcons.download)
        icons["favorite-off"] = loadImage("icons/star0.png")
        icons["favorite-on"]  = loadImage("icons/star1.png")
        icons["share"]        = loadImage("icons/share0.png")
    }
}


fun CardWrapper(sketch: P5) = Sketch {

    fun buildIcon(elementSource: MutableValue<Element?>, iconSource: ()->Image) = Sketch {
        Setup {
            createCanvas(64, 64, RenderMode.WEBGL2)
            pixelDensity(1)
            val iconShader = buildShader {
                Fragment {
                    val iconTexture   by Uniform(iconSource)
                    val sketchTexture by Uniform { sketch }
                    val resolution    by Uniform<vec2> { arrayOf(width, height) }
                    val highlight     by Uniform { elementSource.value?.isMouseOver ?: false }
                    Main {
                        val uv by gl_FragCoord.xy / resolution
                        uv.y = 1.0 - uv.y
                        val iconColor   by texture(iconTexture, uv)
                        val themeColor  by texture(sketchTexture, uv)
                        val tinted      by themeColor.rgb * iconColor.rgb
                        val highlighted by mix(vec3(1.0, 1.0, 1.0), tinted, float(highlight))
                        vec4(mix(vec3(43.0, 43.0, 43.0)/255.0, highlighted, iconColor.a), 1.0)
                    }
                }
            }
            var lastOver = false
            var nowOver: Boolean
            Draw {
                nowOver = elementSource.value?.isMouseOver == true
                if(nowOver) {
                    if(!lastOver) {
                        shader(iconShader)
                    }
                    iconShader.update()
                    rect(0, 0, 0, 0)
                } else {
                    if(lastOver) {
                        resetShader()
                    }
                }
                lastOver = nowOver
            }
        }
    }

    Setup {
        noCanvas()

        var favorited = false
        val downloadElement = mutableValueOf<Element>()
        val favoriteElement = mutableValueOf<Element>()
        val shareElement    = mutableValueOf<Element>()

        val downloadIcon = buildIcon(downloadElement) { icons["download"]!! }
        val favoriteIcon = buildIcon(favoriteElement) { icons[if(favorited) "favorite-on" else "favorite-off"]!! }
        val shareIcon    = buildIcon(shareElement)    { icons["share"]!! }

        Layout {
            GridStyle(inherit = false) {
                style("background-color", "#2b2b2b")
                style("padding", "32px ".repeat(4))
                style("border-radius", "32px")
            }
            Column {
                add(sketch)
                Row { // Buttons
                    GridStyle(false) {
                        style("margin-top", "32px")
                        style("justify-content", "space-between")
                        style("width", "100%") 
                    }
                    ItemStyle { style("cursor", "pointer") }
                    Stack {
                        container.mouseClicked { sketch.save("${getTimestamp()}.png") }
                        downloadElement.value = container
                        add(downloadIcon)
                    }
                    Stack {
                        container.mouseClicked { favorited = !favorited }
                        favoriteElement.value = container
                        add(favoriteIcon)
                    }
                    Stack {
                        container.mouseClicked { println("share!") }
                        shareElement.value = container
                        add(shareIcon)
                    }
                }
            }
        }
        remove()
    }
}

fun CardList() = Sketch {

    fun ExampleSketch(offset: Double) = SimpleSketch(512, 512) {
        background(((127.0*Vector.fromAngle(offset+millis()/2000.0)).xy + createVector(128, 128, 255)).toColor())
    }

    Setup {
        loadIcons()
        noCanvas()
        getBody().style("background-color", "#46484a")
        val cards = Array(10) { CardWrapper(ExampleSketch(it.toDouble()).p5) }
        Layout {
            GridStyle(false) { style("justify-items", "center") }
            Column { addAll(cards) { style("margin-bottom", "20px") } }
        }
        updateLayout()
    }
}