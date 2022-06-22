package p5.kshader

import p5.util.ifTrue
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class KShader {

    abstract class ShaderNode {
        var children =  mutableListOf<ShaderNode>()
        open fun render(): String = throw NotImplementedError()
        var typeName: String = ""
    }

    val declaredVariables = mutableSetOf<String>()

    val lines = mutableListOf<ShaderNode>()

    abstract inner class genBType: ShaderNode()

    fun List<ShaderNode>.render(): String {
        isEmpty().ifTrue {
            return ""
        }
        return joinToString(separator = ", ") { it.render() }
    }

    class LiteralNode<T>(val literalValue: T): ShaderNode() {
        override fun render(): String = when(literalValue) {
                is Double -> {
                    val result = literalValue.toString()
                    if ("." !in result) {
                        "$literalValue."
                    } else result
                }
                else -> "???"
        }
    }

    class AssignmentNode(val name: String, val declare: Boolean, val expression: ShaderNode): ShaderNode() {
        override fun render(): String {
            return "$name = ${expression.render()}"
        }
    }

    class VariableNode(val name: String): ShaderNode() {
        override fun render() = name
    }

    inner class bool: genBType()
    inner class bvec2: genBType()
    inner class bvec3: genBType()
    inner class bvec4: genBType()


    abstract inner class genIType: ShaderNode()
    inner class int: genIType()
    inner class ivec2: genIType()
    inner class ivec3: genIType()
    inner class ivec4: genIType()

    abstract class genFType: ShaderNode() {
        abstract fun copyType(): genFType
    }

    inner class float(): genFType() {

        var needsAssignment = true

        constructor(initValue: Double): this() {
            children.add(LiteralNode(initValue))
        }

        constructor(initValue: Float): this() {
            children.add(LiteralNode(initValue.toDouble()))
        }

        constructor(initValue: Int): this() {
            children.add(LiteralNode(initValue.toDouble()))
        }

        constructor(initValue: float): this() {
            children.add(initValue)
        }

        constructor(initValue: VariableNode): this() {
            children.add(initValue)
        }

        override fun copyType(): genFType {
            return float()
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): float {
            val varName = property.name
            needsAssignment.ifTrue {
                lines.add(AssignmentNode(varName, varName !in declaredVariables, this))
                needsAssignment = false
                declaredVariables.add(varName)
            }
            val newFloat = float(VariableNode(varName))
            return newFloat
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
            val s = "$value has been assigned to '${property.name}' in $thisRef."
            children = mutableListOf(LiteralNode(value.toDouble()))
            needsAssignment = true
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: float) {
            val s = "$value has been assigned to '${property.name}' in $thisRef."
            children = value.children
            needsAssignment = true
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
            val s = "$value has been assigned to '${property.name}' in $thisRef."
            children = mutableListOf(LiteralNode(value))
            needsAssignment = true
        }

        override fun render(): String {
            val child = children[0]
            return child.render()
        }
    }

    class vec2: genFType() {
        override fun copyType(): genFType {
            return vec2()
        }
    }
    class vec3: genFType() {
        override fun copyType(): genFType {
            return vec3()
        }
    }
    class vec4: genFType() {
        override fun copyType(): genFType {
            return vec4()
        }
    }

    inner class mat2
    inner class mat3
    inner class mat4

    inner class sampler2D
    inner class samplerCube
    inner class shaderVoid

    inner class FunctionNode(val name: String): ShaderNode() {
        override fun render(): String {
            return "$name(${children.render()})"
        }
    }

    inner class OperatorNode(val name: String): ShaderNode() {
        override fun render(): String {
            return "${children[0].render()}$name${children[1].render()}"
        }
    }

    fun <T: genFType> radians(degrees: T): T {
        val fnode = FunctionNode("radians")
        fnode.children.add(degrees)
        val result = degrees.copyType() as T
        result.children.add(fnode)
        return result
    }

    operator fun float.plus(other: float): float {
        val onode = OperatorNode("+")
        onode.children.add(this)
        onode.children.add(other)
        return float().apply { children.add(onode) }
    }

    // Trig Functions
    /*
        class radians(genFType: degrees): genFType
        fun degrees(genFType: radians): genFType
        fun sin(genFType: angle): genFType
        fun cos(genFType: angle): genFType
        fun tan(genFType: angle): genFType
        fun asin(genFType: x): genFType
        fun acos(genFType: x): genFType
        fun atan(genFType y, genFType x): genFType
        fun atan(genFType y_over_x): genFType
        fun sinh(genFType: x): genFType
        fun cosh(genFType: x): genFType
        fun tanh(genFType y, genFType x): genFType
        fun asinh(genFType: x): genFType
        fun acosh(genFType: x): genFType
        fun atanh(genFType y, genFType x): genFType
     */

    // Common Functions
    /*
        abs(genFType x): genFType
        abs(genIType x): genIType
        sign(genFType x): genFType
        sign(genIType x): genIType
        floor(genFType x): genFType
        trunc(genFType x): genFType
        round(genFType x): genFType
        roundEven(genFType x): genFType
        ceil(genFType x): genFType
        fract(genFType x): genFType
        mod(genFType x, float y): genFType
        mod(genFType x, genFType y): genFType
        modf(genFType x, out genFType i): genFType
        min(genFType x, genFType y): genFType
        min(genFType x, float y): genFType
        genIType min(genIType x, genIType y): genIType
        genIType min(genIType x, int y): genIType
        max(genFType x, genFType y): genFType
        max(genFType x, float y): genFType
        max(genIType x, genIType y): genIType
        max(genIType x, int y): genIType
        clamp(genFType x, genFType minVal, genFType maxVal):
        clamp(genFType x, float minVal, float maxVal)
        clamp(genIType x, genIType minVal, genIType maxVal)
        clamp(genIType x, int minVal, int maxVal)
        mix(genFType x, genFType y, genFType a)
        mix(genFType x, genFType y, float a)
        mix(genFType x, genFType y, genBType a)
        mix(genDType x, genDType y, genBType a)
        mix(genIType x, genIType y, genBType a)
        mix(genBType x, genBType y, genBType a)

        genFType step(genFType edge, genFType x)
        genFType step(float edge, genFType x)
        genFType smoothstep(genFType edge0, genFType edge1, genFType x)
        genFType smoothstep(float edge0, float edge1, genFType x)
        genBType isnan(genFType x)
        genBType isnan(genDType x)
        genBType isinf(genFType x)
        genBType isinf(genDType x)
        genIType floatBitsToInt(highp genFType value)
        genFType intBitsToFloat(highp genIType value)
        genFType fma(genFType a, genFType b, genFType c)
        genFType frexp(highp genFType x, out highp genIType exp)
        genFType ldexp(highp genFType x, highp genIType exp)
    */


}





