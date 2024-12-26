package projects.examples

import p5.core.P5
import p5.Sketch
import kotlin.math.PI
import kotlin.math.min

fun penrose() = Sketch {

    lateinit var penrose: Penrose

    Setup {
        createCanvas(700, 700)
        noFill()
        penrose = Penrose(this)
        penrose.simulate(5)
        frameRate(10)
        createLoop(duration = 10, gif = true, gifRender = true, gifQuality = 1, gifWorkers = 5)
    }

    Draw {
        background(0)
        penrose.render()
    }

}

class Penrose(val p5: P5) {

    var steps = 0
    val axiom = "[X]++[X]++[X]++[X]++[X]"
    val ruleW = "YF++ZF----XF[-YF----WF]++"
    val ruleX = "+YF--ZF[---WF--XF]+"
    val ruleY = "-WF++XF[+++YF++ZF]-"
    val ruleZ = "--YF++++WF[+ZF++++XF]--XF"

    val startLength = 500.0
    val theta = PI/5.0

    var production = axiom
    var drawLength = startLength
    var generations = 0

    fun simulate(gen: Int) {
        while (generations < gen) {
            iterate()
        }
    }

    fun iterate() {
        drawLength *= 0.5
        generations++
        production = buildString {
            production.forEach {step ->
                when(step) {
                    'W' -> append(ruleW)
                    'X' -> append(ruleX)
                    'Y' -> append(ruleY)
                    'Z' -> append(ruleZ)
                    else -> if (step != 'F') {
                        append(step)
                    }
                }
            }
        }
    }

    fun render() {
        with(p5) {
            translate(width/2, height/2)
            steps = min(production.length, steps+600)

            production.take(steps).forEach { step ->
                when(step) {
                    '+' -> rotate(theta)
                    '-' -> rotate(-theta)
                    '[' -> push()
                    ']' -> pop()
                    'F' -> {
                        stroke(255, 60)
                        line(0, 0, 0, -drawLength)
                        translate(0, -drawLength)
                    }
                }
            }
        }
    }
}