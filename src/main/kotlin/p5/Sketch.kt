@file:Suppress("FunctionName")

package p5

import kotlinx.browser.window

fun Sketch(sketch: SketchScope.()->Unit) {
    window.onload = {
        P5 { p -> sketch(SketchScope(p)) }
    }
}

class SketchScope(val p5: P5) {

    private fun wrap(f: P5.()->Unit): () -> Unit {
        return { f(p5) }
    }

    fun Preload       (block: P5.()->Unit) { p5.preload = wrap(block) }
    fun Setup         (block: P5.()->Unit) { p5.setup = wrap(block) }
    fun WindowResized (block: P5.()->Unit) { p5.windowResized = wrap(block) }
    fun DeviceMoved   (block: P5.()->Unit) { p5.deviceMoved = wrap(block) }
    fun DeviceTurned  (block: P5.()->Unit) { p5.deviceTurned = wrap(block) }
    fun DeviceShaken  (block: P5.()->Unit) { p5.deviceShaken = wrap(block) }
    fun KeyPressed    (block: P5.()->Unit) { p5.keyPressed = wrap(block) }
    fun KeyReleased   (block: P5.()->Unit) { p5.keyReleased = wrap(block) }
    fun KeyTyped      (block: P5.()->Unit) { p5.keyTyped = wrap(block) }
    fun MouseMoved    (block: P5.()->Unit) { p5.mouseMoved = wrap(block) }
    fun MouseDragged  (block: P5.()->Unit) { p5.mouseDragged = wrap(block) }
    fun MousePressed  (block: P5.()->Unit) { p5.mousePressed = wrap(block) }
    fun MouseReleased (block: P5.()->Unit) { p5.mouseReleased = wrap(block) }
    fun MouseClicked  (block: P5.()->Unit) { p5.mouseClicked = wrap(block) }
    fun DoubleClicked (block: P5.()->Unit) { p5.doubleClicked = wrap(block) }
    fun TouchStarted  (block: P5.()->Unit) { p5.touchStarted = wrap(block) }
    fun TouchMoved    (block: P5.()->Unit) { p5.touchMoved = wrap(block) }
    fun TouchEnded    (block: P5.()->Unit) { p5.touchEnded = wrap(block) }
    fun MouseWheel    (block: NativeP5.WheelEvent.()->Unit) { p5.mouseWheel = { wheelEvent -> block(wheelEvent) } }

    // New Scopes

    class DrawContinuation {
        var afterFrame: (()->Unit)? = null
        var afterDone: (()->Unit)? = null

        fun AfterFrame(continuation: ()->Unit) { afterFrame = continuation }
        fun AfterDone(continuation: ()->Unit) { afterDone = continuation }
    }

    fun Draw(steps: Int = 1, block: P5.()->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        with(p5) {
            draw = wrap {
                repeat(steps) {
                    loop()
                    block()
                }
                nextDraw.afterFrame?.invoke()
                nextDraw.afterDone?.invoke()
                noLoop()
                draw = null
            }
        }
        return nextDraw
    }

    fun DrawWhile(cond: ()->Boolean, stepsPerFrame: Int = 1, block: P5.()->Unit): DrawContinuation {
        val nextDraw = DrawContinuation()
        with(p5) {
            loop()
            draw = wrap {
                repeat(stepsPerFrame) {
                    if (cond()) {
                        block()
                    } else {
                        nextDraw.afterDone?.invoke()
                        noLoop()
                        draw = null
                    }
                }
                nextDraw.afterFrame?.invoke()
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
                        draw = {
                            nextDraw.afterDone?.invoke()
                            noLoop()
                            draw = null
                        }
                    }
                }
                nextDraw.afterFrame?.invoke()
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
                            nextDraw.afterFrame?.invoke()
                            noLoop()
                            draw = null
                        }
                    }
                }
            }
        }
        return nextDraw
    }

//    enum class DrawFragmentMode {
//        //PIXEL,
//        //ROW,
//        FRAME_COMPLETE,
//        //FRAME_ELAPSED
//    }
//
//    fun DrawFragment(drawFragmentMode: DrawFragmentMode=DrawFragmentMode.FRAME_COMPLETE,
//                     block: P5.(Number, Number, Number)->NativeP5.Color ) {
//        p5.draw = wrap {
//            val t = millis()/1000.0
//            background(0)
//            withPixels {
//                repeat(height) { y ->
//                    repeat(width) { x ->
//                        colorArray[y, x] = block(x, y, t)
//                    }
//                }
//            }
//        }
//    }


}