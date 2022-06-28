package p5.kshader

import p5.util.ifTrue
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class KShader {

    var debug = false
    var uID = 0

    fun debugPrint(lazyMessage: ()->String) {
        debug.ifTrue {
            println(lazyMessage())
        }
    }

    val queuedAssignments: MutableMap<Int, Assignable<*>> = mutableMapOf()

    // Base class for all shader types
    abstract inner class ShaderNode(vararg cs: ShaderNode) {

        open var children: List<ShaderNode> = cs.toList()
        abstract fun render(): String
        abstract val nativeTypeName: String // For Variable Declaration
        abstract fun copy(): ShaderNode // For copying branches of the tree

        // Logging
        open val descriptor: String = "ShaderNode"
        val id = uID++
    }

    // Allows us to check whether a variable has been declared
    val declaredVariableNames = mutableSetOf<String>()
    fun needsDeclaration(varName: String): Boolean = declaredVariableNames.add(varName)

    // Line by line nodes to be rendered
    var instructionIndent = 0
    val instructions = mutableListOf<ShaderNode>()


    // fun pushAssignment
    // fun pushWhile
    // fun pushFor

    // Logging
    var logIndent = 0
    fun ShaderNode.log() {
        println(" ".repeat(logIndent) + descriptor + " ID:$id")
        logIndent += 1
        children.map { it.log() }
        logIndent -= 1
    }
    fun logInstructions() {
        debugPrint { "logInstructions" }
        instructions.forEach {
            it.log()
        }
    }

    // List Helper Functions
    fun List<ShaderNode>.render(): String {
        debugPrint { "List Render" }
        isEmpty().ifTrue {
            return ""
        }
        return joinToString(separator = ", ") { it.render() }
    }

    fun List<ShaderNode>.copy(): List<ShaderNode> {
        debugPrint { "List Copy" }
        return map { it.copy() }
    }

    // Holds Literal Values (Int, Bool, Double)
    inner class LiteralNode<T>(val literalValue: T): ShaderNode() {
        override fun render(): String {
            debugPrint { "Literal Node Render" }
            return when(literalValue) {
                is Double -> {
                    val result = literalValue.toString()
                    if ("." !in result) {
                        "$literalValue."
                    } else result
                }
                is String -> literalValue
                is Boolean -> literalValue.toString()
                else -> "???"
            }
        }
        override val descriptor = "literalNode($literalValue)"
        override fun copy(): ShaderNode {
            return LiteralNode(literalValue)
        }

        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use LiteralNode as Shader Type")
    }

    inner class AssignmentNode(val name: String, val declare: Boolean, val expression: ShaderNode): ShaderNode(expression) {
        override fun render(): String {
            debugPrint { "Assignment Node Render" }
            val declaration = if (declare) expression.nativeTypeName + " " else ""
            return "$declaration$name = ${expression.render()};"
        }
        override val descriptor = "AssignmentNode($name)"
        override fun copy(): ShaderNode {
            return AssignmentNode(name, declare, expression.copy())
        }
        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use AssignmentNode as Shader Type")
    }

    inner class VariableNode(val name: String): ShaderNode() {
        override fun render() = name
        override val descriptor = "VariableNode($name)"
        override fun copy(): ShaderNode {
            return VariableNode(name)
        }
        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use VariableNode as Shader Type")
    }

    abstract inner class Assignable<T: ShaderNode>(vararg cs: ShaderNode): ShaderNode(*cs) {

        private var needsAssignment = true
        var name: String? = null

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val varName = property.name
            name = varName
            needsAssignment.ifTrue {
                debugPrint { "Get: property name: $varName, id: $id, render: ${render()}" }
                instructions.add(AssignmentNode(varName, needsDeclaration(varName), this.copy()))
                needsAssignment = false
            }
            children = listOf(VariableNode(varName))
            return this as T
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            debugPrint { "Set: property name: ${property.name}, id: $id, render: ${render()}" }
            name = property.name
            children = value.children.copy()
            needsAssignment = true
        }

        operator fun rangeTo(other: T): BoolNode {
            debugPrint { "equals: ${this.id}, ${other.id}" }
            return BoolNode(OperatorNode("==", this.copy(), other.copy()))
        }
    }

    abstract inner class GenBType<T: GenBType<T>>(vararg cs: ShaderNode): Assignable<T>(*cs) {
        abstract fun new(vararg cs: ShaderNode): GenBType<T>

//        operator fun plus(other: T): T {
//            debugPrint { "plus: ${this.id}, ${other.id}" }
//            return new(OperatorNode("+", this.copy(), other.copy())) as T
//        }
        // Negation (!)
        // AND (&&)
        // XOR (^^)
        // OR (||)

    }

    fun bool(b: Boolean): BoolNode {
        return BoolNode(LiteralNode(b))
    }

    inner class BoolNode(vararg cs: ShaderNode): GenBType<BoolNode>(*cs) {
        operator fun not(): BoolNode {
            debugPrint { "not: ${this.id}"}
            return BoolNode(FunctionNode("!", this.copy()))
        }

        override fun new(vararg cs: ShaderNode): GenBType<BoolNode> {
            return BoolNode(*cs)
        }

        override val nativeTypeName = "bool"

        override fun copy(): ShaderNode {
            val result = BoolNode(*children.toTypedArray())
            result.name = name
            return result
        }

        override fun render(): String {
            debugPrint { "bool Render, $id, $name" }
            require(children.isNotEmpty()) {"Error Rendering BoolNode: Not Enough Children; $name; ${log()}"}
            return children[0].render()
        }
    }
