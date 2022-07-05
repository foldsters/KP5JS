package scripts

import p5.Sketch

fun slide() = Sketch {

    Setup {

        var sliderValue by createSlider(0, 100, 0)

        Draw {
            sliderValue = (sliderValue+1)%100
        }

    }
}