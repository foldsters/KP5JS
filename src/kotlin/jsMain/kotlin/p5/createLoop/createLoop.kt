@file:Suppress("UNUSED_PARAMETER", "SYNTAX_ERROR")

package p5.createLoop

import kotlin.js.Json

fun nativeCreateLoop(options: Json): dynamic {
    //val loopPackage = js("import('createLoop')")
    return js("window.createLoop(options)")
}