package projects.correlation_tiler

import p5.Sketch
import p5.core.Image
import p5.core.ShaderPass
import p5.ksl.float
import p5.ksl.vec2
import kotlin.math.PI

fun CorrelationTiler() = Sketch {

    lateinit var sourceImage1: Image
    lateinit var sourceImage2: Image

    Preload {
        sourceImage1 = loadImage("stock/flower.png")
        sourceImage2 = loadImage("stock/flower2.jpg")
    }

    Setup {

        val canvas = createCanvas(1080, 1080)

        val r1 by url { 0.01 }
        val r2 by url { 0.01 }

        val correlationShader = ShaderPass(0) {

            Fragment {
                val img1 by UniformImage { sourceImage1 }
                val img2 by UniformImage { sourceImage2 }
                val res1  by Uniform<vec2> { arrayOf(width, height) }
                val res2  by Uniform<vec2> { arrayOf(width, height) }
                val radii by Uniform<vec2> { arrayOf(r1, r2) }

                val random by buildFunction { x1: float, x2: float, x3: float, x4: float ->
                    fract((x1 + 0.5)*57.2493 + (x2 + 0.883)*45.3243 + (x3 + 9.342)*39.43234 + (x4 + 0.2342)*18.2342)
                }

                val radiusSearch by buildFunction { uv: vec2 ->

                    var color by vec3(0, 0, 1)
                    val worstDist by distance(vec3(0), vec3(1))
                    var bestDist by worstDist

                    val alpha by 2.0*PI*uv.x
                    val beta  by 2.0*PI*uv.y

                    val ap by radii.x*vec2(cos(alpha), sin(alpha))
                    val bp by radii.y*vec2(cos(beta),  sin(beta))

                    val radSteps by float(5)
                    val radSize  by float(0.1)
                    val lineSteps by float(10)

                    val da by normalize(ap)*radSize/radSteps
                    val db by normalize(bp)*radSize/radSteps

                    val ca1 by 0.5 + ap + normalize(vec2(ap.y, -ap.x))*sqrt(1.0-dot(ap, ap))
                    val ca2 by 0.5 + ap - normalize(vec2(ap.y, -ap.x))*sqrt(1.0-dot(ap, ap))
                    val cb1 by 0.5 + bp + normalize(vec2(bp.y, -bp.x))*sqrt(1.0-dot(bp, bp))
                    val cb2 by 0.5 + bp - normalize(vec2(bp.y, -bp.x))*sqrt(1.0-dot(bp, bp))

                    FOR(radSteps) { rad ->
                        FOR(radSteps) { ra ->
                            val rb by rad - ra
                            FOR(lineSteps) { la ->
                                FOR(lineSteps) { lb ->
                                    val ca by mix(ca1, ca2, la/lineSteps) + ra*da
                                    val cb by mix(cb1, cb2, lb/lineSteps) + rb*db
                                    val cola by texture(img1, ca)
                                    val colb by texture(img2, cb)
                                    IF((cola.a GT 0.5) AND (colb.a GT 0.5)) {
                                        val newDist = distance(cola.rgb, colb.rgb)
                                        IF(newDist LT bestDist) {
                                            bestDist = newDist
                                            color = mix(cola.rgb, colb.rgb, float(0.5))
                                        }
                                        IF(newDist LT 0.05) {
                                            Return(color)
                                        }
                                    }
                                }
                            }
                        }
                    }


                    color
                }

                Main {
                    val uv by it/res2
                    uv.y = 1.0 - uv.y

                    val result = radiusSearch(uv)

                    vec4(result, 1)
                }
            }
        }

        image(correlationShader.redraw(1080, 1080), 0, 0, 1080, 1080)

    }

}