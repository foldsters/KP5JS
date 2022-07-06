package projects

import p5.Sketch
import spectro.wavToTri

fun spectGrad() = Sketch {

    Setup {



        for(i in 400..800 step 5) {
            val (red, green, blue) = wavToTri(i)
            println("$i , $red , $green , $blue, ")
        }

    }

}