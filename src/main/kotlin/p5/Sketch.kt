@file:Suppress("FunctionName")

package p5

import kotlinx.browser.window
import p5.core.AUTO
import p5.core.KeyboardEvent
import p5.core.P5
import p5.core.WheelEvent
import p5.native.NativeP5.*
import p5.util.println
import kotlin.math.max

fun Sketch(sketch: SketchScope.()->Unit) {
    window.onload = {
        P5 { p -> sketch(SketchScope(p)) }
    }
}

class SketchScope(val p5: P5) {

    private fun wrap(f: P5.()->Unit): ()->Unit {
        return { p5.f() }
    }

    private fun <T> wrap(f: P5.(T)->Unit): (T)->Unit {
        return { p5.f(it) }
    }

    fun Preload       (block: P5.()->Unit) { p5.preload = wrap(block) } //
    fun Setup         (block: P5.()->Unit) { p5.setup = wrap(block) } //
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

    private var topGrid: P5.Grid? = null

    // Layout
    fun Layout (block: P5.Grid.()->Unit) {
        with(p5) {
            val grid = topGrid ?: Grid()
            grid.action = {
                grid.Stack {
                    block()
                }
            }
            grid.update()
            topGrid = grid
        }
    }

    fun updateLayout() {
        topGrid?.update()
    }

    // New Scopes

    class DrawContinuation {
        var afterFrame: (()->Unit)? = null
        var afterDone: (()->Unit)? = null

        fun AfterFrame(continuation: ()->Unit): DrawContinuation { afterFrame = continuation; return this }
        fun AfterDone(continuation: ()->Unit): DrawContinuation { afterDone = continuation; return this }
    }

    fun Draw(stepsPerFrame: Int = 1, block: P5.(Int)->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        var frame = 0
        with(p5) {
            loop()
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
        var numStepsPerFrame = 1
        with(p5) {
            loop()
            draw = wrap {
                repeat(numStepsPerFrame) {
                    block(frame)
                    frame++
                }
                nextDraw.afterFrame?.invoke()
                numStepsPerFrame = (numStepsPerFrame/frameLagFactor()).toInt().coerceIn(max(1, numStepsPerFrame/10), numStepsPerFrame*10)
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
        var numStepsPerFrame = 1
        with(p5) {
            loop()
            draw = wrap {
                repeat(numStepsPerFrame) {
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
                numStepsPerFrame = (numStepsPerFrame/frameLagFactor()).toInt().coerceIn(max(1, numStepsPerFrame/10), numStepsPerFrame*10)
            }
        }
        return nextDraw
    }

    fun <T> DrawFor(iter: Iterable<T>, stepsPerFrame: Int = 1, block: P5.(T) -> Unit): DrawContinuation {
        val itor = iter.iterator()
        val nextDraw = DrawContinuation()
        with(p5) {
            loop()
            draw = wrap {
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
        return nextDraw
    }

    fun <T> DrawFor(iter: Iterable<T>, stepsPerFrame: AUTO, block: P5.(T) -> Unit): DrawContinuation {
        val itor = iter.iterator()
        val nextDraw = DrawContinuation()
        var numStepsPerFrame = 1
        with(p5) {
            loop()
            draw = wrap {
                repeat(numStepsPerFrame) {
                    if (itor.hasNext()) block(itor.next()) else {
                        nextDraw.afterDone?.invoke()
                        noLoop()
                        draw = {}
                    }
                }
                nextDraw.afterFrame?.invoke()
                numStepsPerFrame = (numStepsPerFrame/frameLagFactor()).toInt().coerceIn(max(1, numStepsPerFrame/10), numStepsPerFrame*10)
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
        var numStepsPerFrame = 1
        with(p5) {
            loop()
            draw = wrap {
                val timeDelta = timeit {
                    withPixels {
                        repeat(numStepsPerFrame) {
                            if (itor.hasNext()) block(itor.next()) else {
                                nextDraw.afterDone?.invoke()
                                noLoop()
                                draw = {}
                            }
                        }
                        nextDraw.afterFrame?.invoke()
                    }
                }
                numStepsPerFrame = (numStepsPerFrame*targetFrameTime()/timeDelta).toInt().coerceIn(max(1, numStepsPerFrame/10), numStepsPerFrame*10)
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
        var numStepsPerFrame = 1
        with(p5) {
            loop()
            draw = wrap {
                val timeDelta = timeit {
                    withPixels {
                        repeat(numStepsPerFrame) {
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
                numStepsPerFrame = (numStepsPerFrame*targetFrameTime()/timeDelta).toInt().coerceIn(max(1, numStepsPerFrame/10), numStepsPerFrame*10)
                println("numStepsPerFrame", numStepsPerFrame)
                println("timeDelta", timeDelta)
                println("lagFactor", timeDelta/targetFrameTime())
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
        var numStepsPerFrame = 1
        with(p5) {
            loop()
            draw = if(frames == null) {
                wrap {
                    using {
                        repeat(numStepsPerFrame) {
                            block(with)
                        }
                        nextDraw.afterFrame?.invoke()
                        numStepsPerFrame = (numStepsPerFrame/frameLagFactor()).toInt().coerceIn(max(1, numStepsPerFrame/10), numStepsPerFrame*10)
                    }
                }
            } else {
                wrap {
                    using {
                        repeat(frames) {
                            repeat(numStepsPerFrame) {
                                block(with)
                            }
                            nextDraw.afterFrame?.invoke()
                            numStepsPerFrame = (numStepsPerFrame/frameLagFactor()).toInt().coerceIn(max(1, numStepsPerFrame/10), numStepsPerFrame*10)
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