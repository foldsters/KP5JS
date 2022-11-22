package p5.core

import p5.Sketch
import p5.ksl.ShaderScope
import p5.ksl.buildShader
import p5.util.ifNotNull

class WebGLCore private constructor() {
    private val attachedSketches: MutableSet<P5> = mutableSetOf()
    private val queuedSketches: MutableSet<P5> = mutableSetOf()

    val sketch = Sketch {
        Setup {
            createCanvas(0, 0, RenderMode.WEBGL2).apply {
                attribute("id", "webgl")
            }
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

    fun render(p5: P5) {
        queuedSketches.add(p5)
        sketch.p5.loop()
    }

    fun clear() {
        attachedSketches.clear()
        sketch.p5.clear()
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
        createCanvas(width, height).apply {
            attribute("id", "ShaderSketch")
        }
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