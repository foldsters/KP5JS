@file:Suppress("FunctionName")

package p5

import p5.core.*
import p5.core.WebGLCore.Companion.getWebGLCore
import p5.native.NativeP5
import kotlin.math.max

fun SimpleSketch(width: Number, height: Number, loop: Boolean = true, onDraw: P5.(Int)->Unit) = Sketch {
    Setup {
        createCanvas(width, height)
        if(!loop) noLoop()
    }
    Draw { onDraw(it) }
}

class Sketch private constructor() {
    constructor(sketch: Sketch.()->Unit): this() {
        NativeP5 {
            p5 = P5(it)
            sketch()
        }
    }

    lateinit var p5: P5

    private fun wrap(f: P5.()->Unit): ()->Unit {
        return { p5.f() }
    }

    private fun <T> wrap(f: P5.(T)->Unit): (T)->Unit {
        return { p5.f(it) }
    }

    fun Preload       (block: P5.()->Unit) { p5.preload = wrap(block) }
    fun Setup         (block: P5.()->Unit) { p5.setup = wrap(block) }
    fun WindowResized (block: P5.()->Unit) { p5.windowResized = wrap(block) }
    fun DeviceMoved   (block: P5.()->Unit) { p5.deviceMoved = wrap(block) }
    fun DeviceTurned  (block: P5.()->Unit) { p5.deviceTurned = wrap(block) }
    fun DeviceShaken  (block: P5.()->Unit) { p5.deviceShaken = wrap(block) }
    fun MouseMoved    (block: P5.()->Unit) { p5.mouseMoved = wrap(block) }
    fun MouseDragged  (block: P5.()->Unit) { p5.mouseDragged = wrap(block) }
    fun MousePressed  (block: P5.()->Unit) { p5.mousePressed = wrap(block) }
    fun MouseReleased (block: P5.()->Unit) { p5.mouseReleased = wrap(block) }
    fun MouseClicked  (block: P5.()->Unit) { p5.mouseClicked = wrap(block) }
    fun DoubleClicked (block: P5.()->Unit) { p5.doubleClicked = wrap(block) }
    fun TouchStarted  (block: P5.()->Unit) { p5.touchStarted = wrap(block) } // TODO: Use Touch Screen Device
    fun TouchMoved    (block: P5.()->Unit) { p5.touchMoved = wrap(block) } // TODO: Use Touch Screen Device
    fun TouchEnded    (block: P5.()->Unit) { p5.touchEnded = wrap(block) } // TODO: Use Touch Screen Device
    fun KeyPressed    (block: KeyboardEvent.()->Unit) { p5.keyPressed =  { keyboardEvent -> block(keyboardEvent) } }
    fun KeyReleased   (block: KeyboardEvent.()->Unit) { p5.keyReleased = { keyboardEvent -> block(keyboardEvent) } }
    fun KeyTyped      (block: KeyboardEvent.()->Unit) { p5.keyTyped = { keyboardEvent -> block(keyboardEvent) } }
    fun MouseWheel    (block: WheelEvent.()->Unit) { p5.mouseWheel = { wheelEvent -> block(wheelEvent) } }

    // Layout
    fun Layout(block: P5.Grid.()->Unit) {
        p5.makeLayout(block)
    }

    fun updateLayout() {
        p5.layout?.update() ?: console.warn("No layout found to update")
    }

    private var autoStepsPerFrame = 1
    fun getAutoStepsPerFrame() = autoStepsPerFrame

    // New Scopes
    fun autoAdjustSteps(block: (Int)->Unit) {
        with(p5) {
            val thisFrameTime = timeit { block(autoStepsPerFrame) }
            if(thisFrameTime > targetFrameTime()) {
                autoStepsPerFrame = max(autoStepsPerFrame-1, 1)
            } else {
                autoStepsPerFrame++
            }
//            deltaTime
//            autoStepsPerFrame = (0.9*autoStepsPerFrame*targetFrameTime()/timeDelta).toInt().coerceIn(max(1, autoStepsPerFrame/10), autoStepsPerFrame*10)
        }
    }

    class DrawContinuation {
        var afterFrame: (()->Unit)? = null
        var afterDone: (()->Unit)? = null

