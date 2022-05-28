package scripts

import p5.Sketch

fun slide() = Sketch {

    setup {

        var sliderValue by createSlider(0, 100, 0)

        draw {
            sliderValue = (sliderValue+1)%100
        }

    }
}