package p5.core

import p5.native.*

class KeyboardEvent(val nativeKeyboardEvent: NativeKeyboardEvent) {
    val altKey: Boolean by nativeKeyboardEvent::altKey
    val bubbles: Boolean by nativeKeyboardEvent::bubbles
    val cancelBubble: Boolean by nativeKeyboardEvent::cancelBubble
    val cancelable: Boolean by nativeKeyboardEvent::cancelable
    val charCode: Int by nativeKeyboardEvent::charCode
    val code: String by nativeKeyboardEvent::code
    val composed: Boolean by nativeKeyboardEvent::composed
    val ctrlKey: Boolean by nativeKeyboardEvent::ctrlKey
    val currentTarget: dynamic by nativeKeyboardEvent::currentTarget
    val defaultPrevented: Boolean by nativeKeyboardEvent::defaultPrevented
    val detail: Double by nativeKeyboardEvent::detail
    val eventPhase: Int by nativeKeyboardEvent::eventPhase
    val isComposing: Boolean by nativeKeyboardEvent::isComposing
    val key: String by nativeKeyboardEvent::key
    val keyCode: Int by nativeKeyboardEvent::keyCode
    val location: Double by nativeKeyboardEvent::location
    val metaKey: Boolean by nativeKeyboardEvent::metaKey
    val repeat: Boolean by nativeKeyboardEvent::repeat
    val returnValue: Boolean by nativeKeyboardEvent::returnValue
    val shiftKey: Boolean by nativeKeyboardEvent::shiftKey
    val timeStamp: Double by nativeKeyboardEvent::timeStamp
    val type: String by nativeKeyboardEvent::type
    val which: Int by nativeKeyboardEvent::which
}

open class Event(val nativeEvent: NativeEvent) {
    val isTrusted: Boolean = nativeEvent.isTrusted
    val bubbles: Boolean = nativeEvent.bubbles
    val cancelBubble: Boolean = nativeEvent.cancelBubble
    val cancelable: Boolean = nativeEvent.cancelable
    val composed: Boolean = nativeEvent.composed
    val currentTarget: dynamic = nativeEvent.currentTarget
    val defaultPrevented: Boolean = nativeEvent.defaultPrevented
    val eventPhase: Int = nativeEvent.eventPhase
    val returnValue: Boolean = nativeEvent.returnValue
    val srcElement: NativeElement? = nativeEvent.srcElement
    val target: NativeElement? = nativeEvent.target
    val timeStamp: Double = nativeEvent.timeStamp
    val type: String = nativeEvent.type
}

class WheelEvent(val nativeWheelEvent: NativeWheelEvent): Event(nativeWheelEvent) {
    val altKey: Boolean = nativeWheelEvent.altKey
    val button: Int = nativeWheelEvent.button
    val buttons: Int = nativeWheelEvent.buttons
    val clientX: Double = nativeWheelEvent.clientX
    val clientY: Double = nativeWheelEvent.clientY
    val ctrlKey: Boolean = nativeWheelEvent.ctrlKey
    val deltaMode: Int = nativeWheelEvent.deltaMode
    val deltaX: Double = nativeWheelEvent.deltaX
    val deltaY: Double = nativeWheelEvent.deltaY
    val deltaZ: Double = nativeWheelEvent.deltaZ
    val detail: Double = nativeWheelEvent.detail
    val fromElement: dynamic = nativeWheelEvent.fromElement
    val layerX: Double = nativeWheelEvent.layerX
    val layerY: Double = nativeWheelEvent.layerY
    val metaKey: Boolean = nativeWheelEvent.metaKey
    val movementX: Double = nativeWheelEvent.movementX
    val movementY: Double = nativeWheelEvent.movementY
    val pageX: Double = nativeWheelEvent.pageX
    val pageY: Double = nativeWheelEvent.pageY
    val relatedTarget: dynamic = nativeWheelEvent.relatedTarget
    val screenX: Double = nativeWheelEvent.screenX
    val screenY: Double = nativeWheelEvent.screenY
    val shiftKey: Boolean = nativeWheelEvent.shiftKey
    val wheelDelta: Double = nativeWheelEvent.wheelDelta
    val wheelDeltaX: Double = nativeWheelEvent.wheelDeltaX
    val wheelDeltaY: Double = nativeWheelEvent.wheelDeltaY
    val which: Int = nativeWheelEvent.which
    val x: Double = nativeWheelEvent.x
    val y: Double = nativeWheelEvent.y
}

