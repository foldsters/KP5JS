package p5.native


open external class NativeEvent {
    val isTrusted: Boolean
    val bubbles: Boolean
    val cancelBubble: Boolean
    val cancelable: Boolean
    val composed: Boolean
    val currentTarget: dynamic
    val defaultPrevented: Boolean
    val eventPhase: Int
    val returnValue: Boolean
    val srcElement: NativeElement?
    val target: NativeElement?
    val timeStamp: Double
    val type: String
}


external class NativeTouchEvent {

}


external class NativeKeyboardEvent: NativeEvent {
    val altKey: Boolean
    val charCode: Int
    val code: String
    val ctrlKey: Boolean
    val detail: Double
    val isComposing: Boolean
    val key: String
    val keyCode: Int
    val location: Double
    val metaKey: Boolean
    val repeat: Boolean
    val shiftKey: Boolean
    val which: Int
}


external class NativeWheelEvent: NativeEvent {
    val altKey: Boolean
    val button: Int
    val buttons: Int
    val clientX: Double
    val clientY: Double
    val ctrlKey: Boolean
    val deltaMode: Int
    val deltaX: Double
    val deltaY: Double
    val deltaZ: Double
    val detail: Double
    val fromElement: dynamic
    val layerX: Double
    val layerY: Double
    val metaKey: Boolean
    val movementX: Double
    val movementY: Double
    val pageX: Double
    val pageY: Double
    val relatedTarget: dynamic
    val screenX: Double
    val screenY: Double
    val shiftKey: Boolean
    val wheelDelta: Double
    val wheelDeltaX: Double
    val wheelDeltaY: Double
    val which: Int
    val x: Double
    val y: Double
}


external class NativeMouseEvent: NativeEvent {
    val altKey: Boolean
    val button: Int
    val buttons: Int
    val clientX: Double
    val clientY: Double
    val ctrlKey: Boolean
    val detail: Double
    val fromElement: dynamic
    val layerX: Double
    val layerY: Double
    val metaKey: Boolean
    val movementX: Double
    val movementY: Double
    val offsetX: Double
    val offsetY: Double
    val pageX: Double
    val pageY: Double
    val relatedTarget: dynamic
    val screenX: Double
    val screenY: Double
    val shiftKey: Boolean
    val which: Int
    val x: Double
    val y: Double
}


external class NativePointerEvent: NativeEvent {
    val altKey: Boolean
    val altitudeAngle: Double
    val azimuthAngle: Double
    val button: Int
    val buttons: Int
    val clientX: Double
    val clientY: Double
    val ctrlKey: Boolean
    val detail: Double
    val fromElement: dynamic
    val height: Double
    val isPrimary: Boolean
    val layerX: Double
    val layerY: Double
    val metaKey: Boolean
    val movementX: Double
    val movementY: Double
    val offsetX: Double
    val offsetY: Double
    val pageX: Double
    val pageY: Double
    val pointerId: Int
    val pointerType: String
    val pressure: Double
    val relatedTarget: dynamic
    val screenX: Double
    val screenY: Double
    val shiftKey: Boolean
    val tangentialPressure: Double
    val tiltX: Double
    val tiltY: Double
    val twist: Double
    val which: Number
    val width: Double
    val x: Double
    val y: Double
}


external class NativeDataTransfer {
    val dropEffect: String
    val effectAllowed: String
    val files: dynamic
    val items: dynamic
}


external class NativeDragEvent: NativeEvent {
    val altKey: Boolean
    val button: Int
    val buttons: Int
    val clientX: Double
    val clientY: Double
    val ctrlKey: Boolean
    val dataTransfer: NativeDataTransfer
    val detail: Double
    val fromElement: dynamic
    val layerX: Double
    val layerY: Double
    val metaKey: Boolean
    val movementX: Double
    val movementY: Double
    val offsetX: Double
    val offsetY: Double
    val pageX: Double
    val pageY: Double
    val relatedTarget: dynamic
    val screenX: Double
    val screenY: Double
    val shiftKey: Boolean
    val which: Int
    val x: Double
    val y: Double
}
