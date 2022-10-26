package p5.core

import p5.Sketch

class WebGLCore private constructor() {
    private val attachedSketches: MutableSet<P5> = mutableSetOf()
    private val queuedSketches: MutableSet<P5> = mutableSetOf()

    val sketch = Sketch {
        Setup {
            createCanvas(0, 0, RenderMode.WEBGL2).apply { hide() }
            pixelDensity(1)
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