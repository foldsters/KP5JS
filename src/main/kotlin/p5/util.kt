package p5.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import org.w3c.dom.Window
import kotlin.js.Json
import kotlin.math.ln
import kotlin.random.Random.Default.nextDouble
import kotlin.reflect.KProperty
import kotlin.reflect.KType

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

fun StringBuilder.appendAll(iterable: Iterable<Any?>, sep: String="") {
    iterable.forEach { append(it, sep) }
}

fun StringBuilder.appendAll(array: Array<out Any?>, sep: String="") {
    array.forEach { append(it, sep) }
}

fun <T: Comparable<T>> List<T>.indexOfMax(): Int { return indexOf(maxOf { it })}

inline fun <T> Iterable<T>.withEach(action: T.()->Unit) {
    return this.forEach { action(it) }
}

inline fun <T> withEach(vararg receivers: T, action: T.()->Unit) {
    return receivers.forEach { action(it) }
}

fun println(message: Any?, message2: Any?, vararg messages: Any?, sep: String = " ") {
    val result = buildString {
        append(message, sep, message2, sep)
        appendAll(messages, sep)
    }
    println(result)
}

class FieldMap<T, V>(private val defaultValue: V) {
    private val fields: MutableMap<T, V> = mutableMapOf()

    operator fun getValue(thisRef: T, property: KProperty<*>): V {
        return fields[thisRef] ?: defaultValue
    }

    operator fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        fields[thisRef] = value
    }
}

class JsField<T, V>(val defaultValue: V) {
    operator fun getValue(thisRef: T, property: KProperty<*>): V {
        val fieldName = property.name
        thisRef ?: error("No class reference provided for JsField $fieldName")
        val result =  js("thisRef[fieldName]") as? V? ?: defaultValue
        console.log(thisRef)
        return result
    }

    operator fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        println("?")
        val fieldName = property.name
        thisRef ?: error("No class reference provided for JsField $fieldName")
        console.log(thisRef)
        js("thisRef[fieldName] = value")
    }
}

fun <L1, L2, R> List<Pair<L1, R>>.runOnFirst(block: List<L1>.() -> List<L2>): List<Pair<L2, R>> {
    return block( map{ it.first } ).zip( map{it.second} )
}

fun <L, R1, R2> List<Pair<L, R1>>.runOnSecond(block: List<R1>.() -> List<R2>): List<Pair<L, R2>> {
    return map{ it.first }.zip( block( map{it.second} ) )
}

fun <L, R> List<Pair<L, R>>.transpose(): Pair<List<L>, List<R>> {
    return map{it.first} to map{it.second}
}

fun <T, P> List<T>.runOver(getter: T.()->P, setter: T.(P)->T, block: List<P>.()->List<P>): List<T> {
    return zip(block(map{getter(it)})).map { setter(it.first, it.second) }
}

fun <T, P> List<T>.runOver(getter: T.()->P, setter: T.(P)->Unit, block: List<P>.()->List<P>) {
    zip(block(map{getter(it)})).forEach { setter(it.first, it.second) }
}

fun <T, P1, P2, R> List<T>.traverse(selector: T.()->P1, effect: List<P1>.()->List<P2>, result: T.(P2)->R): List<R> {
    return zip(effect(map{selector(it)})).map { result(it.first, it.second) }
}

fun <T, P, R> List<T>.traverse(selector: T.()->P, result: T.(P)->R): List<R> {
    return zip(map{selector(it)}).map { result(it.first, it.second) }
}

fun <T: @Serializable Any> kotlinx.serialization.json.Json.decodeFromString(kType: KType, string: String): T {
    return with(serializersModule) {
        decodeFromString(serializer(kType) as KSerializer<T>, string)
    }
}

fun <T: @Serializable Any> kotlinx.serialization.json.Json.encodeToString(kType: KType, value: T): String {
    return with(serializersModule) {
        encodeToString(serializer(kType) as KSerializer<T>, value)
    }
}

