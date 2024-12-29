package projects.spectro

import p5.Sketch

@OptIn(ExperimentalJsExport::class)
@JsExport
fun SpectGrad() = Sketch {

    Setup {
        for(i in 400..800 step 5) {
            val (red, green, blue) = wavToTri(i)
            println("$i , $red , $green , $blue, ")
        }

    }

}