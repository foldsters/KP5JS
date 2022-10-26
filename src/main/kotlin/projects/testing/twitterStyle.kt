@file:Suppress("FunctionName")

package projects.testing

import p5.SimpleSketch
import p5.Sketch
import p5.core.*
import p5.ksl.buildShader
import p5.ksl.vec2
import p5.util.getTimestamp
import p5.util.mutableValueOf
import p5.core.P5.Vector
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.util.MutableValue
import projects.testing.CardWrapperData.loadIcons
import projects.testing.CardWrapperData.icons

object CardWrapperData {
    val icons = mutableMapOf<String, Image>()
    fun P5.loadIcons() {
        icons["download"]     = loadImage("icons/download.png")
        icons["favorite-off"] = loadImage("icons/star0.png")
        icons["favorite-on"]  = loadImage("icons/star1.png")
        icons["share"]        = loadImage("icons/share0.png")
    }
}

fun CardWrapper(sketch: P5) = Sketch {

    Setup {
        noCanvas()

        var favorited = false
        val downloadElement = mutableValueOf<Element>()
        val favoriteElement = mutableValueOf<Element>()
        val shareElement    = mutableValueOf<Element>()
        var cardElement: Element? = null

        val webGLCore = getWebGLCore(0)
        val webGLRenderer = webGLCore.sketch.p5

        fun P5.buildIconShader(iconElement: MutableValue<Element?>, textureSource: () -> Image): Shader = buildShader {
            Fragment {
                val iconTexture by Uniform(textureSource)
                val sketchTexture by Uniform { sketch }
                val resolution by Uniform<vec2> { arrayOf(width, height) }
                val highlight by Uniform { iconElement.value?.isMouseOver ?: false }
                Main {
                    val uv by gl_FragCoord.xy / resolution
                    uv.y = 1.0 - uv.y
                    val iconColor by texture(iconTexture, uv)
                    val themeColor by texture(sketchTexture, uv)
                    val tinted by themeColor.rgb * iconColor.rgb
                    val highlighted by mix(vec3(1.0, 1.0, 1.0), tinted, float(!highlight))
                    vec4(mix(vec3(43.0, 43.0, 43.0) / 255.0, highlighted, iconColor.a), 1.0)
                }
            }
        }

        val downloadIcon = LiteShaderSketch(64, 64, webGLRenderer.buildIconShader(downloadElement) { icons["download"]!! }, 0)
        val favoriteIcon = LiteShaderSketch(64, 64, webGLRenderer.buildIconShader(favoriteElement) { icons[if (favorited) "favorite-on" else "favorite-off"]!! }, 0)
        val shareIcon    = LiteShaderSketch(64, 64, webGLRenderer.buildIconShader(shareElement)    { icons["share"]!! }, 0)

        val icons = arrayOf(downloadIcon, favoriteIcon, shareIcon)

        Draw {
            for(icon in icons) {
                if(cardElement?.isMouseOver == true) {
                    webGLCore.attach(icon.p5)
                } else {
                    webGLCore.detach(icon.p5)
                }
            }
        }

        Layout {
            GridStyle(inherit = false) {
                style("background-color", "#2b2b2b")
                style("padding", "32px ".repeat(4))
                style("border-radius", "32px")
            }
            Column {
                cardElement = this.container
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
            GridStyle(false) {
                style("justify-items", "center")
                style("margin-top", "32px")
            }
            Column { addAll(cards) { style("margin-bottom", "32px") } }
        }
        updateLayout()
    }
}