        fun AfterFrame(continuation: ()->Unit): DrawContinuation { afterFrame = continuation; return this }
        fun AfterDone(continuation: ()->Unit): DrawContinuation { afterDone = continuation; return this }
    }

    fun Draw(stepsPerFrame: Int = 1, autoStart: Boolean=true, block: P5.(Int)->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        var frame = 0
        with(p5) {
            if(autoStart) loop() else noLoop()
            draw = wrap {
                repeat(stepsPerFrame) {
                    block(frame)
                    frame++
                }
                nextDraw.afterFrame?.invoke()
            }
        }
        return nextDraw
    }

    fun Draw(stepsPerFrame: AUTO, block: P5.(Int)->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        var frame = 0
        autoStepsPerFrame = 1
        with(p5) {
            loop()
            draw = wrap {
                autoAdjustSteps {
                    repeat(it) {
                        block(frame)
                        frame++
                    }
                    nextDraw.afterFrame?.invoke()
                }
            }
        }
        return nextDraw
    }

    fun DrawWithPixels(stepsPerFrame: Int = 1, autoStart: Boolean=true, block: P5.PixelScope.(Int)->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        var frame = 0
        with(p5) {
            if(autoStart) loop() else noLoop()
            draw = wrap {
                withPixels {
                    repeat(stepsPerFrame) {
                        block(frame)
                        frame++
                    }
                    nextDraw.afterFrame?.invoke()
                }
            }
        }
        return nextDraw
    }

    fun DrawWithPixels(stepsPerFrame: AUTO, block: P5.PixelScope.(Int)->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        var frame = 0
        autoStepsPerFrame = 1
        with(p5) {
            loop()
            draw = wrap {
                withPixels {
                    autoAdjustSteps {
                        repeat(it) {
                            block(frame)
                            frame++
                        }
                        nextDraw.afterFrame?.invoke()
                    }
                }
            }
        }
        return nextDraw
    }

    fun DrawWhile(cond: ()->Boolean, stepsPerFrame: Int = 1, block: P5.(Int)->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        var frame = 0
        with(p5) {
            loop()
            draw = wrap {
                repeat(stepsPerFrame) {
                    if (cond()) {
                        block(frame)
                        frame++
                    } else {
                        nextDraw.afterDone?.invoke()
                        noLoop()
                        draw = {}
                    }
                }
                nextDraw.afterFrame?.invoke()
            }
        }
        return nextDraw
    }

    fun DrawWhile(cond: ()->Boolean, stepsPerFrame: AUTO, block: P5.(Int)->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        var frame = 0
        autoStepsPerFrame = 1
        with(p5) {
            loop()
            draw = wrap {
                autoAdjustSteps {
                    repeat(it) {
                        if (cond()) {
                            block(frame)
                            frame++
                        } else {
                            nextDraw.afterDone?.invoke()
                            noLoop()
                            draw = {}
                        }
                    }
                }
                nextDraw.afterFrame?.invoke()
            }
        }
        return nextDraw
    }

    fun <T> DrawFor(iter: Iterable<T>, stepsPerFrame: Int = 1, loop: Boolean=false, block: P5.(T) -> Unit): DrawContinuation {
        var itor = iter.iterator()
        val nextDraw = DrawContinuation()
        with(p5) {
            loop()
            draw = wrap {
                repeat(stepsPerFrame) {
                    if (itor.hasNext()) {
                        block(itor.next())
                    } else if(loop) {
                        itor = iter.iterator()
                    } else {
                        nextDraw.afterDone?.invoke()
                        noLoop()
                        draw = {}
                    }
                }
                nextDraw.afterFrame?.invoke()
            }
        }
        return nextDraw
    }

    fun <T> DrawFor(iter: Iterable<T>, stepsPerFrame: AUTO, block: P5.(T) -> Unit): DrawContinuation {
        val itor = iter.iterator()
        val nextDraw = DrawContinuation()
        autoStepsPerFrame = 1
        with(p5) {
            loop()
            draw = wrap {
                autoAdjustSteps {
                    repeat(it) {
                        if (itor.hasNext()) block(itor.next()) else {
                            nextDraw.afterDone?.invoke()
                            noLoop()
                            draw = {}
                        }
                    }
                    nextDraw.afterFrame?.invoke()
                }
            }
        }
        return nextDraw
    }

