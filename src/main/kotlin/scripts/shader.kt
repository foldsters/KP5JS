package scripts

import p5.Sketch
import p5.kglsl.KGLSL
import p5.kglsl.KGLSL.*
import p5.kglsl.*

fun shader() = Sketch {
    val shader = KGLSL()
    val fragment = shader.fragment {

        data class complex(val real: FloatNode, val imag: FloatNode) {
            operator fun plus(other: complex): complex {
                return complex(real+other.real, imag+other.imag)
            }
            operator fun times(other: complex): complex {
                return complex(real*other.real - imag*other.imag, real*other.imag + imag*other.real)
            }
        }

        +"#define two 2"

        val z by Uniform<vec2>()
        z

        Main {
            var q by float(12.1) + radians(float(13.0))
            var r by q + q + z.x
            val s = float(17)
            val t by float(22)

            var a by float(33)
            a = float(44)

            val c = complex(q, r) * complex(s*float(2), t)
            q = c.real
            r = c.imag
            var v1 by vec2(q, r)
            val v2 = vec2(r, q)
            val v3 by v1
            q += q + v3.x
            v1 = (v3 + v3)
            v1.y *= q
            v1.x = t*v2.y
            val b = bool(true)
            val n by b EQ !(float(5) LE float(6))

            While(n) {
                q = v2.x
                Break
            }

            vec4(q, r, s, t)
        }


    }
    println(fragment)

}