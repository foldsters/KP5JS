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

    fun preload       (block: P5.()->Unit) { p5.preload = wrap(block) }
    fun setup         (block: P5.()->Unit) { p5.setup = wrap(block) }
    fun draw          (block: P5.()->Unit) { p5.draw = wrap(block) }
    fun windowResized (block: P5.()->Unit) { p5.windowResized = wrap(block) }
    fun deviceMoved   (block: P5.()->Unit) { p5.deviceMoved = wrap(block) }
    fun deviceTurned  (block: P5.()->Unit) { p5.deviceTurned = wrap(block) }
    fun deviceShaken  (block: P5.()->Unit) { p5.deviceShaken = wrap(block) }
    fun keyPressed    (block: P5.()->Unit) { p5.keyPressed = wrap(block) }
    fun keyReleased   (block: P5.()->Unit) { p5.keyReleased = wrap(block) }
    fun keyTyped      (block: P5.()->Unit) { p5.keyTyped = wrap(block) }
    fun mouseMoved    (block: P5.()->Unit) { p5.mouseMoved = wrap(block) }
    fun mouseDragged  (block: P5.()->Unit) { p5.mouseDragged = wrap(block) }
    fun mousePressed  (block: P5.()->Unit) { p5.mousePressed = wrap(block) }
    fun mouseReleased (block: P5.()->Unit) { p5.mouseReleased = wrap(block) }
    fun mouseClicked  (block: P5.()->Unit) { p5.mouseClicked = wrap(block) }
    fun doubleClicked (block: P5.()->Unit) { p5.doubleClicked = wrap(block) }
    fun mouseWheel    (block: P5.(NativeP5.WheelEvent)->Unit) { p5.mouseWheel = wrap(block) }
    fun touchStarted  (block: P5.()->Unit) { p5.touchStarted = wrap(block) }
    fun touchMoved    (block: P5.()->Unit) { p5.touchMoved = wrap(block) }
    fun touchEnded    (block: P5.()->Unit) { p5.touchEnded = wrap(block) }

}