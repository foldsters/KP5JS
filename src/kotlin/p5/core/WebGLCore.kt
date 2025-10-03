package p5.core

import p5.Sketch
import p5.ksl.*
import kotlin.math.max
import kotlin.math.round


class WebGLCore private constructor() {
    private val attachedSketches: MutableSet<P5> = mutableSetOf()
    private val queuedSketches: MutableSet<P5> = mutableSetOf()

    val sketch = Sketch {
        Setup {
            createCanvas(1, 1, RenderMode.WEBGL2)
            pixelDensity(1)
            background(0, 0, 0, 0)
            noStroke()
            Draw(autoStart = true) {
                for (sketch in queuedSketches) { sketch.redraw() }
                queuedSketches.clear()
                for (sketch in attachedSketches) { sketch.redraw() }
                if(attachedSketches.isEmpty()) {
                    resetShader()
                    noLoop()
                }
            }
        }
    }

    fun attach(p5: P5) {
        attachedSketches.add(p5)
        sketch.p5.loop()
    }

    fun detach(p5: P5) { attachedSketches.remove(p5) }

    fun clear() {
        attachedSketches.clear()
        sketch.p5.clear()
    }

    val renderer by sketch::p5

    fun render(shader: Shader, width: Number, height: Number): Image {
        renderer.clear()
        renderer.resizeCanvas(width, height)
        renderer.shader(shader)
        renderer.apply { shader.update() }
        renderer.rect(0, 0, width, height)
        return renderer.get(0, 0, width, height)
    }

    companion object {
        const val numContexts = 8
        private val coreArray: Array<WebGLCore?> = Array(numContexts) { null }

        fun getWebGLCore(core: Int): WebGLCore {
            with(WebGLCore) {
                require(core in 0 until numContexts) { "Must be a valid core number (0 to ${numContexts})" }
                if (coreArray[core] == null) { coreArray[core] = WebGLCore() }
            }
            return coreArray[core]!!
        }
    }
}


fun LiteShaderSketch(width: Number, height: Number, shader: Shader, webGLCoreIndex: Int) = Sketch {
    Setup {
        createCanvas(width, height)
        pixelDensity(1)
        val webGLCore = WebGLCore.getWebGLCore(webGLCoreIndex)
        val webGLRenderer = webGLCore.sketch.p5
        Draw(autoStart=false) {
            webGLRenderer.resizeCanvas(width, height)
            webGLRenderer.shader(shader)
            shader.update()
            webGLRenderer.rect(0, 0, width, height)
            image(webGLRenderer, 0, 0, width, height)
        }
    }
}


fun ShaderSketch(width: Number, height: Number,
                 webGLCoreIndex: Int, debug: Boolean = false,
                 updateMipmap: Boolean = false,
                 minFilterMode: MinFilterMode?=null,
                 magFilterMode: MagFilterMode? = null,
                 shaderBuilder: ShaderScope.(Sketch)->Unit) = Sketch {
    Setup {
        createCanvas(width, height)
        pixelDensity(1)
        noStroke()
        val webGLCore = WebGLCore.getWebGLCore(webGLCoreIndex)
        val webGLRenderer = webGLCore.sketch.p5
        val shader = webGLRenderer.buildShader(debug) {
            shaderBuilder(this@Sketch)
        }
        minFilterMode?.let { shader.minFilterMode = it }
        magFilterMode?.let { shader.magFilterMode = it }
        Draw(autoStart=false) {
            webGLRenderer.resizeCanvas(width, height)
            webGLRenderer.shader(shader)
            webGLRenderer.apply { shader.update(updateMipmap) }
            webGLRenderer.rect(0, 0, width, height)
            image(webGLRenderer, 0, 0, width, height)
        }
    }
}


class ShaderPass(webGLCoreIndex: Int, shaderBuilder: ShaderScope.()->Unit) {

    val webGLCore = WebGLCore.getWebGLCore(webGLCoreIndex)
    val webGLRenderer = webGLCore.sketch.p5
    val shader = webGLCore.sketch.p5.buildShader(debug = true) {
        shaderBuilder()
    }

    var searchImage: Image? = null
    var resolution: Array<Number>? = null

    val maximaShader = webGLRenderer.buildShader {
        Fragment {
            val image by UniformImage {
                searchImage ?: error("Searching null Image")
            }
            val res by Uniform<vec2> {
                resolution ?: error("Invalid search shader configuration")
            }

            Main {
                val foldXs = int(floor(it.y)) EQ 1
                val dist = int(floor(it.x))
                var maxVal by float(0.0)

                IF(foldXs) {
                    FOR(int(res.y)) { y ->
                        val searchColor by texelFetch(image, ivec2(dist, y), int(0))
                        val searchVal by round(searchColor.r * 255.0) + searchColor.g
                        maxVal = max(searchVal, maxVal)
                    }
                } ELSE {
                    FOR(int(res.x)) { x ->
                        val searchColor by texelFetch(image, ivec2(x, dist), int(0))
                        val searchVal by round(searchColor.r * 255.0) + searchColor.g
                        maxVal = max(searchVal, maxVal)
                    }

                }

                val maxColor by vec4(maxVal / 255.0, fract(maxVal), 0.0, 0.0)
                maxColor
            }
        }
    }

    fun redraw(width: Number, height: Number): Image = webGLCore.render(shader, width, height)

    fun P5.findMaxima(width: Number, height: Number): List<P5.Vector> {
        searchImage = redraw(width, height)
        resolution = arrayOf(width, height)
        val collapsedImage = webGLCore.render(maximaShader, max(width.toDouble(), height.toDouble()), 2)
        var maxVal = 0.0
        val maxXs = mutableListOf<Int>()
        val maxYs = mutableListOf<Int>()
        val results = mutableListOf<P5.Vector>()

        collapsedImage.withPixels {
            colorMode(ColorMode.RGB, 255, 255, 255, 1)

            for (x in 0..width.toInt()) {
                val searchColor = colorArray[createVector(x, 0)]
                val searchVal = round(red(searchColor)) + green(searchColor) / 255.0
                if (searchVal == maxVal) {
                    maxXs.add(x)
                } else if (searchVal > maxVal) {
                    maxXs.clear()
                    maxXs.add(x)
                    maxVal = searchVal
                }
            }

            for (x in 0..width.toInt()) {
                val searchColor = colorArray[createVector(x, 1)]
                val searchVal = round(red(searchColor)) + green(searchColor) / 255.0
                if (searchVal == maxVal) {
                    maxYs.add(x)
                }
            }
        }

        searchImage!!.withPixels {
            maxXs.forEach { x ->
                maxYs.forEach { y ->
                    val searchColor = colorArray[createVector(x, y)]
                    val searchVal = round(red(searchColor)) + green(searchColor) / 255.0
                    if (searchVal == maxVal) {
                        results.add(createVector(x, y))
                    }
                }
            }
        }

        return results
    }
}