/* KEYWORDS
const uniform buffer shared attribute varying
coherent volatile restrict readonly writeonly
atomic_uint
layout
centroid flat smooth noperspective
patch sample
invariant precise
break continue do for while switch case default
if else
subroutine
in out inout
int void bool true false float double
discard return
vec2 vec3 vec4 ivec2 ivec3 ivec4 bvec2 bvec3 bvec4
uint uvec2 uvec3 uvec4
dvec2 dvec3 dvec4
mat2 mat3 mat4
mat2x2 mat2x3 mat2x4
mat3x2 mat3x3 mat3x4
mat4x2 mat4x3 mat4x4
dmat2 dmat3 dmat4
dmat2x2 dmat2x3 dmat2x4
dmat3x2 dmat3x3 dmat3x4
dmat4x2 dmat4x3 dmat4x4
lowp mediump highp precision
sampler1D sampler1DShadow sampler1DArray sampler1DArrayShadow
isampler1D isampler1DArray usampler1D usampler1DArray
sampler2D sampler2DShadow sampler2DArray sampler2DArrayShadow
isampler2D isampler2DArray usampler2D usampler2DArray
sampler2DRect sampler2DRectShadow isampler2DRect usampler2DRect
sampler2DMS isampler2DMS usampler2DMS
sampler2DMSArray isampler2DMSArray usampler2DMSArray
sampler3D isampler3D usampler3D
samplerCube samplerCubeShadow isamplerCube usamplerCube
samplerCubeArray samplerCubeArrayShadow
isamplerCubeArray usamplerCubeArray
samplerBuffer isamplerBuffer usamplerBuffer
image1D iimage1D uimage1D
image1DArray iimage1DArray uimage1DArray
image2D iimage2D uimage2D
image2DArray iimage2DArray uimage2DArray
image2DRect iimage2DRect uimage2DRect
image2DMS iimage2DMS uimage2DMS
image2DMSArray iimage2DMSArray uimage2DMSArray
image3D iimage3D uimage3D
imageCube iimageCube uimageCube
imageCubeArray iimageCubeArray uimageCubeArray
imageBuffer iimageBuffer uimageBuffer
struct

VULKAN ONLY

texture1D texture1DArray
itexture1D itexture1DArray utexture1D utexture1DArray
texture2D texture2DArray
itexture2D itexture2DArray utexture2D utexture2DArray
texture2DRect itexture2DRect utexture2DRect
texture2DMS itexture2DMS utexture2DMS
texture2DMSArray itexture2DMSArray utexture2DMSArray
texture3D itexture3D utexture3D
textureCube itextureCube utextureCube
textureCubeArray itextureCubeArray utextureCubeArray
textureBuffer itextureBuffer utextureBuffer
sampler samplerShadow
subpassInput isubpassInput usubpassInput
subpassInputMS isubpassInputMS usubpassInputMS

FUTURE USE

common partition active
asm
class union enum typedef template this
resource
goto
inline noinline public static extern external interface
long short half fixed unsigned superp
input output
hvec2 hvec3 hvec4 fvec2 fvec3 fvec4
filter
sizeof cast
namespace using
sampler3DRect
*/