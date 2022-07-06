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

    private fun <T> wrap(f: P5.(T)->Unit): (T) -> Unit {
        return { v -> f(p5, v) }
    }

    fun Preload       (block: P5.()->Unit) { p5.preload = wrap(block) }
    fun Setup         (block: P5.()->Unit) { p5.setup = wrap(block) }
    fun Draw          (block: P5.()->Unit) { p5.draw = wrap(block) }
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
    fun MouseWheel    (block: P5.(NativeP5.WheelEvent)->Unit) { p5.mouseWheel = wrap(block) }
    fun TouchStarted  (block: P5.()->Unit) { p5.touchStarted = wrap(block) }
    fun TouchMoved    (block: P5.()->Unit) { p5.touchMoved = wrap(block) }
    fun TouchEnded    (block: P5.()->Unit) { p5.touchEnded = wrap(block) }

    // New Scopes

    fun DrawWhile(cond: ()->Boolean, block: P5.()->Unit) {
        p5.draw = wrap { if (cond()) block() else noLoop() }
    }

    fun <T> DrawFor(iter: Iterable<T>, stepsPerFrame: Int = 1, block: P5.(T) -> Unit) {
        val itor = iter.iterator()
        p5.draw = wrap {
            repeat(stepsPerFrame) {
                if (itor.hasNext()) block(itor.next()) else noLoop()
            }
        }
    }

    fun <T> DrawForWithPixels(iter: Iterable<T>, stepsPerFrame: Int = 1, block: P5.PixelScope.(T) -> Unit) {
        val itor = iter.iterator()
        p5.draw = wrap {
            withPixels {
                repeat(stepsPerFrame) {
                    if (itor.hasNext()) block(itor.next()) else noLoop()
                }
            }
        }
    }

    fun <T> DrawFor(itor: Iterator<T>, block: P5.(T) -> Unit) {
        p5.draw = wrap {
            if (itor.hasNext()) block(itor.next()) else noLoop()
        }
    }

    fun <T> DrawFor(itor: Iterator<T>, stepsPerFrame: Int, block: P5.(T) -> Unit) {
        p5.draw = wrap {
            repeat(stepsPerFrame) {
                if (itor.hasNext()) block(itor.next()) else noLoop()
            }
        }
    }

    fun <T> DrawFor(iter: Iterable<T>, onLastFrame: ()->Unit, block: P5.(T) -> Unit) {
        val itor = iter.iterator()
        p5.draw = wrap {
            if (itor.hasNext()) block(itor.next()) else {
                onLastFrame()
                noLoop()
            }
        }
    }

    enum class DrawFragmentMode {
        //PIXEL,
        //ROW,
        FRAME_COMPLETE,
        //FRAME_ELAPSED
    }

    fun DrawFragment(drawFragmentMode: DrawFragmentMode=DrawFragmentMode.FRAME_COMPLETE,
                     block: P5.(Number, Number, Number)->NativeP5.Color ) {
        p5.draw = wrap {
            val t = millis()/1000.0
            background(0)
            withPixels {
                repeat(height) { y ->
                    repeat(width) { x ->
                        colorArray[y, x] = block(x, y, t)
                    }
                }
            }
        }
    }


}