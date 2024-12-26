
import p5.Sketch
import projects.cards.mysticTomato
import projects.hopper.hopper
import kotlinx.browser.window
import p5.util.isDarkMode
import p5.util.println
import projects.cards.perlinLightning
import projects.circlizer.circlizer
import projects.correlation_tiler.CorrelationTiler
import projects.hopper.hopperClouds
import projects.hopper.hopperClouds2
import projects.huegene.huegene
import projects.huegene.huegeneFlow
import projects.testing.CardList
import projects.layout.layoutExample1
import projects.moire.moire
import projects.ordinary_generating_functions.oge
import projects.raytrace.bitfield
import projects.singularity.singularity
import projects.som.paletteGenerator
import projects.testing.ElementEvents
import website.front

fun main() {
    window.onload = {
        val pageName = js("page") as String
        console.log(pageName)
        CorrelationTiler()
    }
}

//val sketchMap = mutableMapOf<String, ()->Sketch>(
//    "Choose-Sketch" to ::Select,
//    "Mystic-Tomato" to ::mysticTomato,
//    "Perlin-Lightning" to ::perlinLightning,
//    "Hopper" to ::hopper,
//    "Hopper-Cloud" to ::hopperClouds,
//    "Hopper-Cloud 2" to ::hopperClouds2,
//    "Huegene" to ::huegene,
//    "Huegene-Flow" to ::huegeneFlow,
//    "Layout-Example" to ::layoutExample1,
//    "Bitfield" to ::bitfield,
//    "Palette-Generator" to ::paletteGenerator,
//    "Element-Events" to ::ElementEvents,
//    "Twitter-Style" to ::CardList,
//    "Ordinary-Generating-Function" to ::oge,
//    "Moire" to ::moire,
//    "Singularity" to ::singularity,
//    "Circlizer" to ::circlizer
//)
//
//fun Select(): Sketch = Sketch {
//
//    Setup {
//        noCanvas()
//        if(isDarkMode()) { getBody().style("background-color", "#46484a") }
//
//        val sketch by url { "" }
//
//        println("sketch", sketch)
//
//        if(sketch in sketchMap) {
//            sketchMap[sketch]?.invoke()
//            return@Setup
//        }
//
//        lateinit var clearPage:  ()->Unit
//
//        val selector = createSelect().apply {
//            sketchMap.keys.forEach { option(it.replace("-", " ")) }
//        }
//
//        Layout {
//            Column {
//                add(selector)
//                clearPage = ::delete
//            }
//        }
//
//        selector.changed {
//            clearPage()
//            sketchMap[selector.value().replace(" ", "-")]?.invoke()
//        }
//    }
//}
//