//    inner class bvec2: genBType()
//    inner class bvec3: genBType()
//    inner class bvec4: genBType()


    interface genIType
//    inner class int: genIType()
//    inner class ivec2: genIType()
//    inner class ivec3: genIType()
//    inner class ivec4: genIType()

    abstract inner class GenFType<T: GenFType<T>>(vararg cs: ShaderNode): Assignable<T>(*cs) {
        abstract fun new(vararg cs: ShaderNode): GenFType<T>

        operator fun plus(other: T): T {
            debugPrint { "plus: ${this.id}, ${other.id}" }
            return new(OperatorNode("+", this.copy(), other.copy())) as T
        }
    }


    // External Float Constructors
    fun float(x: Double) = FloatNode().apply { children = listOf(LiteralNode(x)) }
    fun float(x: Float) = float(x.toDouble())
    fun float(x: Int) = float(x.toDouble())
    fun float(x: FloatNode) = x.copy()
    fun float(x: VariableNode) = FloatNode().apply { children = listOf(x) }

    inner class FloatNode(vararg cs: ShaderNode): GenFType<FloatNode>(*cs) {

        override val descriptor = "float"
        override val nativeTypeName = "float"

        override fun copy(): ShaderNode {
            debugPrint { "Float Copy" }
            val childrenCopy = children.copy().toTypedArray()
            val result = FloatNode(*childrenCopy)
            result.name = name
            debug.ifTrue {
                println("breakDown")
                this.log()
                result.log()
            }
            return result
        }

        override fun new(vararg cs: ShaderNode) = FloatNode(*cs)

        override fun render(): String {
            debugPrint { "float Render, $id" }
            require(children.isNotEmpty()) {"Error Rendering Float: Not Enough Children"}
            return children[0].render()
        }

        operator fun <T: VecType<*>> plus(other: T): T {
            debugPrint { "plus: ${this.id}, ${other.id}" }
            return new(OperatorNode("+", this.copy(), other.copy())) as T
        }

        operator fun minus(other: FloatNode): FloatNode {
            debugPrint { "minus: ${this.id}, ${other.id}" }
            val oNode = OperatorNode("-")
            oNode.children = listOf(this.copy(), other.copy())
            val result = FloatNode()
            result.children = listOf(oNode)
            return result
        }

    }

    abstract inner class VecType<T: VecType<T>>(vararg cs: ShaderNode): GenFType<T>(*cs) {

        var x: FloatNode
            get() { return FloatNode(ComponentNode("x", this.copy())) }
            set(value) {
                val varName = name ?: throw IllegalStateException("Unable to Determine Name of Vector")
                instructions.add(AssignmentNode("${varName}.x", false, value.copy()))
            }
        var y: FloatNode
            get() { return FloatNode(ComponentNode("y", this.copy())) }
            set(value) {
                val varName = name ?: throw IllegalStateException("Unable to Determine Name of Vector")
                instructions.add(AssignmentNode("${varName}.y", false, value.copy()))
            }
//        val xx: Vec2Node
//            get() { return Vec2Node(ComponentNode("xx", this.copy())) }
//        val yy: Vec2Node
//            get() { return Vec2Node(ComponentNode("yy", this.copy())) }
//        var xy: Vec2Node
//            get() { return Vec2Node(ComponentNode("xy", this.copy())) }
//            set(value) { components[1] = value }

    }

    fun vec2(x: FloatNode, y: FloatNode): Vec2Node {
        return Vec2Node(x, y)
    }

    inner class Vec2Node(vararg cs: ShaderNode): VecType<Vec2Node>(*cs) {

        override fun new(vararg cs: ShaderNode): GenFType<Vec2Node> {
            return Vec2Node(*cs)
        }
        override val nativeTypeName = "vec2"
        override fun copy(): ShaderNode {
            val result = Vec2Node(*children.copy().toTypedArray())
            result.name = name
            return result
        }

        override fun render(): String {
            debugPrint { "vec2 Render, $id" }
            if (children.size == 1 && children[0] is VariableNode) {
                return children[0].render()
            }
            require(children.size == 2) {"Error Rendering Vec2: Wrong Number of Children: ${children.render()}"}
            return "vec2(${children.render()})"
        }
    }

    inner class Vec3Node(vararg cs: ShaderNode): VecType<Vec3Node>(*cs) {
        override fun new(vararg cs: ShaderNode): GenFType<Vec3Node> {
            return Vec3Node(*cs)
        }

        override fun render(): String {
            TODO("Not yet implemented")
        }

        override val nativeTypeName = "vec3"
        override fun copy(): ShaderNode {
            TODO("Vec3 Copy Not yet implemented")
        }
    }

    fun vec4(x: FloatNode, y: FloatNode, z: FloatNode, w: FloatNode) = Vec4Node().apply { children = listOf(x, y, z, w) }
    fun vec4(xy: Vec2Node, z: FloatNode, w: FloatNode) {}
    fun vec4(x: FloatNode, yz: Vec2Node, w: FloatNode) {}
    fun vec4(x: FloatNode, y: FloatNode, zw: Vec2Node) {}
    fun vec4(xyz: Vec3Node, w: FloatNode) {}
    fun vec4(x: FloatNode, yzw: Vec3Node) {}
    fun vec4(xyzw: Vec3Node) = xyzw

    inner class Vec4Node(vararg cs: ShaderNode): VecType<Vec4Node>(*cs) {
        override fun new(vararg cs: ShaderNode): GenFType<Vec4Node> {
            return Vec4Node(*cs)
        }

        override fun render(): String {
            TODO("Not yet implemented")
        }

        override val nativeTypeName = "vec4"

        constructor(x: FloatNode, y: FloatNode, z: FloatNode, w: FloatNode): this() {
            children = listOf(x, y, z, w)
        }

        override fun copy(): ShaderNode {
            TODO("Vec4 Copy Not yet implemented")
        }
}

    inner class mat2
    inner class mat3
    inner class mat4

    inner class sampler2D
    inner class samplerCube

    inner class FunctionNode(val name: String, vararg cs: ShaderNode): ShaderNode(*cs) {
        override fun render(): String {
            debugPrint { "Function Node Render: ${children.render()}" }
            return "$name(${children.render()})"
        }
        override val descriptor = name
        override fun copy(): ShaderNode {
            val result = FunctionNode(name)
            result.children = children.copy()
            return result
        }
        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use FunctionNode as Shader Type")
    }

    inner class OperatorNode(val name: String, vararg cs: ShaderNode): ShaderNode(*cs) {
        override fun render(): String {
            debugPrint { "Operator Node Render: $name" }
            require(children.size > 1) {"Error in rendering Operator Node: Not Enough Children ($children)"}
            val child0 = children[0]
            val child1 = children[1]
            val c0 = if(child0.children.isNotEmpty() && child0.children[0] is OperatorNode) {
                "(${child0.render()})"
            } else {
                child0.render()
            }
            val c1 = if(child1.children.isNotEmpty() && child1.children[0] is OperatorNode) {
                "(${child1.render()})"
            } else {
                child1.render()
            }
            return "$c0$name$c1"
        }
        override val descriptor = name
        override fun copy(): ShaderNode {
            val result =  OperatorNode(name)
            result.children = children.copy()
            return result
        }
        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use OperatorNode as Shader Type")
    }

    inner class ComponentNode(val name: String, vararg cs: ShaderNode): ShaderNode(*cs) {
        override fun render(): String {
            debugPrint { "Component Node Render" }
            require(children.isNotEmpty()) {"Error in rendering Component Node: Not Enough Children ($children)"}
            if (children[0] is Assignable<*>) {
                return "${children[0].render()}.$name"
            }
            return "(${children[0].render()}).$name"
        }
        override val descriptor = name
        override fun copy(): ShaderNode {
            return ComponentNode(name, *(children.copy().toTypedArray()))
        }
        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use ComponentNode as Shader Type")
    }

    fun <T: GenFType<*>> radians(degrees: T): T {
        val fNode = FunctionNode("radians")
        fNode.children = listOf(degrees)
        val result = degrees.new() as T
        result.children = listOf(fNode)
        return result
    }

    operator fun FloatNode.times(other: FloatNode) = FloatNode(OperatorNode("*", this.copy(), other.copy()))



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


    fun Main(block: ()->Vec4Node) {
        instructions.add(LiteralNode("void main(){"))
        block()
        instructions.add(LiteralNode("}"))
    }

    fun While(cond: BoolNode, block: ()->Unit) {
        instructions.add(LiteralNode("while(${cond.render()}){"))
        block()
        instructions.add(LiteralNode("}"))
    }

    fun fragment(block: KShader.()->Unit): String {
        block()
        return instructions.joinToString("\n") { it.render() }
    }

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