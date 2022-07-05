package p5.util

import org.w3c.dom.Window
import kotlin.js.Json

infix fun ClosedRange<Int>.step(step: Double): Iterable<Double> {
    require(step > 0.0) { "Step must be positive, was: $step." }
    val sequence = generateSequence(start.toDouble()) { previous ->
        val next = previous + step
        if (next > endInclusive) null else next
    }
    return sequence.asIterable()
}

infix fun ClosedRange<Int>.stepByUntil(step: Double): Iterable<Double> {
    require(step > 0.0) { "Step must be positive, was: $step." }
    val sequence = generateSequence(start.toDouble()) { previous ->
        val next = previous + step
        if (next + step >= endInclusive) null else next
    }
    return sequence.asIterable()
}

infix fun ClosedRange<Double>.stepByUntil(step: Double): Iterable<Double> {
    require(start.isFinite())
    require(endInclusive.isFinite())
    require(step > 0.0) { "Step must be positive, was: $step." }
    val sequence = generateSequence(start) { previous ->
        if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
        val next = previous + step
        if (next + step >= endInclusive) null else next
    }
    return sequence.asIterable()
}

fun Json.setIfNotNull(propertyName: String, value: Any?) {
    if (value != null) {
        set(propertyName, value)
    }
}

fun Window.setTimeout(timeout: Int, arguments: Array<Any?>? = null, handler: dynamic) {
    if (arguments == null) {
        setTimeout(handler, timeout)
    } else {
        setTimeout(handler, timeout, *arguments)
    }
}

fun Number.toFixed(digits: Int): String = this.asDynamic().toFixed(digits) as String

inline fun <T> T.ifTrue(block: (T)->Unit) {
    if (this == true) {
        block(this)
    }
}

inline fun <T> T.ifFalse(block: (T)->Unit) {
    if (this == false) {
        block(this)
    }
}

inline fun <T> T.ifNull(block: (T)->Unit) {
    if (this == null) {
        block(this)
    }
}

inline fun <T: Any> T?.ifNotNull(block: (T)->Unit) {
    if (this != null) {
        block(this)
    }
}

fun StringBuilder.appendAll(iterable: Iterable<String>, sep: String="") {
    iterable.forEach { append(it, sep) }
}