class MouseEvent(val nativeMouseEvent: NativeMouseEvent): Event(nativeMouseEvent) {
    val altKey: Boolean by nativeMouseEvent::altKey
    val button: Int by nativeMouseEvent::button
    val buttons: Int by nativeMouseEvent::buttons
    val clientX: Double by nativeMouseEvent::clientX
    val clientY: Double by nativeMouseEvent::clientY
    val ctrlKey: Boolean by nativeMouseEvent::ctrlKey
    val detail: Double by nativeMouseEvent::detail
    val fromElement: dynamic by nativeMouseEvent::fromElement
    val layerX: Double by nativeMouseEvent::layerX
    val layerY: Double by nativeMouseEvent::layerY
    val metaKey: Boolean by nativeMouseEvent::metaKey
    val movementX: Double by nativeMouseEvent::movementX
    val movementY: Double by nativeMouseEvent::movementY
    val offsetX: Double by nativeMouseEvent::offsetX
    val offsetY: Double by nativeMouseEvent::offsetY
    val pageX: Double by nativeMouseEvent::pageX
    val pageY: Double by nativeMouseEvent::pageY
    val relatedTarget: dynamic by nativeMouseEvent::relatedTarget
    val screenX: Double by nativeMouseEvent::screenX
    val screenY: Double by nativeMouseEvent::screenY
    val shiftKey: Boolean by nativeMouseEvent::shiftKey
    val which: Int by nativeMouseEvent::which
    val x: Double by nativeMouseEvent::x
    val y: Double by nativeMouseEvent::y
}

class PointerEvent(val nativePointerEvent: NativePointerEvent): Event(nativePointerEvent) {
    val altKey: Boolean by nativePointerEvent::altKey
    val altitudeAngle: Double by nativePointerEvent::altitudeAngle
    val azimuthAngle: Double by nativePointerEvent::azimuthAngle
    val button: Int by nativePointerEvent::button
    val buttons: Int by nativePointerEvent::buttons
    val clientX: Double by nativePointerEvent::clientX
    val clientY: Double by nativePointerEvent::clientY
    val ctrlKey: Boolean by nativePointerEvent::ctrlKey
    val detail: Double by nativePointerEvent::detail
    val fromElement: dynamic by nativePointerEvent::fromElement
    val height: Double by nativePointerEvent::height
    val isPrimary: Boolean by nativePointerEvent::isPrimary
    val layerX: Double by nativePointerEvent::layerX
    val layerY: Double by nativePointerEvent::layerY
    val metaKey: Boolean by nativePointerEvent::metaKey
    val movementX: Double by nativePointerEvent::movementX
    val movementY: Double by nativePointerEvent::movementY
    val offsetX: Double by nativePointerEvent::offsetX
    val offsetY: Double by nativePointerEvent::offsetY
    val pageX: Double by nativePointerEvent::pageX
    val pageY: Double by nativePointerEvent::pageY
    val pointerId: Int by nativePointerEvent::pointerId
    val pointerType: String by nativePointerEvent::pointerType
    val pressure: Double by nativePointerEvent::pressure
    val relatedTarget: dynamic by nativePointerEvent::relatedTarget
    val screenX: Double by nativePointerEvent::screenX
    val screenY: Double by nativePointerEvent::screenY
    val shiftKey: Boolean by nativePointerEvent::shiftKey
    val tangentialPressure: Double by nativePointerEvent::tangentialPressure
    val tiltX: Double by nativePointerEvent::tiltX
    val tiltY: Double by nativePointerEvent::tiltY
    val twist: Double by nativePointerEvent::twist
    val which: Number by nativePointerEvent::which
    val width: Double by nativePointerEvent::width
    val x: Double by nativePointerEvent::x
    val y: Double by nativePointerEvent::y
}

class DataTransfer(val nativeDataTransfer: NativeDataTransfer) {
    val dropEffect: String by nativeDataTransfer::dropEffect
    val effectAllowed: String by nativeDataTransfer::effectAllowed
    val files: dynamic by nativeDataTransfer::files
    val items: dynamic by nativeDataTransfer::items
}


class DragEvent(val nativeDragEvent: NativeDragEvent): Event(nativeDragEvent) {
    val altKey: Boolean by nativeDragEvent::altKey
    val button: Int by nativeDragEvent::button
    val buttons: Int by nativeDragEvent::buttons
    val clientX: Double by nativeDragEvent::clientX
    val clientY: Double by nativeDragEvent::clientY
    val ctrlKey: Boolean by nativeDragEvent::ctrlKey
    val dataTransfer: NativeDataTransfer by nativeDragEvent::dataTransfer
    val detail: Double by nativeDragEvent::detail
    val fromElement: dynamic by nativeDragEvent::fromElement
    val layerX: Double by nativeDragEvent::layerX
    val layerY: Double by nativeDragEvent::layerY
    val metaKey: Boolean by nativeDragEvent::metaKey
    val movementX: Double by nativeDragEvent::movementX
    val movementY: Double by nativeDragEvent::movementY
    val offsetX: Double by nativeDragEvent::offsetX
    val offsetY: Double by nativeDragEvent::offsetY
    val pageX: Double by nativeDragEvent::pageX
    val pageY: Double by nativeDragEvent::pageY
    val relatedTarget: dynamic by nativeDragEvent::relatedTarget
    val screenX: Double by nativeDragEvent::screenX
    val screenY: Double by nativeDragEvent::screenY
    val shiftKey: Boolean by nativeDragEvent::shiftKey
    val which: Int by nativeDragEvent::which
    val x: Double by nativeDragEvent::x
    val y: Double by nativeDragEvent::y
}

class EventAction<T>(val callback: (T)->Unit) {
    var remove: (()->Boolean)? = null
}