fun String.mapLines(transform: (String)->String): String {
    return split('\n').joinToString(separator = "\n", transform = transform)
}

fun String.mapLinesIndexed(transform: (Int, String)->String): String {
    return split('\n').mapIndexed(transform).joinToString(separator = "\n")
}

operator fun <T> (()->T).getValue(thisRef: Any?, property: KProperty<*>): T {
    return this()
}

inline fun <T, reified R> Array<T>.arrayMap(transform: (T)->R): Array<R> = Array(size) { transform(this[it]) }

fun setTimeout(delayMillis: Number, block: ()->Unit): Int {
    return js("setTimeout(block, delayMillis)") as Int
}

typealias Undefined = Nothing?
fun Undefined.load() {}

fun <T> weightedChoice(weightedCandidates: List<Pair<T, Double>>): T? {
    if (weightedCandidates.isEmpty()) return null
    val weightSum = weightedCandidates.sumOf { it.second }
    var thresholdWeight = nextDouble()*weightSum
    for ((candidate, weight) in weightedCandidates) {
        if (thresholdWeight <= weight) return candidate
        thresholdWeight -= weight
    }
    return null
}

class MutableValue<T: Any?>(var value: T)
fun <T: Any?> mutableValueOf(value: T) = MutableValue(value)

inline fun <T, R> Iterable<T>.mapWith(transform: T.()->R): List<R> = map { transform(it) }

inline fun <T, R: Comparable<R>> Collection<T>.takeSmallestBy(n: Int, crossinline selector: (T)->R): List<T> {
    if(n > size.toDouble()) return toList()
    if(n > ln(size.toDouble())) return sortedBy(selector).take(n)
    if(isEmpty()) return listOf()
    if(n == 1) return listOf(minBy(selector))
    val smallElements = ArrayList<T>()
    val smallWeights  = ArrayList<R>()
    var maxSmallWeight: R? = null
    var maxSmallIndexByWeight: Int = -1
    forEach { element ->
        val weight = selector(element)
        if (smallElements.size < n) {
            if ((maxSmallWeight ?: weight) <= weight) {
                maxSmallWeight = weight
                maxSmallIndexByWeight = smallWeights.size
            }
            smallElements.add(element)
            smallWeights.add(weight)
        } else {
            if(weight < maxSmallWeight!!) {
                smallElements[maxSmallIndexByWeight] = element
                smallWeights[maxSmallIndexByWeight] = weight
                maxSmallWeight = weight
                maxSmallIndexByWeight = smallWeights.indices.maxBy { smallWeights[it] }
            }
        }
    }
    return smallElements
}

inline fun <T, R: Comparable<R>> Collection<T>.takeLargestBy(n: Int, crossinline selector: (T)->R): List<T> {
    if(n >= size.toDouble()) return toList()
    if(n >= ln(size.toDouble())) return sortedBy(selector).takeLast(n)
    if(isEmpty()) return listOf()
    if(n == 1) return listOf(maxBy(selector))
    val largeElements = ArrayList<T>()
    val largeWeights  = ArrayList<R>()
    var minLargeWeight: R? = null
    var minLargeIndexByWeight: Int = -1
    forEach { element ->
        val weight = selector(element)
        if (largeElements.size < n) {
            if ((minLargeWeight ?: weight) >= weight) {
                minLargeWeight = weight
                minLargeIndexByWeight = largeWeights.size
            }
            largeElements.add(element)
            largeWeights.add(weight)
        } else {
            if(weight > minLargeWeight!!) {
                largeElements[minLargeIndexByWeight] = element
                largeWeights[minLargeIndexByWeight] = weight
                minLargeWeight = weight
                minLargeIndexByWeight = largeWeights.indices.minBy { largeWeights[it] }
            }
        }
    }
    return largeElements
}

fun Boolean.toInt() = if (this) 1 else 0


