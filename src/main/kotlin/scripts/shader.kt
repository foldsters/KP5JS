package scripts

import p5.Sketch
import p5.kshader.KShader
import p5.kshader.KShader.*

fun shader() = Sketch {
    val shader = KShader()
    val fragment = shader.fragment {

        data class complex(val real: FloatNode, val imag: FloatNode) {
            operator fun plus(other: complex): complex {
                return complex(real+other.real, imag+other.imag)
            }
            operator fun times(other: complex): complex {
                return complex(real*other.real - imag*other.imag, real*other.imag + imag*other.real)
            }
        }

        Main {
            var q by float(12.1) + radians(float(13.0))
            var r by q + q
            val s = float(17)
            val t by float(22)

            val c = complex(q, r) * complex(s*float(2), t)
            q = c.real
            r = c.imag

            val v1 by vec2(q, r)
            val v2 = vec2(r, q)

            v1.y *= q
            v1.x = t*v2.y
            val b = bool(true)
            val n by b .. !b

            While(n) {
                q = v2.x
                q
            }

            Vec4Node()
        }


    }
    println(fragment)

}