    fun <T> DrawForWithPixels(iter: Iterable<T>, stepsPerFrame: Int = 1, block: P5.PixelScope.(T) -> Unit): DrawContinuation {
        val itor = iter.iterator()
        val nextDraw = DrawContinuation()
        with(p5) {
            loop()
            draw = wrap {
                withPixels {
                    repeat(stepsPerFrame) {
                        if (itor.hasNext()) block(itor.next()) else {
                            nextDraw.afterDone?.invoke()
                            noLoop()
                            draw = {}
                        }
                    }
                    nextDraw.afterFrame?.invoke()
                }
            }
        }
        return nextDraw
    }

    fun <T> DrawForWithPixels(iter: Iterable<T>, stepsPerFrame: AUTO, block: P5.PixelScope.(T) -> Unit): DrawContinuation {
        val itor = iter.iterator()
        val nextDraw = DrawContinuation()
        autoStepsPerFrame = 1
        with(p5) {
            loop()
            draw = wrap {
                autoAdjustSteps {
                    withPixels {
                        repeat(it) {
                            if (itor.hasNext()) block(itor.next()) else {
                                nextDraw.afterDone?.invoke()
                                noLoop()
                                draw = {}
                            }
                        }
                        nextDraw.afterFrame?.invoke()
                    }
                }
            }
        }
        return nextDraw
    }

    fun DrawWhileWithPixels(cond: ()->Boolean, stepsPerFrame: Int = 1, block: P5.PixelScope.()->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        //var frame = 0
        with(p5) {
            loop()
            draw = wrap {
                withPixels {
                    repeat(stepsPerFrame) {
                        if (cond()) {
                            block()
                            //frame++
                        } else {
                            nextDraw.afterDone?.invoke()
                            noLoop()
                            draw = {}
                        }
                    }
                    nextDraw.afterFrame?.invoke()
                }
            }
        }
        return nextDraw
    }

    fun DrawWhileWithPixels(cond: ()->Boolean, stepsPerFrame: AUTO, block: P5.PixelScope.()->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        autoStepsPerFrame = 1
        with(p5) {
            loop()
            draw = wrap {
                autoAdjustSteps {
                    withPixels {
                        repeat(it) {
                            if (cond()) {
                                block()
                            } else {
                                nextDraw.afterDone?.invoke()
                                noLoop()
                                draw = {}
                            }
                        }
                        nextDraw.afterFrame?.invoke()
                    }
                }
            }
        }
        return nextDraw
    }

    fun <T> DrawUsing(frames: Int? = null, stepsPerFrame: Int = 1, with: T, using: (()->Unit)->Unit, block: T.()->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        with(p5) {
            loop()
            draw = if(frames == null) {
                wrap {
                    using {
                        repeat(stepsPerFrame) {
                            block(with)
                        }
                        nextDraw.afterFrame?.invoke()
                    }
                }
            } else {
                wrap {
                    using {
                        repeat(frames) {
                            repeat(stepsPerFrame) {
                                block(with)
                            }
                            nextDraw.afterFrame?.invoke()
                        }
                        nextDraw.afterDone?.invoke()
                        noLoop()
                        draw = {}
                    }
                }
            }
        }
        return nextDraw
    }

    fun <T> DrawUsing(frames: Int? = null, stepsPerFrame: AUTO, with: T, using: (()->Unit)->Unit, block: T.()->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        autoStepsPerFrame = 1
        with(p5) {
            loop()
            draw = if(frames == null) {
                wrap {
                    using {
                        autoAdjustSteps {
                            repeat(it) {
                                block(with)
                            }
                            nextDraw.afterFrame?.invoke()
                        }
                    }
                }
            } else {
                wrap {
                    using {
                        repeat(frames) {
                            autoAdjustSteps {
                                repeat(it) {
                                    block(with)
                                }
                                nextDraw.afterFrame?.invoke()
                            }
                        }
                        nextDraw.afterDone?.invoke()
                        noLoop()
                        draw = {}
                    }
                }
            }
        }
        return nextDraw
    }


}