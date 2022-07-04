@file:Suppress("BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER", "unused", "SpellCheckingInspection", "PropertyName",
    "FunctionName"
)


package p5.kglsl

import p5.util.appendAll
import p5.util.ifTrue
import kotlin.math.max
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class KGLSL {

    // Instructions to be Rendered at the End
    var instructionIndent = 0
    val instructions = mutableListOf<Instruction>()

    // Tracking variables for assignment
    val seenVariableNames = mutableSetOf<String>()
    val queuedAssignments: MutableMap<String, AssignmentStatement> = mutableMapOf()
    fun pushQueuedAssignments() {
        queuedAssignments.forEach { (name: String, it: AssignmentStatement) ->
            it.apply {
                queuedAssignments.remove(name)
                instructions.add(Instruction(this))
            }
        }
    }

//     _   _           _        _____
//    | \ | | ___   __| | ___  |_   _|   _ _ __   ___  ___
//    |  \| |/ _ \ / _` |/ _ \   | || | | | '_ \ / _ \/ __|
//    | |\  | (_) | (_| |  __/   | || |_| | |_) |  __/\__ \
//    |_| \_|\___/ \__,_|\___|   |_| \__, | .__/ \___||___/
//                                   |___/|_|

    // SHADER NODE: Base class for all shader types
    abstract inner class ShaderNode(vararg cs: ShaderNode) {

        open var children: List<ShaderNode> = cs.toList()
        abstract fun render(): String
        abstract val nativeTypeName: String // For Variable Declaration
        abstract fun copy(): ShaderNode // For copying branches of the tree

        init {
            pushQueuedAssignments()
        }
    }

    inner class Instruction(instructionNode: ShaderNode) {
        val id: Int = instructionNode.getSnapshotNum() ?: genSnapshotId()
        val text = instructionNode.render()
    }

    // LITERAL EXPR: Holds kotlin float, int, bool
    inner class LiteralExpr<T>(val literalValue: T): ShaderNode() {

        var id = genSnapshotId()

        override fun render(): String {
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
        override fun copy(): ShaderNode {
            val container = this
            return LiteralExpr(literalValue).apply {id = container.id}
        }

        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use LiteralNode as Shader Type")
    }

    // ASSIGNMENT STATEMENT: Renders to an assignment/declaration statement
    inner class AssignmentStatement(
        val name: String,
        val expression: GenExpr<*>,
        val declare: Boolean = false,
        val assign: Boolean = true,
        val modifiers: List<String> = listOf()
    ): ShaderNode(expression) {
        override fun render(): String {
            return buildString {
                appendAll(modifiers, " ")
                if(declare) append(expression.nativeTypeName, " ")
                append(name)
                if(assign) append(" = ", expression.render())
                append(";")
            }
        }
        override fun copy(): AssignmentStatement {
            return AssignmentStatement(name, expression.copy() as GenExpr<*>, declare)
        }
        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use AssignmentNode as Shader Type")
    }

    // VARIABLE EXPR: Expressions that are just variable names
    inner class VariableExpr(val name: String): ShaderNode() {
        override fun render() = name
        override fun copy(): ShaderNode {
            return VariableExpr(name)
        }
        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use VariableNode as Shader Type")
    }

    inner class FunctionExpr(val name: String, vararg cs: ShaderNode): ShaderNode(*cs) {
        override fun render(): String {
            return "$name(${children.render()})"
        }
        override fun copy(): ShaderNode {
            val result = FunctionExpr(name)
            result.children = children.copy()
            return result
        }
        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use FunctionNode as Shader Type")
    }

    inner class OperatorExpr(val name: String, vararg cs: ShaderNode): ShaderNode(*cs) {
        override fun render(): String {
            require(children.size > 1) {"Error in rendering Operator Node: Not Enough Children ($children)"}
            val child0 = children[0]
            val child1 = children[1]
            val c0 = if(child0.children.isNotEmpty() && child0.children[0] is OperatorExpr) {
                "(${child0.render()})"
            } else {
                child0.render()
            }
            val c1 = if(child1.children.isNotEmpty() && child1.children[0] is OperatorExpr) {
                "(${child1.render()})"
            } else {
                child1.render()
            }
            return "$c0$name$c1"
        }
        override fun copy(): ShaderNode {
            val result =  OperatorExpr(name)
            result.children = children.copy()
            return result
        }
        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use OperatorNode as Shader Type")
    }

    inner class ComponentExpr(val name: String, vararg cs: ShaderNode): ShaderNode(*cs) {
        override fun render(): String {
            require(children.isNotEmpty()) {"Error in rendering Component Node: Not Enough Children ($children)"}
            if (children[0] is GenExpr<*>) {
                return "${children[0].render()}.$name"
            }
            return "(${children[0].render()}).$name"
        }
        override fun copy(): ShaderNode {
            return ComponentExpr(name, *(children.copy().toTypedArray()))
        }
        override val nativeTypeName: String
            get() = throw IllegalStateException("Cannot Use ComponentNode as Shader Type")
    }

    interface ExprLen
    interface ExprLen1: ExprLen
    interface ExprLen2: ExprLen
    interface ExprLen3: ExprLen
    interface ExprLen4: ExprLen

//      ____            _____
//     / ___| ___ _ __ | ____|_  ___ __  _ __
//    | |  _ / _ \ '_ \|  _| \ \/ / '_ \| '__|
//    | |_| |  __/ | | | |___ >  <| |_) | |
//     \____|\___|_| |_|_____/_/\_\ .__/|_|
//                                |_|

    private var uniqueExprId = 0
    fun genSnapshotId(): Int = uniqueExprId++

    fun ShaderNode.getSnapshotNum(): Int? {
        val num = when(this) {
            is GenExpr<*> -> id
            is LiteralExpr<*> -> id
            else -> -1
        }
        val result = max(num, children.maxOfOrNull { it.getSnapshotNum() ?: -1 } ?: -1)
        return if(result == -1) null else result
    }

    // GENEXPR: Any expression that can be assigned to a value
    abstract inner class GenExpr<T: ShaderNode>(vararg cs: ShaderNode): ShaderNode(*cs) {

        var name: String? = null
        var assign = true
        var modifiers = listOf<String>()
        var id = genSnapshotId()

        abstract fun new(vararg cs: ShaderNode): GenExpr<T>

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            pushQueuedAssignments()
            val varName = property.name
            name = varName
            println("getting var $name")
            seenVariableNames.add(name!!).ifTrue {
                instructions.add(Instruction(AssignmentStatement(varName, this.copy() as GenExpr<*>, true, assign, modifiers)))
            }
            children = listOf(VariableExpr(varName))
            return this as T
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            pushQueuedAssignments()
            name = property.name
            children = value.children.copy()
            println("pushing $name for assignment")
            queuedAssignments[name!!] = AssignmentStatement(name!!, this, name!! !in seenVariableNames, assign, modifiers)
        }

        override fun copy(): ShaderNode {
            val result = new(*children.toTypedArray())
            result.name = name
            result.id = id
            return result
        }

        override fun render(): String {
            require(children.isNotEmpty()) {"Error Rendering Gen Expr $name of type $nativeTypeName: No Children"}
            return children[0].render()
        }

    }

//     ____              _
//    | __ )  ___   ___ | | ___  __ _ _ __  ___
//    |  _ \ / _ \ / _ \| |/ _ \/ _` | '_ \/ __|
//    | |_) | (_) | (_) | |  __/ (_| | | | \__ \
//    |____/ \___/ \___/|_|\___|\__,_|_| |_|___/

    // GENBEXPR: Base class of boolean and boolean vector expressions
    abstract inner class GenBExpr<T: GenBExpr<T>>(vararg cs: ShaderNode): GenExpr<T>(*cs)

    // BOOLEXPR: Expression of type bool
    inner class BoolExpr(vararg cs: ShaderNode): GenBExpr<BoolExpr>(*cs), ExprLen1 {
        override fun new(vararg cs: ShaderNode) = BoolExpr(*cs)
        override val nativeTypeName = "bool"
    }

    // BVECEXPR: Abstract base class of float vector expressions
    abstract inner class BVecExpr<T: BVecExpr<T>>(vararg cs: ShaderNode): GenBExpr<T>(*cs) {

        abstract val numComponents: Int

        inner class ComponentProvider<R: GenExpr<R>>(val clazz: KClass<R>) {
            operator fun getValue(thisRef: Any?, property: KProperty<*>): R {
                return newByClass(clazz, ComponentExpr(property.name, this@BVecExpr.copy()))
            }

            operator fun setValue(thisRef: Any?, property: KProperty<*>, value: R) {
                val varName = name ?: throw IllegalStateException("Unable to Determine Name of Vector")
                instructions.add(Instruction(AssignmentStatement("${varName}.${property.name}", value, false)))
            }
        }

        val C1 = ComponentProvider(BoolExpr::class)
        val C2 = ComponentProvider(BVec2Expr::class)
        val C3 = ComponentProvider(BVec3Expr::class)
        val C4 = ComponentProvider(BVec4Expr::class)

        var x    by C1; var y    by C1; var xy   by C2; var yx   by C2; val xx   by C2; val yy   by C2
        val xxx  by C3; val xxy  by C3; val xyx  by C3; val xyy  by C3; val yxx  by C3; val yxy  by C3
        val yyx  by C3; val yyy  by C3; val xxxx by C4; val xxxy by C4; val xxyx by C4; val xxyy by C4
        val xyxx by C4; val xyxy by C4; val xyyx by C4; val xyyy by C4; val yxxx by C4; val yxxy by C4
        val yxyx by C4; val yxyy by C4; val yyxx by C4; val yyxy by C4; val yyyx by C4; val yyyy by C4

        override fun render(): String {
            if (children.size == 1) { return super.render() }
            require(children.size <= numComponents) {"Error Rendering $name of type $nativeTypeName: Wrong Number of Children"}
            return "$nativeTypeName(${children.render()})"
        }
    }

    inner class BVec2Expr(vararg cs: ShaderNode): BVecExpr<BVec2Expr>(*cs), ExprLen2 {
        override fun new(vararg cs: ShaderNode) = BVec2Expr(*cs)
        override val nativeTypeName = "bvec2"
        override val numComponents = 2
    }

    inner class BVec3Expr(vararg cs: ShaderNode): BVecExpr<BVec3Expr>(*cs), ExprLen3 {
        override fun new(vararg cs: ShaderNode) = BVec3Expr(*cs)
        override val nativeTypeName = "bvec3"
        override val numComponents = 3

        // Components and Swizzling
        var z    by C1; var xz   by C2; var yz   by C2; var zx   by C2; var zy   by C2; val zz   by C2
        var xyz  by C3; var xzy  by C3; var yxz  by C3; var yzx  by C3; var zxy  by C3; var zyx  by C3
        val xxz  by C3; val xzx  by C3; val xzz  by C3; val yyz  by C3; val yzy  by C3; val yzz  by C3
        val zxx  by C3; val zxz  by C3; val zyy  by C3; val zyz  by C3; val zzx  by C3; val zzy  by C3
        val zzz  by C3; val xxxz by C4; val xxyz by C4; val xxzx by C4; val xxzy by C4; val xxzz by C4
        val xyxz by C4; val xyyz by C4; val xyzx by C4; val xyzy by C4; val xyzz by C4; val xzxx by C4
        val xzxy by C4; val xzxz by C4; val xzyx by C4; val xzyy by C4; val xzyz by C4; val xzzx by C4
        val xzzy by C4; val xzzz by C4; val yxxz by C4; val yxyz by C4; val yxzx by C4; val yxzy by C4
        val yxzz by C4; val yyxz by C4; val yyyz by C4; val yyzx by C4; val yyzy by C4; val yyzz by C4
        val yzxx by C4; val yzxy by C4; val yzxz by C4; val yzyx by C4; val yzyy by C4; val yzyz by C4
        val yzzx by C4; val yzzy by C4; val yzzz by C4; val zxxx by C4; val zxxy by C4; val zxxz by C4
        val zxyx by C4; val zxyy by C4; val zxyz by C4; val zxzx by C4; val zxzy by C4; val zxzz by C4
        val zyxx by C4; val zyxy by C4; val zyxz by C4; val zyyx by C4; val zyyy by C4; val zyyz by C4
        val zyzx by C4; val zyzy by C4; val zyzz by C4; val zzxx by C4; val zzxy by C4; val zzxz by C4
        val zzyx by C4; val zzyy by C4; val zzyz by C4; val zzzx by C4; val zzzy by C4; val zzzz by C4

        var r    by C1; var g    by C1; var b    by C1; var rg   by C2; var rb   by C2; var gr   by C2
        var gb   by C2; var br   by C2; var bg   by C2; val rr   by C2; val gg   by C2; val bb   by C2
        var rgb  by C3; var rbg  by C3; var grb  by C3; var gbr  by C3; var brg  by C3; var bgr  by C3
        val rrr  by C3; val rrg  by C3; val rrb  by C3; val rgr  by C3; val rgg  by C3; val rbr  by C3
        val rbb  by C3; val grr  by C3; val grg  by C3; val ggr  by C3; val ggg  by C3; val ggb  by C3
        val gbg  by C3; val gbb  by C3; val brr  by C3; val brb  by C3; val bgg  by C3; val bgb  by C3
        val bbr  by C3; val bbg  by C3; val bbb  by C3; val rrrr by C4; val rrrg by C4; val rrrb by C4
        val rrgr by C4; val rrgg by C4; val rrgb by C4; val rrbr by C4; val rrbg by C4; val rrbb by C4
        val rgrr by C4; val rgrg by C4; val rgrb by C4; val rggr by C4; val rggg by C4; val rggb by C4
        val rgbr by C4; val rgbg by C4; val rgbb by C4; val rbrr by C4; val rbrg by C4; val rbrb by C4
        val rbgr by C4; val rbgg by C4; val rbgb by C4; val rbbr by C4; val rbbg by C4; val rbbb by C4
        val grrr by C4; val grrg by C4; val grrb by C4; val grgr by C4; val grgg by C4; val grgb by C4
        val grbr by C4; val grbg by C4; val grbb by C4; val ggrr by C4; val ggrg by C4; val ggrb by C4
        val gggr by C4; val gggg by C4; val gggb by C4; val ggbr by C4; val ggbg by C4; val ggbb by C4
        val gbrr by C4; val gbrg by C4; val gbrb by C4; val gbgr by C4; val gbgg by C4; val gbgb by C4
        val gbbr by C4; val gbbg by C4; val gbbb by C4; val brrr by C4; val brrg by C4; val brrb by C4
        val brgr by C4; val brgg by C4; val brgb by C4; val brbr by C4; val brbg by C4; val brbb by C4
        val bgrr by C4; val bgrg by C4; val bgrb by C4; val bggr by C4; val bggg by C4; val bggb by C4
        val bgbr by C4; val bgbg by C4; val bgbb by C4; val bbrr by C4; val bbrg by C4; val bbrb by C4
        val bbgr by C4; val bbgg by C4; val bbgb by C4; val bbbr by C4; val bbbg by C4; val bbbb by C4
    }

    inner class BVec4Expr(vararg cs: ShaderNode): BVecExpr<BVec4Expr>(*cs), ExprLen4 {
        override fun new(vararg cs: ShaderNode) = BVec4Expr(*cs)
        override val nativeTypeName = "bvec4"
        override val numComponents = 4

        // Components and Swizzling
        var z    by C1; var w    by C1; var xz   by C2; var xw   by C2; var yz   by C2; var yw   by C2
        var zx   by C2; var zy   by C2; var zw   by C2; var wx   by C2; var wy   by C2; var wz   by C2
        val zz   by C2; val ww   by C2; var xyz  by C3; var xyw  by C3; var xzy  by C3; var xzw  by C3
        var xwy  by C3; var xwz  by C3; var yxz  by C3; var yxw  by C3; var yzx  by C3; var yzw  by C3
        var ywx  by C3; var ywz  by C3; var zxy  by C3; var zxw  by C3; var zyx  by C3; var zyw  by C3
        var zwx  by C3; var zwy  by C3; var wxy  by C3; var wxz  by C3; var wyx  by C3; var wyz  by C3
        var wzx  by C3; var wzy  by C3; val xxz  by C3; val xxw  by C3; val xzx  by C3; val xzz  by C3
        val xwx  by C3; val xww  by C3; val yyz  by C3; val yyw  by C3; val yzy  by C3; val yzz  by C3
        val ywy  by C3; val yww  by C3; val zxx  by C3; val zxz  by C3; val zyy  by C3; val zyz  by C3
        val zzx  by C3; val zzy  by C3; val zzz  by C3; val zzw  by C3; val zwz  by C3; val zww  by C3
        val wxx  by C3; val wxw  by C3; val wyy  by C3; val wyw  by C3; val wzz  by C3; val wzw  by C3
        val wwx  by C3; val wwy  by C3; val wwz  by C3; val www  by C3; var xyzw by C4; var xywz by C4
        var xzyw by C4; var xzwy by C4; var xwyz by C4; var xwzy by C4; var yxzw by C4; var yxwz by C4
        var yzxw by C4; var yzwx by C4; var ywxz by C4; var ywzx by C4; var zxyw by C4; var zxwy by C4
        var zyxw by C4; var zywx by C4; var zwxy by C4; var zwyx by C4; var wxyz by C4; var wxzy by C4
        var wyxz by C4; var wyzx by C4; var wzxy by C4; var wzyx by C4; val xxxz by C4; val xxxw by C4
        val xxyz by C4; val xxyw by C4; val xxzx by C4; val xxzy by C4; val xxzz by C4; val xxzw by C4
        val xxwx by C4; val xxwy by C4; val xxwz by C4; val xxww by C4; val xyxz by C4; val xyxw by C4
        val xyyz by C4; val xyyw by C4; val xyzx by C4; val xyzy by C4; val xyzz by C4; val xywx by C4
        val xywy by C4; val xyww by C4; val xzxx by C4; val xzxy by C4; val xzxz by C4; val xzxw by C4
        val xzyx by C4; val xzyy by C4; val xzyz by C4; val xzzx by C4; val xzzy by C4; val xzzz by C4
        val xzzw by C4; val xzwx by C4; val xzwz by C4; val xzww by C4; val xwxx by C4; val xwxy by C4
        val xwxz by C4; val xwxw by C4; val xwyx by C4; val xwyy by C4; val xwyw by C4; val xwzx by C4
        val xwzz by C4; val xwzw by C4; val xwwx by C4; val xwwy by C4; val xwwz by C4; val xwww by C4
        val yxxz by C4; val yxxw by C4; val yxyz by C4; val yxyw by C4; val yxzx by C4; val yxzy by C4
        val yxzz by C4; val yxwx by C4; val yxwy by C4; val yxww by C4; val yyxz by C4; val yyxw by C4
        val yyyz by C4; val yyyw by C4; val yyzx by C4; val yyzy by C4; val yyzz by C4; val yyzw by C4
        val yywx by C4; val yywy by C4; val yywz by C4; val yyww by C4; val yzxx by C4; val yzxy by C4
        val yzxz by C4; val yzyx by C4; val yzyy by C4; val yzyz by C4; val yzyw by C4; val yzzx by C4
        val yzzy by C4; val yzzz by C4; val yzzw by C4; val yzwy by C4; val yzwz by C4; val yzww by C4
        val ywxx by C4; val ywxy by C4; val ywxw by C4; val ywyx by C4; val ywyy by C4; val ywyz by C4
        val ywyw by C4; val ywzy by C4; val ywzz by C4; val ywzw by C4; val ywwx by C4; val ywwy by C4
        val ywwz by C4; val ywww by C4; val zxxx by C4; val zxxy by C4; val zxxz by C4; val zxxw by C4
        val zxyx by C4; val zxyy by C4; val zxyz by C4; val zxzx by C4; val zxzy by C4; val zxzz by C4
        val zxzw by C4; val zxwx by C4; val zxwz by C4; val zxww by C4; val zyxx by C4; val zyxy by C4
        val zyxz by C4; val zyyx by C4; val zyyy by C4; val zyyz by C4; val zyyw by C4; val zyzx by C4
        val zyzy by C4; val zyzz by C4; val zyzw by C4; val zywy by C4; val zywz by C4; val zyww by C4
        val zzxx by C4; val zzxy by C4; val zzxz by C4; val zzxw by C4; val zzyx by C4; val zzyy by C4
        val zzyz by C4; val zzyw by C4; val zzzx by C4; val zzzy by C4; val zzzz by C4; val zzzw by C4
        val zzwx by C4; val zzwy by C4; val zzwz by C4; val zzww by C4; val zwxx by C4; val zwxz by C4
        val zwxw by C4; val zwyy by C4; val zwyz by C4; val zwyw by C4; val zwzx by C4; val zwzy by C4
        val zwzz by C4; val zwzw by C4; val zwwx by C4; val zwwy by C4; val zwwz by C4; val zwww by C4
        val wxxx by C4; val wxxy by C4; val wxxz by C4; val wxxw by C4; val wxyx by C4; val wxyy by C4
        val wxyw by C4; val wxzx by C4; val wxzz by C4; val wxzw by C4; val wxwx by C4; val wxwy by C4
        val wxwz by C4; val wxww by C4; val wyxx by C4; val wyxy by C4; val wyxw by C4; val wyyx by C4
        val wyyy by C4; val wyyz by C4; val wyyw by C4; val wyzy by C4; val wyzz by C4; val wyzw by C4
        val wywx by C4; val wywy by C4; val wywz by C4; val wyww by C4; val wzxx by C4; val wzxz by C4
        val wzxw by C4; val wzyy by C4; val wzyz by C4; val wzyw by C4; val wzzx by C4; val wzzy by C4
        val wzzz by C4; val wzzw by C4; val wzwx by C4; val wzwy by C4; val wzwz by C4; val wzww by C4
        val wwxx by C4; val wwxy by C4; val wwxz by C4; val wwxw by C4; val wwyx by C4; val wwyy by C4
        val wwyz by C4; val wwyw by C4; val wwzx by C4; val wwzy by C4; val wwzz by C4; val wwzw by C4
        val wwwx by C4; val wwwy by C4; val wwwz by C4; val wwww by C4

        var r    by C1; var g    by C1; var b    by C1; var a    by C1; var rg   by C2; var rb   by C2
        var ra   by C2; var gr   by C2; var gb   by C2; var ga   by C2; var br   by C2; var bg   by C2
        var ba   by C2; var ar   by C2; var ag   by C2; var ab   by C2; val rr   by C2; val gg   by C2
        val bb   by C2; val aa   by C2; var rgb  by C3; var rga  by C3; var rbg  by C3; var rba  by C3
        var rag  by C3; var rab  by C3; var grb  by C3; var gra  by C3; var gbr  by C3; var gba  by C3
        var gar  by C3; var gab  by C3; var brg  by C3; var bra  by C3; var bgr  by C3; var bga  by C3
        var bar  by C3; var bag  by C3; var arg  by C3; var arb  by C3; var agr  by C3; var agb  by C3
        var abr  by C3; var abg  by C3; val rrr  by C3; val rrg  by C3; val rrb  by C3; val rra  by C3
        val rgr  by C3; val rgg  by C3; val rbr  by C3; val rbb  by C3; val rar  by C3; val raa  by C3
        val grr  by C3; val grg  by C3; val ggr  by C3; val ggg  by C3; val ggb  by C3; val gga  by C3
        val gbg  by C3; val gbb  by C3; val gag  by C3; val gaa  by C3; val brr  by C3; val brb  by C3
        val bgg  by C3; val bgb  by C3; val bbr  by C3; val bbg  by C3; val bbb  by C3; val bba  by C3
        val bab  by C3; val baa  by C3; val arr  by C3; val ara  by C3; val agg  by C3; val aga  by C3
        val abb  by C3; val aba  by C3; val aar  by C3; val aag  by C3; val aab  by C3; val aaa  by C3
        var rgba by C4; var rgab by C4; var rbga by C4; var rbag by C4; var ragb by C4; var rabg by C4
        var grba by C4; var grab by C4; var gbra by C4; var gbar by C4; var garb by C4; var gabr by C4
        var brga by C4; var brag by C4; var bgra by C4; var bgar by C4; var barg by C4; var bagr by C4
        var argb by C4; var arbg by C4; var agrb by C4; var agbr by C4; var abrg by C4; var abgr by C4
        val rrrr by C4; val rrrg by C4; val rrrb by C4; val rrra by C4; val rrgr by C4; val rrgg by C4
        val rrgb by C4; val rrga by C4; val rrbr by C4; val rrbg by C4; val rrbb by C4; val rrba by C4
        val rrar by C4; val rrag by C4; val rrab by C4; val rraa by C4; val rgrr by C4; val rgrg by C4
        val rgrb by C4; val rgra by C4; val rggr by C4; val rggg by C4; val rggb by C4; val rgga by C4
        val rgbr by C4; val rgbg by C4; val rgbb by C4; val rgar by C4; val rgag by C4; val rgaa by C4
        val rbrr by C4; val rbrg by C4; val rbrb by C4; val rbra by C4; val rbgr by C4; val rbgg by C4
        val rbgb by C4; val rbbr by C4; val rbbg by C4; val rbbb by C4; val rbba by C4; val rbar by C4
        val rbab by C4; val rbaa by C4; val rarr by C4; val rarg by C4; val rarb by C4; val rara by C4
        val ragr by C4; val ragg by C4; val raga by C4; val rabr by C4; val rabb by C4; val raba by C4
        val raar by C4; val raag by C4; val raab by C4; val raaa by C4; val grrr by C4; val grrg by C4
        val grrb by C4; val grra by C4; val grgr by C4; val grgg by C4; val grgb by C4; val grga by C4
        val grbr by C4; val grbg by C4; val grbb by C4; val grar by C4; val grag by C4; val graa by C4
        val ggrr by C4; val ggrg by C4; val ggrb by C4; val ggra by C4; val gggr by C4; val gggg by C4
        val gggb by C4; val ggga by C4; val ggbr by C4; val ggbg by C4; val ggbb by C4; val ggba by C4
        val ggar by C4; val ggag by C4; val ggab by C4; val ggaa by C4; val gbrr by C4; val gbrg by C4
        val gbrb by C4; val gbgr by C4; val gbgg by C4; val gbgb by C4; val gbga by C4; val gbbr by C4
        val gbbg by C4; val gbbb by C4; val gbba by C4; val gbag by C4; val gbab by C4; val gbaa by C4
        val garr by C4; val garg by C4; val gara by C4; val gagr by C4; val gagg by C4; val gagb by C4
        val gaga by C4; val gabg by C4; val gabb by C4; val gaba by C4; val gaar by C4; val gaag by C4
        val gaab by C4; val gaaa by C4; val brrr by C4; val brrg by C4; val brrb by C4; val brra by C4
        val brgr by C4; val brgg by C4; val brgb by C4; val brbr by C4; val brbg by C4; val brbb by C4
        val brba by C4; val brar by C4; val brab by C4; val braa by C4; val bgrr by C4; val bgrg by C4
        val bgrb by C4; val bggr by C4; val bggg by C4; val bggb by C4; val bgga by C4; val bgbr by C4
        val bgbg by C4; val bgbb by C4; val bgba by C4; val bgag by C4; val bgab by C4; val bgaa by C4
        val bbrr by C4; val bbrg by C4; val bbrb by C4; val bbra by C4; val bbgr by C4; val bbgg by C4
        val bbgb by C4; val bbga by C4; val bbbr by C4; val bbbg by C4; val bbbb by C4; val bbba by C4
        val bbar by C4; val bbag by C4; val bbab by C4; val bbaa by C4; val barr by C4; val barb by C4
        val bara by C4; val bagg by C4; val bagb by C4; val baga by C4; val babr by C4; val babg by C4
        val babb by C4; val baba by C4; val baar by C4; val baag by C4; val baab by C4; val baaa by C4
        val arrr by C4; val arrg by C4; val arrb by C4; val arra by C4; val argr by C4; val argg by C4
        val arga by C4; val arbr by C4; val arbb by C4; val arba by C4; val arar by C4; val arag by C4
        val arab by C4; val araa by C4; val agrr by C4; val agrg by C4; val agra by C4; val aggr by C4
        val aggg by C4; val aggb by C4; val agga by C4; val agbg by C4; val agbb by C4; val agba by C4
        val agar by C4; val agag by C4; val agab by C4; val agaa by C4; val abrr by C4; val abrb by C4
        val abra by C4; val abgg by C4; val abgb by C4; val abga by C4; val abbr by C4; val abbg by C4
        val abbb by C4; val abba by C4; val abar by C4; val abag by C4; val abab by C4; val abaa by C4
        val aarr by C4; val aarg by C4; val aarb by C4; val aara by C4; val aagr by C4; val aagg by C4
        val aagb by C4; val aaga by C4; val aabr by C4; val aabg by C4; val aabb by C4; val aaba by C4
        val aaar by C4; val aaag by C4; val aaab by C4; val aaaa by C4
    }

    interface NonBool

//     ___       _
//    |_ _|_ __ | |_ ___  __ _  ___ _ __ ___
//     | || '_ \| __/ _ \/ _` |/ _ \ '__/ __|
//     | || | | | ||  __/ (_| |  __/ |  \__ \
//    |___|_| |_|\__\___|\__, |\___|_|  |___/
//                       |___/

    // GENIEXPR: Base class of float and float vector expressions
    abstract inner class GenIExpr<T: GenIExpr<T>>(vararg cs: ShaderNode): GenExpr<T>(*cs), NonBool

    // INTEXPR: Expression of type int
    inner class IntExprImpl(vararg cs: ShaderNode): IntExpr(*cs)
    abstract inner class IntExpr(vararg cs: ShaderNode): GenIExpr<IntExpr>(*cs), ExprLen1 {
        override val nativeTypeName = "int"
        override fun new(vararg cs: ShaderNode) = IntExprImpl(*cs)
    }

    // IVECEXPR: Abstract base class of float vector expressions
    abstract inner class IVecExpr<T: IVecExpr<T>>(vararg cs: ShaderNode): GenIExpr<T>(*cs) {

        abstract val numComponents: Int

        inner class ComponentProvider<R: GenExpr<R>>(val clazz: KClass<R>) {
            operator fun getValue(thisRef: Any?, property: KProperty<*>): R {
                return newByClass(clazz, ComponentExpr(property.name, this@IVecExpr.copy()))
            }

            operator fun setValue(thisRef: Any?, property: KProperty<*>, value: R) {
                val varName = name ?: throw IllegalStateException("Unable to Determine Name of Vector")
                instructions.add(Instruction(AssignmentStatement("${varName}.${property.name}", value, false)))
            }
        }

        val C1 = ComponentProvider(IntExpr::class)
        val C2 = ComponentProvider(IVec2Expr::class)
        val C3 = ComponentProvider(IVec3Expr::class)
        val C4 = ComponentProvider(IVec4Expr::class)

        var x    by C1; var y    by C1; var xy   by C2; var yx   by C2; val xx   by C2; val yy   by C2
        val xxx  by C3; val xxy  by C3; val xyx  by C3; val xyy  by C3; val yxx  by C3; val yxy  by C3
        val yyx  by C3; val yyy  by C3; val xxxx by C4; val xxxy by C4; val xxyx by C4; val xxyy by C4
        val xyxx by C4; val xyxy by C4; val xyyx by C4; val xyyy by C4; val yxxx by C4; val yxxy by C4
        val yxyx by C4; val yxyy by C4; val yyxx by C4; val yyxy by C4; val yyyx by C4; val yyyy by C4

        override fun render(): String {
            if (children.size == 1) { return super.render() }
            require(children.size <= numComponents) {"Error Rendering $name of type $nativeTypeName: Wrong Number of Children"}
            return "$nativeTypeName(${children.render()})"
        }

    }

    inner class IVec2ExprImpl(vararg cs: ShaderNode): IVec2Expr(*cs)
    abstract inner class IVec2Expr(vararg cs: ShaderNode): IVecExpr<IVec2Expr>(*cs), ExprLen2 {
        override fun new(vararg cs: ShaderNode) = IVec2ExprImpl(*cs)
        override val nativeTypeName = "ivec2"
        override val numComponents = 2
    }

    inner class IVec3ExprImpl(vararg cs: ShaderNode): IVec3Expr(*cs)
    abstract inner class IVec3Expr(vararg cs: ShaderNode): IVecExpr<IVec3Expr>(*cs), ExprLen3 {
        override fun new(vararg cs: ShaderNode) = IVec3ExprImpl(*cs)
        override val nativeTypeName = "ivec3"
        override val numComponents = 3

        // Components and Swizzling
        var z    by C1; var xz   by C2; var yz   by C2; var zx   by C2; var zy   by C2; val zz   by C2
        var xyz  by C3; var xzy  by C3; var yxz  by C3; var yzx  by C3; var zxy  by C3; var zyx  by C3
        val xxz  by C3; val xzx  by C3; val xzz  by C3; val yyz  by C3; val yzy  by C3; val yzz  by C3
        val zxx  by C3; val zxz  by C3; val zyy  by C3; val zyz  by C3; val zzx  by C3; val zzy  by C3
        val zzz  by C3; val xxxz by C4; val xxyz by C4; val xxzx by C4; val xxzy by C4; val xxzz by C4
        val xyxz by C4; val xyyz by C4; val xyzx by C4; val xyzy by C4; val xyzz by C4; val xzxx by C4
        val xzxy by C4; val xzxz by C4; val xzyx by C4; val xzyy by C4; val xzyz by C4; val xzzx by C4
        val xzzy by C4; val xzzz by C4; val yxxz by C4; val yxyz by C4; val yxzx by C4; val yxzy by C4
        val yxzz by C4; val yyxz by C4; val yyyz by C4; val yyzx by C4; val yyzy by C4; val yyzz by C4
        val yzxx by C4; val yzxy by C4; val yzxz by C4; val yzyx by C4; val yzyy by C4; val yzyz by C4
        val yzzx by C4; val yzzy by C4; val yzzz by C4; val zxxx by C4; val zxxy by C4; val zxxz by C4
        val zxyx by C4; val zxyy by C4; val zxyz by C4; val zxzx by C4; val zxzy by C4; val zxzz by C4
        val zyxx by C4; val zyxy by C4; val zyxz by C4; val zyyx by C4; val zyyy by C4; val zyyz by C4
        val zyzx by C4; val zyzy by C4; val zyzz by C4; val zzxx by C4; val zzxy by C4; val zzxz by C4
        val zzyx by C4; val zzyy by C4; val zzyz by C4; val zzzx by C4; val zzzy by C4; val zzzz by C4

        var r    by C1; var g    by C1; var b    by C1; var rg   by C2; var rb   by C2; var gr   by C2
        var gb   by C2; var br   by C2; var bg   by C2; val rr   by C2; val gg   by C2; val bb   by C2
        var rgb  by C3; var rbg  by C3; var grb  by C3; var gbr  by C3; var brg  by C3; var bgr  by C3
        val rrr  by C3; val rrg  by C3; val rrb  by C3; val rgr  by C3; val rgg  by C3; val rbr  by C3
        val rbb  by C3; val grr  by C3; val grg  by C3; val ggr  by C3; val ggg  by C3; val ggb  by C3
        val gbg  by C3; val gbb  by C3; val brr  by C3; val brb  by C3; val bgg  by C3; val bgb  by C3
        val bbr  by C3; val bbg  by C3; val bbb  by C3; val rrrr by C4; val rrrg by C4; val rrrb by C4
        val rrgr by C4; val rrgg by C4; val rrgb by C4; val rrbr by C4; val rrbg by C4; val rrbb by C4
        val rgrr by C4; val rgrg by C4; val rgrb by C4; val rggr by C4; val rggg by C4; val rggb by C4
        val rgbr by C4; val rgbg by C4; val rgbb by C4; val rbrr by C4; val rbrg by C4; val rbrb by C4
        val rbgr by C4; val rbgg by C4; val rbgb by C4; val rbbr by C4; val rbbg by C4; val rbbb by C4
        val grrr by C4; val grrg by C4; val grrb by C4; val grgr by C4; val grgg by C4; val grgb by C4
        val grbr by C4; val grbg by C4; val grbb by C4; val ggrr by C4; val ggrg by C4; val ggrb by C4
        val gggr by C4; val gggg by C4; val gggb by C4; val ggbr by C4; val ggbg by C4; val ggbb by C4
        val gbrr by C4; val gbrg by C4; val gbrb by C4; val gbgr by C4; val gbgg by C4; val gbgb by C4
        val gbbr by C4; val gbbg by C4; val gbbb by C4; val brrr by C4; val brrg by C4; val brrb by C4
        val brgr by C4; val brgg by C4; val brgb by C4; val brbr by C4; val brbg by C4; val brbb by C4
        val bgrr by C4; val bgrg by C4; val bgrb by C4; val bggr by C4; val bggg by C4; val bggb by C4
        val bgbr by C4; val bgbg by C4; val bgbb by C4; val bbrr by C4; val bbrg by C4; val bbrb by C4
        val bbgr by C4; val bbgg by C4; val bbgb by C4; val bbbr by C4; val bbbg by C4; val bbbb by C4
    }

    inner class IVec4ExprImpl(vararg cs: ShaderNode): IVec4Expr(*cs)
    abstract inner class IVec4Expr(vararg cs: ShaderNode): IVecExpr<IVec4Expr>(*cs), ExprLen4 {
        override fun new(vararg cs: ShaderNode) = IVec4ExprImpl(*cs)
        override val nativeTypeName = "ivec4"
        override val numComponents = 4

        // Components and Swizzling
        var z    by C1; var w    by C1; var xz   by C2; var xw   by C2; var yz   by C2; var yw   by C2
        var zx   by C2; var zy   by C2; var zw   by C2; var wx   by C2; var wy   by C2; var wz   by C2
        val zz   by C2; val ww   by C2; var xyz  by C3; var xyw  by C3; var xzy  by C3; var xzw  by C3
        var xwy  by C3; var xwz  by C3; var yxz  by C3; var yxw  by C3; var yzx  by C3; var yzw  by C3
        var ywx  by C3; var ywz  by C3; var zxy  by C3; var zxw  by C3; var zyx  by C3; var zyw  by C3
        var zwx  by C3; var zwy  by C3; var wxy  by C3; var wxz  by C3; var wyx  by C3; var wyz  by C3
        var wzx  by C3; var wzy  by C3; val xxz  by C3; val xxw  by C3; val xzx  by C3; val xzz  by C3
        val xwx  by C3; val xww  by C3; val yyz  by C3; val yyw  by C3; val yzy  by C3; val yzz  by C3
        val ywy  by C3; val yww  by C3; val zxx  by C3; val zxz  by C3; val zyy  by C3; val zyz  by C3
        val zzx  by C3; val zzy  by C3; val zzz  by C3; val zzw  by C3; val zwz  by C3; val zww  by C3
        val wxx  by C3; val wxw  by C3; val wyy  by C3; val wyw  by C3; val wzz  by C3; val wzw  by C3
        val wwx  by C3; val wwy  by C3; val wwz  by C3; val www  by C3; var xyzw by C4; var xywz by C4
        var xzyw by C4; var xzwy by C4; var xwyz by C4; var xwzy by C4; var yxzw by C4; var yxwz by C4
        var yzxw by C4; var yzwx by C4; var ywxz by C4; var ywzx by C4; var zxyw by C4; var zxwy by C4
        var zyxw by C4; var zywx by C4; var zwxy by C4; var zwyx by C4; var wxyz by C4; var wxzy by C4
        var wyxz by C4; var wyzx by C4; var wzxy by C4; var wzyx by C4; val xxxz by C4; val xxxw by C4
        val xxyz by C4; val xxyw by C4; val xxzx by C4; val xxzy by C4; val xxzz by C4; val xxzw by C4
        val xxwx by C4; val xxwy by C4; val xxwz by C4; val xxww by C4; val xyxz by C4; val xyxw by C4
        val xyyz by C4; val xyyw by C4; val xyzx by C4; val xyzy by C4; val xyzz by C4; val xywx by C4
        val xywy by C4; val xyww by C4; val xzxx by C4; val xzxy by C4; val xzxz by C4; val xzxw by C4
        val xzyx by C4; val xzyy by C4; val xzyz by C4; val xzzx by C4; val xzzy by C4; val xzzz by C4
        val xzzw by C4; val xzwx by C4; val xzwz by C4; val xzww by C4; val xwxx by C4; val xwxy by C4
        val xwxz by C4; val xwxw by C4; val xwyx by C4; val xwyy by C4; val xwyw by C4; val xwzx by C4
        val xwzz by C4; val xwzw by C4; val xwwx by C4; val xwwy by C4; val xwwz by C4; val xwww by C4
        val yxxz by C4; val yxxw by C4; val yxyz by C4; val yxyw by C4; val yxzx by C4; val yxzy by C4
        val yxzz by C4; val yxwx by C4; val yxwy by C4; val yxww by C4; val yyxz by C4; val yyxw by C4
        val yyyz by C4; val yyyw by C4; val yyzx by C4; val yyzy by C4; val yyzz by C4; val yyzw by C4
        val yywx by C4; val yywy by C4; val yywz by C4; val yyww by C4; val yzxx by C4; val yzxy by C4
        val yzxz by C4; val yzyx by C4; val yzyy by C4; val yzyz by C4; val yzyw by C4; val yzzx by C4
        val yzzy by C4; val yzzz by C4; val yzzw by C4; val yzwy by C4; val yzwz by C4; val yzww by C4
        val ywxx by C4; val ywxy by C4; val ywxw by C4; val ywyx by C4; val ywyy by C4; val ywyz by C4
        val ywyw by C4; val ywzy by C4; val ywzz by C4; val ywzw by C4; val ywwx by C4; val ywwy by C4
        val ywwz by C4; val ywww by C4; val zxxx by C4; val zxxy by C4; val zxxz by C4; val zxxw by C4
        val zxyx by C4; val zxyy by C4; val zxyz by C4; val zxzx by C4; val zxzy by C4; val zxzz by C4
        val zxzw by C4; val zxwx by C4; val zxwz by C4; val zxww by C4; val zyxx by C4; val zyxy by C4
        val zyxz by C4; val zyyx by C4; val zyyy by C4; val zyyz by C4; val zyyw by C4; val zyzx by C4
        val zyzy by C4; val zyzz by C4; val zyzw by C4; val zywy by C4; val zywz by C4; val zyww by C4
        val zzxx by C4; val zzxy by C4; val zzxz by C4; val zzxw by C4; val zzyx by C4; val zzyy by C4
        val zzyz by C4; val zzyw by C4; val zzzx by C4; val zzzy by C4; val zzzz by C4; val zzzw by C4
        val zzwx by C4; val zzwy by C4; val zzwz by C4; val zzww by C4; val zwxx by C4; val zwxz by C4
        val zwxw by C4; val zwyy by C4; val zwyz by C4; val zwyw by C4; val zwzx by C4; val zwzy by C4
        val zwzz by C4; val zwzw by C4; val zwwx by C4; val zwwy by C4; val zwwz by C4; val zwww by C4
        val wxxx by C4; val wxxy by C4; val wxxz by C4; val wxxw by C4; val wxyx by C4; val wxyy by C4
        val wxyw by C4; val wxzx by C4; val wxzz by C4; val wxzw by C4; val wxwx by C4; val wxwy by C4
        val wxwz by C4; val wxww by C4; val wyxx by C4; val wyxy by C4; val wyxw by C4; val wyyx by C4
        val wyyy by C4; val wyyz by C4; val wyyw by C4; val wyzy by C4; val wyzz by C4; val wyzw by C4
        val wywx by C4; val wywy by C4; val wywz by C4; val wyww by C4; val wzxx by C4; val wzxz by C4
        val wzxw by C4; val wzyy by C4; val wzyz by C4; val wzyw by C4; val wzzx by C4; val wzzy by C4
        val wzzz by C4; val wzzw by C4; val wzwx by C4; val wzwy by C4; val wzwz by C4; val wzww by C4
        val wwxx by C4; val wwxy by C4; val wwxz by C4; val wwxw by C4; val wwyx by C4; val wwyy by C4
        val wwyz by C4; val wwyw by C4; val wwzx by C4; val wwzy by C4; val wwzz by C4; val wwzw by C4
        val wwwx by C4; val wwwy by C4; val wwwz by C4; val wwww by C4

        var r    by C1; var g    by C1; var b    by C1; var a    by C1; var rg   by C2; var rb   by C2
        var ra   by C2; var gr   by C2; var gb   by C2; var ga   by C2; var br   by C2; var bg   by C2
        var ba   by C2; var ar   by C2; var ag   by C2; var ab   by C2; val rr   by C2; val gg   by C2
        val bb   by C2; val aa   by C2; var rgb  by C3; var rga  by C3; var rbg  by C3; var rba  by C3
        var rag  by C3; var rab  by C3; var grb  by C3; var gra  by C3; var gbr  by C3; var gba  by C3
        var gar  by C3; var gab  by C3; var brg  by C3; var bra  by C3; var bgr  by C3; var bga  by C3
        var bar  by C3; var bag  by C3; var arg  by C3; var arb  by C3; var agr  by C3; var agb  by C3
        var abr  by C3; var abg  by C3; val rrr  by C3; val rrg  by C3; val rrb  by C3; val rra  by C3
        val rgr  by C3; val rgg  by C3; val rbr  by C3; val rbb  by C3; val rar  by C3; val raa  by C3
        val grr  by C3; val grg  by C3; val ggr  by C3; val ggg  by C3; val ggb  by C3; val gga  by C3
        val gbg  by C3; val gbb  by C3; val gag  by C3; val gaa  by C3; val brr  by C3; val brb  by C3
        val bgg  by C3; val bgb  by C3; val bbr  by C3; val bbg  by C3; val bbb  by C3; val bba  by C3
        val bab  by C3; val baa  by C3; val arr  by C3; val ara  by C3; val agg  by C3; val aga  by C3
        val abb  by C3; val aba  by C3; val aar  by C3; val aag  by C3; val aab  by C3; val aaa  by C3
        var rgba by C4; var rgab by C4; var rbga by C4; var rbag by C4; var ragb by C4; var rabg by C4
        var grba by C4; var grab by C4; var gbra by C4; var gbar by C4; var garb by C4; var gabr by C4
        var brga by C4; var brag by C4; var bgra by C4; var bgar by C4; var barg by C4; var bagr by C4
        var argb by C4; var arbg by C4; var agrb by C4; var agbr by C4; var abrg by C4; var abgr by C4
        val rrrr by C4; val rrrg by C4; val rrrb by C4; val rrra by C4; val rrgr by C4; val rrgg by C4
        val rrgb by C4; val rrga by C4; val rrbr by C4; val rrbg by C4; val rrbb by C4; val rrba by C4
        val rrar by C4; val rrag by C4; val rrab by C4; val rraa by C4; val rgrr by C4; val rgrg by C4
        val rgrb by C4; val rgra by C4; val rggr by C4; val rggg by C4; val rggb by C4; val rgga by C4
        val rgbr by C4; val rgbg by C4; val rgbb by C4; val rgar by C4; val rgag by C4; val rgaa by C4
        val rbrr by C4; val rbrg by C4; val rbrb by C4; val rbra by C4; val rbgr by C4; val rbgg by C4
        val rbgb by C4; val rbbr by C4; val rbbg by C4; val rbbb by C4; val rbba by C4; val rbar by C4
        val rbab by C4; val rbaa by C4; val rarr by C4; val rarg by C4; val rarb by C4; val rara by C4
        val ragr by C4; val ragg by C4; val raga by C4; val rabr by C4; val rabb by C4; val raba by C4
        val raar by C4; val raag by C4; val raab by C4; val raaa by C4; val grrr by C4; val grrg by C4
        val grrb by C4; val grra by C4; val grgr by C4; val grgg by C4; val grgb by C4; val grga by C4
        val grbr by C4; val grbg by C4; val grbb by C4; val grar by C4; val grag by C4; val graa by C4
        val ggrr by C4; val ggrg by C4; val ggrb by C4; val ggra by C4; val gggr by C4; val gggg by C4
        val gggb by C4; val ggga by C4; val ggbr by C4; val ggbg by C4; val ggbb by C4; val ggba by C4
        val ggar by C4; val ggag by C4; val ggab by C4; val ggaa by C4; val gbrr by C4; val gbrg by C4
        val gbrb by C4; val gbgr by C4; val gbgg by C4; val gbgb by C4; val gbga by C4; val gbbr by C4
        val gbbg by C4; val gbbb by C4; val gbba by C4; val gbag by C4; val gbab by C4; val gbaa by C4
        val garr by C4; val garg by C4; val gara by C4; val gagr by C4; val gagg by C4; val gagb by C4
        val gaga by C4; val gabg by C4; val gabb by C4; val gaba by C4; val gaar by C4; val gaag by C4
        val gaab by C4; val gaaa by C4; val brrr by C4; val brrg by C4; val brrb by C4; val brra by C4
        val brgr by C4; val brgg by C4; val brgb by C4; val brbr by C4; val brbg by C4; val brbb by C4
        val brba by C4; val brar by C4; val brab by C4; val braa by C4; val bgrr by C4; val bgrg by C4
        val bgrb by C4; val bggr by C4; val bggg by C4; val bggb by C4; val bgga by C4; val bgbr by C4
        val bgbg by C4; val bgbb by C4; val bgba by C4; val bgag by C4; val bgab by C4; val bgaa by C4
        val bbrr by C4; val bbrg by C4; val bbrb by C4; val bbra by C4; val bbgr by C4; val bbgg by C4
        val bbgb by C4; val bbga by C4; val bbbr by C4; val bbbg by C4; val bbbb by C4; val bbba by C4
        val bbar by C4; val bbag by C4; val bbab by C4; val bbaa by C4; val barr by C4; val barb by C4
        val bara by C4; val bagg by C4; val bagb by C4; val baga by C4; val babr by C4; val babg by C4
        val babb by C4; val baba by C4; val baar by C4; val baag by C4; val baab by C4; val baaa by C4
        val arrr by C4; val arrg by C4; val arrb by C4; val arra by C4; val argr by C4; val argg by C4
        val arga by C4; val arbr by C4; val arbb by C4; val arba by C4; val arar by C4; val arag by C4
        val arab by C4; val araa by C4; val agrr by C4; val agrg by C4; val agra by C4; val aggr by C4
        val aggg by C4; val aggb by C4; val agga by C4; val agbg by C4; val agbb by C4; val agba by C4
        val agar by C4; val agag by C4; val agab by C4; val agaa by C4; val abrr by C4; val abrb by C4
        val abra by C4; val abgg by C4; val abgb by C4; val abga by C4; val abbr by C4; val abbg by C4
        val abbb by C4; val abba by C4; val abar by C4; val abag by C4; val abab by C4; val abaa by C4
        val aarr by C4; val aarg by C4; val aarb by C4; val aara by C4; val aagr by C4; val aagg by C4
        val aagb by C4; val aaga by C4; val aabr by C4; val aabg by C4; val aabb by C4; val aaba by C4
        val aaar by C4; val aaag by C4; val aaab by C4; val aaaa by C4
    }

//     _____ _             _
//    |  ___| | ___   __ _| |_ ___
//    | |_  | |/ _ \ / _` | __/ __|
//    |  _| | | (_) | (_| | |_\__ \
//    |_|   |_|\___/ \__,_|\__|___/

    // GENFEXPR: Base class of float and float vector expressions
    abstract inner class GenFExpr<T: GenFExpr<T>>(vararg cs: ShaderNode): GenExpr<T>(*cs), NonBool

    // FLOATEXPR: Expression of type float
    inner class FloatExprImpl(vararg cs: ShaderNode): FloatExpr(*cs)
    abstract inner class FloatExpr (vararg cs: ShaderNode): GenFExpr<FloatExpr>(*cs), ExprLen1 {
        override val nativeTypeName = "float"
        override fun new(vararg cs: ShaderNode) = FloatExprImpl(*cs)
    }

    // VECEXPR: Abstract base class of float vector expressions
    abstract inner class VecExpr<T: VecExpr<T>>(vararg cs: ShaderNode): GenFExpr<T>(*cs) {

        abstract val numComponents: Int

        inner class ComponentProvider<R: GenExpr<R>>(val clazz: KClass<R>) {
            operator fun getValue(thisRef: Any?, property: KProperty<*>): R {
                return newByClass(clazz, ComponentExpr(property.name, this@VecExpr.copy()))
            }

            operator fun setValue(thisRef: Any?, property: KProperty<*>, value: R) {
                val varName = name ?: throw IllegalStateException("Unable to Determine Name of Vector")
                instructions.add(AssignmentStatement("${varName}.${property.name}", value, false))
            }
        }

        // See swizzle.kt for components

        val C1 = ComponentProvider(FloatExpr::class)
        val C2 = ComponentProvider(Vec2Expr::class)
        val C3 = ComponentProvider(Vec3Expr::class)
        val C4 = ComponentProvider(Vec4Expr::class)

        var x    by C1; var y    by C1; var xy   by C2; var yx   by C2; val xx   by C2; val yy   by C2
        val xxx  by C3; val xxy  by C3; val xyx  by C3; val xyy  by C3; val yxx  by C3; val yxy  by C3
        val yyx  by C3; val yyy  by C3; val xxxx by C4; val xxxy by C4; val xxyx by C4; val xxyy by C4
        val xyxx by C4; val xyxy by C4; val xyyx by C4; val xyyy by C4; val yxxx by C4; val yxxy by C4
        val yxyx by C4; val yxyy by C4; val yyxx by C4; val yyxy by C4; val yyyx by C4; val yyyy by C4

        override fun render(): String {
            if (children.size == 1) { return super.render() }
            require(children.size <= numComponents) {"Error Rendering $name of type $nativeTypeName: Wrong Number of Children"}
            return "$nativeTypeName(${children.render()})"
        }
    }

    inner class Vec2ExprImpl(vararg cs: ShaderNode): Vec2Expr(*cs)
    abstract inner class Vec2Expr(vararg cs: ShaderNode): VecExpr<Vec2Expr>(*cs), ExprLen2 {
        override fun new(vararg cs: ShaderNode) = Vec2ExprImpl(*cs)
        override val nativeTypeName = "vec2"
        override val numComponents = 2
    }

    inner class Vec3ExprImpl(vararg cs: ShaderNode): Vec3Expr(*cs)
    abstract inner class Vec3Expr(vararg cs: ShaderNode): VecExpr<Vec3Expr>(*cs), ExprLen3 {
        override fun new(vararg cs: ShaderNode): Vec3Expr = Vec3ExprImpl(*cs)
        override val nativeTypeName = "vec3"
        override val numComponents = 3

        // Components and Swizzling
        var z    by C1; var xz   by C2; var yz   by C2; var zx   by C2; var zy   by C2; val zz   by C2
        var xyz  by C3; var xzy  by C3; var yxz  by C3; var yzx  by C3; var zxy  by C3; var zyx  by C3
        val xxz  by C3; val xzx  by C3; val xzz  by C3; val yyz  by C3; val yzy  by C3; val yzz  by C3
        val zxx  by C3; val zxz  by C3; val zyy  by C3; val zyz  by C3; val zzx  by C3; val zzy  by C3
        val zzz  by C3; val xxxz by C4; val xxyz by C4; val xxzx by C4; val xxzy by C4; val xxzz by C4
        val xyxz by C4; val xyyz by C4; val xyzx by C4; val xyzy by C4; val xyzz by C4; val xzxx by C4
        val xzxy by C4; val xzxz by C4; val xzyx by C4; val xzyy by C4; val xzyz by C4; val xzzx by C4
        val xzzy by C4; val xzzz by C4; val yxxz by C4; val yxyz by C4; val yxzx by C4; val yxzy by C4
        val yxzz by C4; val yyxz by C4; val yyyz by C4; val yyzx by C4; val yyzy by C4; val yyzz by C4
        val yzxx by C4; val yzxy by C4; val yzxz by C4; val yzyx by C4; val yzyy by C4; val yzyz by C4
        val yzzx by C4; val yzzy by C4; val yzzz by C4; val zxxx by C4; val zxxy by C4; val zxxz by C4
        val zxyx by C4; val zxyy by C4; val zxyz by C4; val zxzx by C4; val zxzy by C4; val zxzz by C4
        val zyxx by C4; val zyxy by C4; val zyxz by C4; val zyyx by C4; val zyyy by C4; val zyyz by C4
        val zyzx by C4; val zyzy by C4; val zyzz by C4; val zzxx by C4; val zzxy by C4; val zzxz by C4
        val zzyx by C4; val zzyy by C4; val zzyz by C4; val zzzx by C4; val zzzy by C4; val zzzz by C4

        var r    by C1; var g    by C1; var b    by C1; var rg   by C2; var rb   by C2; var gr   by C2
        var gb   by C2; var br   by C2; var bg   by C2; val rr   by C2; val gg   by C2; val bb   by C2
        var rgb  by C3; var rbg  by C3; var grb  by C3; var gbr  by C3; var brg  by C3; var bgr  by C3
        val rrr  by C3; val rrg  by C3; val rrb  by C3; val rgr  by C3; val rgg  by C3; val rbr  by C3
        val rbb  by C3; val grr  by C3; val grg  by C3; val ggr  by C3; val ggg  by C3; val ggb  by C3
        val gbg  by C3; val gbb  by C3; val brr  by C3; val brb  by C3; val bgg  by C3; val bgb  by C3
        val bbr  by C3; val bbg  by C3; val bbb  by C3; val rrrr by C4; val rrrg by C4; val rrrb by C4
        val rrgr by C4; val rrgg by C4; val rrgb by C4; val rrbr by C4; val rrbg by C4; val rrbb by C4
        val rgrr by C4; val rgrg by C4; val rgrb by C4; val rggr by C4; val rggg by C4; val rggb by C4
        val rgbr by C4; val rgbg by C4; val rgbb by C4; val rbrr by C4; val rbrg by C4; val rbrb by C4
        val rbgr by C4; val rbgg by C4; val rbgb by C4; val rbbr by C4; val rbbg by C4; val rbbb by C4
        val grrr by C4; val grrg by C4; val grrb by C4; val grgr by C4; val grgg by C4; val grgb by C4
        val grbr by C4; val grbg by C4; val grbb by C4; val ggrr by C4; val ggrg by C4; val ggrb by C4
        val gggr by C4; val gggg by C4; val gggb by C4; val ggbr by C4; val ggbg by C4; val ggbb by C4
        val gbrr by C4; val gbrg by C4; val gbrb by C4; val gbgr by C4; val gbgg by C4; val gbgb by C4
        val gbbr by C4; val gbbg by C4; val gbbb by C4; val brrr by C4; val brrg by C4; val brrb by C4
        val brgr by C4; val brgg by C4; val brgb by C4; val brbr by C4; val brbg by C4; val brbb by C4
        val bgrr by C4; val bgrg by C4; val bgrb by C4; val bggr by C4; val bggg by C4; val bggb by C4
        val bgbr by C4; val bgbg by C4; val bgbb by C4; val bbrr by C4; val bbrg by C4; val bbrb by C4
        val bbgr by C4; val bbgg by C4; val bbgb by C4; val bbbr by C4; val bbbg by C4; val bbbb by C4
    }

    inner class Vec4ExprImpl(vararg cs: ShaderNode): Vec4Expr(*cs)
    abstract inner class Vec4Expr(vararg cs: ShaderNode): VecExpr<Vec4Expr>(*cs), ExprLen4 {
        override fun new(vararg cs: ShaderNode) = Vec4ExprImpl(*cs)
        override val nativeTypeName = "vec4"
        override val numComponents = 4

        // Components and Swizzling
        var z    by C1; var w    by C1; var xz   by C2; var xw   by C2; var yz   by C2; var yw   by C2
        var zx   by C2; var zy   by C2; var zw   by C2; var wx   by C2; var wy   by C2; var wz   by C2
        val zz   by C2; val ww   by C2; var xyz  by C3; var xyw  by C3; var xzy  by C3; var xzw  by C3
        var xwy  by C3; var xwz  by C3; var yxz  by C3; var yxw  by C3; var yzx  by C3; var yzw  by C3
        var ywx  by C3; var ywz  by C3; var zxy  by C3; var zxw  by C3; var zyx  by C3; var zyw  by C3
        var zwx  by C3; var zwy  by C3; var wxy  by C3; var wxz  by C3; var wyx  by C3; var wyz  by C3
        var wzx  by C3; var wzy  by C3; val xxz  by C3; val xxw  by C3; val xzx  by C3; val xzz  by C3
        val xwx  by C3; val xww  by C3; val yyz  by C3; val yyw  by C3; val yzy  by C3; val yzz  by C3
        val ywy  by C3; val yww  by C3; val zxx  by C3; val zxz  by C3; val zyy  by C3; val zyz  by C3
        val zzx  by C3; val zzy  by C3; val zzz  by C3; val zzw  by C3; val zwz  by C3; val zww  by C3
        val wxx  by C3; val wxw  by C3; val wyy  by C3; val wyw  by C3; val wzz  by C3; val wzw  by C3
        val wwx  by C3; val wwy  by C3; val wwz  by C3; val www  by C3; var xyzw by C4; var xywz by C4
        var xzyw by C4; var xzwy by C4; var xwyz by C4; var xwzy by C4; var yxzw by C4; var yxwz by C4
        var yzxw by C4; var yzwx by C4; var ywxz by C4; var ywzx by C4; var zxyw by C4; var zxwy by C4
        var zyxw by C4; var zywx by C4; var zwxy by C4; var zwyx by C4; var wxyz by C4; var wxzy by C4
        var wyxz by C4; var wyzx by C4; var wzxy by C4; var wzyx by C4; val xxxz by C4; val xxxw by C4
        val xxyz by C4; val xxyw by C4; val xxzx by C4; val xxzy by C4; val xxzz by C4; val xxzw by C4
        val xxwx by C4; val xxwy by C4; val xxwz by C4; val xxww by C4; val xyxz by C4; val xyxw by C4
        val xyyz by C4; val xyyw by C4; val xyzx by C4; val xyzy by C4; val xyzz by C4; val xywx by C4
        val xywy by C4; val xyww by C4; val xzxx by C4; val xzxy by C4; val xzxz by C4; val xzxw by C4
        val xzyx by C4; val xzyy by C4; val xzyz by C4; val xzzx by C4; val xzzy by C4; val xzzz by C4
        val xzzw by C4; val xzwx by C4; val xzwz by C4; val xzww by C4; val xwxx by C4; val xwxy by C4
        val xwxz by C4; val xwxw by C4; val xwyx by C4; val xwyy by C4; val xwyw by C4; val xwzx by C4
        val xwzz by C4; val xwzw by C4; val xwwx by C4; val xwwy by C4; val xwwz by C4; val xwww by C4
        val yxxz by C4; val yxxw by C4; val yxyz by C4; val yxyw by C4; val yxzx by C4; val yxzy by C4
        val yxzz by C4; val yxwx by C4; val yxwy by C4; val yxww by C4; val yyxz by C4; val yyxw by C4
        val yyyz by C4; val yyyw by C4; val yyzx by C4; val yyzy by C4; val yyzz by C4; val yyzw by C4
        val yywx by C4; val yywy by C4; val yywz by C4; val yyww by C4; val yzxx by C4; val yzxy by C4
        val yzxz by C4; val yzyx by C4; val yzyy by C4; val yzyz by C4; val yzyw by C4; val yzzx by C4
        val yzzy by C4; val yzzz by C4; val yzzw by C4; val yzwy by C4; val yzwz by C4; val yzww by C4
        val ywxx by C4; val ywxy by C4; val ywxw by C4; val ywyx by C4; val ywyy by C4; val ywyz by C4
        val ywyw by C4; val ywzy by C4; val ywzz by C4; val ywzw by C4; val ywwx by C4; val ywwy by C4
        val ywwz by C4; val ywww by C4; val zxxx by C4; val zxxy by C4; val zxxz by C4; val zxxw by C4
        val zxyx by C4; val zxyy by C4; val zxyz by C4; val zxzx by C4; val zxzy by C4; val zxzz by C4
        val zxzw by C4; val zxwx by C4; val zxwz by C4; val zxww by C4; val zyxx by C4; val zyxy by C4
        val zyxz by C4; val zyyx by C4; val zyyy by C4; val zyyz by C4; val zyyw by C4; val zyzx by C4
        val zyzy by C4; val zyzz by C4; val zyzw by C4; val zywy by C4; val zywz by C4; val zyww by C4
        val zzxx by C4; val zzxy by C4; val zzxz by C4; val zzxw by C4; val zzyx by C4; val zzyy by C4
        val zzyz by C4; val zzyw by C4; val zzzx by C4; val zzzy by C4; val zzzz by C4; val zzzw by C4
        val zzwx by C4; val zzwy by C4; val zzwz by C4; val zzww by C4; val zwxx by C4; val zwxz by C4
        val zwxw by C4; val zwyy by C4; val zwyz by C4; val zwyw by C4; val zwzx by C4; val zwzy by C4
        val zwzz by C4; val zwzw by C4; val zwwx by C4; val zwwy by C4; val zwwz by C4; val zwww by C4
        val wxxx by C4; val wxxy by C4; val wxxz by C4; val wxxw by C4; val wxyx by C4; val wxyy by C4
        val wxyw by C4; val wxzx by C4; val wxzz by C4; val wxzw by C4; val wxwx by C4; val wxwy by C4
        val wxwz by C4; val wxww by C4; val wyxx by C4; val wyxy by C4; val wyxw by C4; val wyyx by C4
        val wyyy by C4; val wyyz by C4; val wyyw by C4; val wyzy by C4; val wyzz by C4; val wyzw by C4
        val wywx by C4; val wywy by C4; val wywz by C4; val wyww by C4; val wzxx by C4; val wzxz by C4
        val wzxw by C4; val wzyy by C4; val wzyz by C4; val wzyw by C4; val wzzx by C4; val wzzy by C4
        val wzzz by C4; val wzzw by C4; val wzwx by C4; val wzwy by C4; val wzwz by C4; val wzww by C4
        val wwxx by C4; val wwxy by C4; val wwxz by C4; val wwxw by C4; val wwyx by C4; val wwyy by C4
        val wwyz by C4; val wwyw by C4; val wwzx by C4; val wwzy by C4; val wwzz by C4; val wwzw by C4
        val wwwx by C4; val wwwy by C4; val wwwz by C4; val wwww by C4

        var r    by C1; var g    by C1; var b    by C1; var a    by C1; var rg   by C2; var rb   by C2
        var ra   by C2; var gr   by C2; var gb   by C2; var ga   by C2; var br   by C2; var bg   by C2
        var ba   by C2; var ar   by C2; var ag   by C2; var ab   by C2; val rr   by C2; val gg   by C2
        val bb   by C2; val aa   by C2; var rgb  by C3; var rga  by C3; var rbg  by C3; var rba  by C3
        var rag  by C3; var rab  by C3; var grb  by C3; var gra  by C3; var gbr  by C3; var gba  by C3
        var gar  by C3; var gab  by C3; var brg  by C3; var bra  by C3; var bgr  by C3; var bga  by C3
        var bar  by C3; var bag  by C3; var arg  by C3; var arb  by C3; var agr  by C3; var agb  by C3
        var abr  by C3; var abg  by C3; val rrr  by C3; val rrg  by C3; val rrb  by C3; val rra  by C3
        val rgr  by C3; val rgg  by C3; val rbr  by C3; val rbb  by C3; val rar  by C3; val raa  by C3
        val grr  by C3; val grg  by C3; val ggr  by C3; val ggg  by C3; val ggb  by C3; val gga  by C3
        val gbg  by C3; val gbb  by C3; val gag  by C3; val gaa  by C3; val brr  by C3; val brb  by C3
        val bgg  by C3; val bgb  by C3; val bbr  by C3; val bbg  by C3; val bbb  by C3; val bba  by C3
        val bab  by C3; val baa  by C3; val arr  by C3; val ara  by C3; val agg  by C3; val aga  by C3
        val abb  by C3; val aba  by C3; val aar  by C3; val aag  by C3; val aab  by C3; val aaa  by C3
        var rgba by C4; var rgab by C4; var rbga by C4; var rbag by C4; var ragb by C4; var rabg by C4
        var grba by C4; var grab by C4; var gbra by C4; var gbar by C4; var garb by C4; var gabr by C4
        var brga by C4; var brag by C4; var bgra by C4; var bgar by C4; var barg by C4; var bagr by C4
        var argb by C4; var arbg by C4; var agrb by C4; var agbr by C4; var abrg by C4; var abgr by C4
        val rrrr by C4; val rrrg by C4; val rrrb by C4; val rrra by C4; val rrgr by C4; val rrgg by C4
        val rrgb by C4; val rrga by C4; val rrbr by C4; val rrbg by C4; val rrbb by C4; val rrba by C4
        val rrar by C4; val rrag by C4; val rrab by C4; val rraa by C4; val rgrr by C4; val rgrg by C4
        val rgrb by C4; val rgra by C4; val rggr by C4; val rggg by C4; val rggb by C4; val rgga by C4
        val rgbr by C4; val rgbg by C4; val rgbb by C4; val rgar by C4; val rgag by C4; val rgaa by C4
        val rbrr by C4; val rbrg by C4; val rbrb by C4; val rbra by C4; val rbgr by C4; val rbgg by C4
        val rbgb by C4; val rbbr by C4; val rbbg by C4; val rbbb by C4; val rbba by C4; val rbar by C4
        val rbab by C4; val rbaa by C4; val rarr by C4; val rarg by C4; val rarb by C4; val rara by C4
        val ragr by C4; val ragg by C4; val raga by C4; val rabr by C4; val rabb by C4; val raba by C4
        val raar by C4; val raag by C4; val raab by C4; val raaa by C4; val grrr by C4; val grrg by C4
        val grrb by C4; val grra by C4; val grgr by C4; val grgg by C4; val grgb by C4; val grga by C4
        val grbr by C4; val grbg by C4; val grbb by C4; val grar by C4; val grag by C4; val graa by C4
        val ggrr by C4; val ggrg by C4; val ggrb by C4; val ggra by C4; val gggr by C4; val gggg by C4
        val gggb by C4; val ggga by C4; val ggbr by C4; val ggbg by C4; val ggbb by C4; val ggba by C4
        val ggar by C4; val ggag by C4; val ggab by C4; val ggaa by C4; val gbrr by C4; val gbrg by C4
        val gbrb by C4; val gbgr by C4; val gbgg by C4; val gbgb by C4; val gbga by C4; val gbbr by C4
        val gbbg by C4; val gbbb by C4; val gbba by C4; val gbag by C4; val gbab by C4; val gbaa by C4
        val garr by C4; val garg by C4; val gara by C4; val gagr by C4; val gagg by C4; val gagb by C4
        val gaga by C4; val gabg by C4; val gabb by C4; val gaba by C4; val gaar by C4; val gaag by C4
        val gaab by C4; val gaaa by C4; val brrr by C4; val brrg by C4; val brrb by C4; val brra by C4
        val brgr by C4; val brgg by C4; val brgb by C4; val brbr by C4; val brbg by C4; val brbb by C4
        val brba by C4; val brar by C4; val brab by C4; val braa by C4; val bgrr by C4; val bgrg by C4
        val bgrb by C4; val bggr by C4; val bggg by C4; val bggb by C4; val bgga by C4; val bgbr by C4
        val bgbg by C4; val bgbb by C4; val bgba by C4; val bgag by C4; val bgab by C4; val bgaa by C4
        val bbrr by C4; val bbrg by C4; val bbrb by C4; val bbra by C4; val bbgr by C4; val bbgg by C4
        val bbgb by C4; val bbga by C4; val bbbr by C4; val bbbg by C4; val bbbb by C4; val bbba by C4
        val bbar by C4; val bbag by C4; val bbab by C4; val bbaa by C4; val barr by C4; val barb by C4
        val bara by C4; val bagg by C4; val bagb by C4; val baga by C4; val babr by C4; val babg by C4
        val babb by C4; val baba by C4; val baar by C4; val baag by C4; val baab by C4; val baaa by C4
        val arrr by C4; val arrg by C4; val arrb by C4; val arra by C4; val argr by C4; val argg by C4
        val arga by C4; val arbr by C4; val arbb by C4; val arba by C4; val arar by C4; val arag by C4
        val arab by C4; val araa by C4; val agrr by C4; val agrg by C4; val agra by C4; val aggr by C4
        val aggg by C4; val aggb by C4; val agga by C4; val agbg by C4; val agbb by C4; val agba by C4
        val agar by C4; val agag by C4; val agab by C4; val agaa by C4; val abrr by C4; val abrb by C4
        val abra by C4; val abgg by C4; val abgb by C4; val abga by C4; val abbr by C4; val abbg by C4
        val abbb by C4; val abba by C4; val abar by C4; val abag by C4; val abab by C4; val abaa by C4
        val aarr by C4; val aarg by C4; val aarb by C4; val aara by C4; val aagr by C4; val aagg by C4
        val aagb by C4; val aaga by C4; val aabr by C4; val aabg by C4; val aabb by C4; val aaba by C4
        val aaar by C4; val aaag by C4; val aaab by C4; val aaaa by C4
    }

//     __  __       _        _
//    |  \/  | __ _| |_ _ __(_)_  __
//    | |\/| |/ _` | __| '__| \ \/ /
//    | |  | | (_| | |_| |  | |>  <
//    |_|  |_|\__,_|\__|_|  |_/_/\_\

    inner class Mat2Expr
    inner class Mat3Expr
    inner class Mat4Expr

//    inner class sampler2D
//    inner class samplerCube

//     ____                        _
//    / ___|  __ _ _ __ ___  _ __ | | ___ _ __
//    \___ \ / _` | '_ ` _ \| '_ \| |/ _ \ '__|
//     ___) | (_| | | | | | | |_) | |  __/ |
//    |____/ \__,_|_| |_| |_| .__/|_|\___|_|
//                          |_|

    inner class Sampler2D(vararg cs: ShaderNode): GenExpr<Sampler2D>(*cs) {
        override val nativeTypeName = "sampler2D"
        override fun new(vararg cs: ShaderNode) = Sampler2D(*cs)
    }

    fun texture(sampler: Sampler2D, P: Vec2Expr): Vec4Expr = functionOf("texture", sampler, P)
    fun textureGrad(sampler: Sampler2D, P: Vec2Expr, dPdx: Vec2Expr, dPdy: Vec2Expr): Vec4Expr = functionOf("textureGrad", sampler, P, dPdx, dPdy)


    inline fun <reified T: GenExpr<*>> Uniform(): T {
        val result = new<T>(LiteralExpr("?"))
        result.modifiers = listOf("uniform")
        result.assign = false
        return result
    }

    inline fun <reified T: GenExpr<*>> Out(): T {
        val result = new<T>(LiteralExpr("?"))
        result.modifiers = listOf("out")
        result.assign = false
        return result
    }

    inline fun <reified T: GenExpr<*>> In(): T {
        val result = new<T>(LiteralExpr("?"))
        result.modifiers = listOf("in")
        result.assign = false
        return result
    }

    operator fun String.unaryPlus() {
        pushQueuedAssignments()
        instructions.add(LiteralExpr(this))
    }

    var iteratorCount = 0
    fun makeIteratorVar(): String {
        var iteratorVarName = "i$iteratorCount"
        while(true) {
            if (iteratorVarName !in seenVariableNames) { break }
            iteratorCount++
            iteratorVarName = "i$iteratorCount"
        }
        return iteratorVarName
    }

    fun For(start: FloatExpr, stop: FloatExpr, step: FloatExpr, block: (i: FloatExpr)->Unit) {
        val iterName = makeIteratorVar()
        val iterVar = float(VariableExpr(iterName))
        +"for(float $iterName=${start.render()}; $iterName<${stop.render()}; $iterName+=${step.render()} {"
        block(iterVar)
        +"}"
    }

    fun For(start: IntExpr, stop: IntExpr, step: IntExpr, block: (i: IntExpr)->Unit) {
        val iterName = makeIteratorVar()
        val iterVar = int(VariableExpr(iterName))
        +"for(int $iterName=${start.render()}; $iterName<${stop.render()}; $iterName+=${step.render()} {"
        block(iterVar)
        +"}"
    }

    fun While(cond: BoolExpr, block: ()->Unit) {
        +"while(${cond.render()}){"
        block()
        +"}"
    }

    fun Main(block: ()->Vec4Expr) {
        var fragColor by Out<Vec4Expr>()
        fragColor
        val num = genSnapshotId()
        +"void main() {"
        val result = block()
        fragColor = result
        +"}"
    }

    val Break: Unit get() {
        +"break;"
    }

    val Continue: Unit get() {
        +"continue;"
    }

    fun fragment(block: KGLSL.()->Unit): String {
        block()
        return instructions.joinToString("\n") { it.render() }
    }

    // List Helper Functions
    fun List<ShaderNode>.render(): String {
        isEmpty().ifTrue {
            return ""
        }
        return joinToString(separator = ", ") { it.render() }
    }

    // Used for Rendering
    fun List<ShaderNode>.copy(): List<ShaderNode> {
        return map { it.copy() }
    }

    inline fun <reified T: GenExpr<*>> new(vararg cs: ShaderNode): T {
        return newByClass(T::class, *cs)
    }

    fun <T: GenExpr<*>> newByClass(clazz: KClass<T>, vararg cs: ShaderNode): T {
        return when(clazz) {
            FloatExpr::class -> FloatExprImpl(*cs)
            Vec2Expr::class -> Vec2ExprImpl(*cs)
            Vec3Expr::class -> Vec3ExprImpl(*cs)
            Vec4Expr::class -> Vec4ExprImpl(*cs)
            BoolExpr::class -> BoolExpr(*cs)
            BVec2Expr::class -> BVec2Expr(*cs)
            BVec3Expr::class -> BVec3Expr(*cs)
            BVec4Expr::class -> BVec4Expr(*cs)
            IntExpr::class -> IntExprImpl(*cs)
            IVec2Expr::class -> IVec2ExprImpl(*cs)
            IVec3Expr::class -> IVec3ExprImpl(*cs)
            IVec4Expr::class -> IVec4ExprImpl(*cs)
            Sampler2D::class -> Sampler2D(*cs)
            else -> throw IllegalStateException("Unable to make type ${clazz.simpleName}")
        } as T
    }

//     ____        _ _ _       ___
//    | __ ) _   _(_) | |_    |_ _|_ __
//    |  _ \| | | | | | __|____| || '_ \
//    | |_) | |_| | | | ||_____| || | | |
//    |____/ \__,_|_|_|\__|   |___|_| |_|
//
//     _____                 _   _
//    |  ___|   _ _ __   ___| |_(_) ___  _ __  ___
//    | |_ | | | | '_ \ / __| __| |/ _ \| '_ \/ __|
//    |  _|| |_| | | | | (__| |_| | (_) | | | \__ \
//    |_|   \__,_|_| |_|\___|\__|_|\___/|_| |_|___/

    // Helper
    inline fun <reified T: GenExpr<*>> functionOf(name: String, vararg cs: ShaderNode): T {
        return new(FunctionExpr(name, *(cs.map { it.copy() }).toTypedArray()))
    }
    inline fun <reified T: GenExpr<*>> operatorOf(name: String, vararg cs: ShaderNode): T {
        return new(OperatorExpr(name, *(cs.map { it.copy() }).toTypedArray()))
    }

    // External Constructors

    private object ConstructorKey
    fun bool(b: Boolean) = BoolExpr(LiteralExpr(b))

    fun int(x: Double): IntExpr = int(x.toInt())
    fun int(x: Float): IntExpr = int(x.toInt())
    fun int(x: Int): IntExpr = IntExprImpl(LiteralExpr(x))
    fun int(x: IntExpr): IntExpr = x
    fun int(x: VariableExpr): IntExpr = IntExprImpl(x)

    fun ivec2(x: IntExpr, y: IntExpr): IVec2Expr = IVec2ExprImpl(x, y)

    fun ivec3(x: IntExpr, y: IntExpr, z: IntExpr): IVec3Expr = IVec3ExprImpl(x, y, z)
    fun ivec3(xy: IVec2Expr, z: IntExpr): IVec3Expr = IVec3ExprImpl(xy.x, xy.y, z)
    fun ivec3(x: IntExpr, yz: IVec2Expr): IVec3Expr = IVec3ExprImpl(x, yz.x, yz.y)
    fun ivec3(xyz: IVec3Expr): IVec3Expr = IVec3ExprImpl(xyz.x, xyz.y, xyz.z)

    fun ivec4(x: IntExpr, y: IntExpr, z: IntExpr, w: IntExpr): IVec4Expr = IVec4ExprImpl(x, y, z, w)
    fun ivec4(xy: IVec2Expr, z: IntExpr, w: IntExpr): IVec4Expr = IVec4ExprImpl(xy.x, xy.y, z, w)
    fun ivec4(x: IntExpr, yz: IVec2Expr, w: IntExpr): IVec4Expr = IVec4ExprImpl(x, yz.x, yz.y, w)
    fun ivec4(x: IntExpr, y: IntExpr, zw: IVec2Expr): IVec4Expr = IVec4ExprImpl(x, y, zw.x, zw.y)
    fun ivec4(xyz: IVec3Expr, w: IntExpr): IVec4Expr = IVec4ExprImpl(xyz.x, xyz.y, xyz.z, w)
    fun ivec4(x: IntExpr, yzw: IVec3Expr): IVec4Expr = IVec4ExprImpl(x, yzw.x, yzw.y, yzw.z)
    fun ivec4(xyzw: IVec4Expr): IVec4Expr = IVec4ExprImpl(xyzw.x, xyzw.y, xyzw.z, xyzw.w)

    fun float(x: Double): FloatExpr = FloatExprImpl(LiteralExpr(x))
    fun float(x: Float): FloatExpr = float(x.toDouble())
    fun float(x: Int): FloatExpr = float(x.toDouble())
    fun float(x: FloatExpr): FloatExpr = x
    fun float(x: VariableExpr): FloatExpr = FloatExprImpl(x)

    fun vec2(x: FloatExpr, y: FloatExpr): Vec2Expr = Vec2ExprImpl(x, y)

    fun vec3(x: FloatExpr, y: FloatExpr, z: FloatExpr): Vec3Expr = Vec3ExprImpl(x, y, z)
    fun vec3(xy: Vec2Expr, z: FloatExpr): Vec3Expr = Vec3ExprImpl(xy.x, xy.y, z)
    fun vec3(x: FloatExpr, yz: Vec2Expr): Vec3Expr = Vec3ExprImpl(x, yz.x, yz.y)
    fun vec3(xyz: Vec3Expr): Vec3Expr = Vec3ExprImpl(xyz.x, xyz.y, xyz.z)

    fun vec4(x: FloatExpr, y: FloatExpr, z: FloatExpr, w: FloatExpr): Vec4Expr = Vec4ExprImpl(x, y, z, w)
    fun vec4(xy: Vec2Expr, z: FloatExpr, w: FloatExpr): Vec4Expr = Vec4ExprImpl(xy.x, xy.y, z, w)
    fun vec4(x: FloatExpr, yz: Vec2Expr, w: FloatExpr): Vec4Expr = Vec4ExprImpl(x, yz.x, yz.y, w)
    fun vec4(x: FloatExpr, y: FloatExpr, zw: Vec2Expr): Vec4Expr = Vec4ExprImpl(x, y, zw.x, zw.y)
    fun vec4(xyz: Vec3Expr, w: FloatExpr): Vec4Expr = Vec4ExprImpl(xyz, w)
    fun vec4(x: FloatExpr, yzw: Vec3Expr): Vec4Expr = Vec4ExprImpl(x, yzw.x, yzw.y, yzw.z)
    fun vec4(xyzw: Vec4Expr): Vec4Expr = Vec4ExprImpl(xyzw.x, xyzw.y, xyzw.z, xyzw.w)

    // Operators
    fun <T: GenExpr<T>> equalTo(left: T, right: T): BoolExpr = BoolExpr(OperatorExpr("==", left.copy(), right.copy()))
    infix fun <T: GenExpr<T>> T.EQ(other: T) = equalTo(this, other)

    operator fun BoolExpr.not() = BoolExpr(FunctionExpr("!", this.copy()))
    infix fun BoolExpr.AND(other: BoolExpr) = BoolExpr(FunctionExpr("&&", this.copy(), other.copy()))
    infix fun BoolExpr.XOR(other: BoolExpr) = BoolExpr(FunctionExpr("^^", this.copy(), other.copy()))
    infix fun BoolExpr.OR(other: BoolExpr) = BoolExpr(FunctionExpr("||", this.copy(), other.copy()))

    inline operator fun <reified T: GenIExpr<*>> T.plus(other: T): T = operatorOf("+", this, other)
    inline operator fun <reified T: IVecExpr<*>> T.plus(other: IntExpr): T = operatorOf("+", this, other)
    inline operator fun <reified T: IVecExpr<*>> IntExpr.plus(other: T): T = operatorOf("+", this, other)
    inline operator fun <reified T: GenIExpr<*>> T.minus(other: T): T = operatorOf("-", this, other)
    inline operator fun <reified T: IVecExpr<*>> T.minus(other: IntExpr): T = operatorOf("-", this, other)
    inline operator fun <reified T: IVecExpr<*>> IntExpr.minus(other: T): T = operatorOf("-", this, other)
    inline operator fun <reified T: GenIExpr<*>> T.times(other: T): T = operatorOf("*", this, other)
    inline operator fun <reified T: IVecExpr<*>> T.times(other: IntExpr): T = operatorOf("*", this, other)
    inline operator fun <reified T: IVecExpr<*>> IntExpr.times(other: T): T = operatorOf("*", this, other)
    inline operator fun <reified T: GenIExpr<*>> T.div(other: T): T = operatorOf("/", this, other)
    inline operator fun <reified T: IVecExpr<*>> T.div(other: IntExpr): T = operatorOf("/", this, other)
    infix fun IntExpr.LT(other: IntExpr) = BoolExpr(OperatorExpr("<", this.copy(), other.copy()))
    infix fun IntExpr.GT(other: IntExpr) = BoolExpr(OperatorExpr(">", this.copy(), other.copy()))
    infix fun IntExpr.LE(other: IntExpr) = BoolExpr(OperatorExpr("<=", this.copy(), other.copy()))
    infix fun IntExpr.GE(other: IntExpr) = BoolExpr(OperatorExpr(">=", this.copy(), other.copy()))

    inline operator fun <reified T: GenFExpr<*>> T.plus(other: T): T = operatorOf("+", this, other)
    inline operator fun <reified T: VecExpr<*>> T.plus(other: FloatExpr): T = operatorOf("+", this, other)
    inline operator fun <reified T: VecExpr<*>> FloatExpr.plus(other: T): T = operatorOf("+", this, other)
    inline operator fun <reified T: GenFExpr<*>> T.minus(other: T): T = operatorOf("-", this, other)
    inline operator fun <reified T: VecExpr<*>> T.minus(other: FloatExpr): T = operatorOf("-", this, other)
    inline operator fun <reified T: VecExpr<*>> FloatExpr.minus(other: T): T = operatorOf("-", this, other)
    inline operator fun <reified T: GenFExpr<*>> T.times(other: T): T = operatorOf("*", this, other)
    inline operator fun <reified T: VecExpr<*>> T.times(other: FloatExpr): T = operatorOf("*", this, other)
    inline operator fun <reified T: VecExpr<*>> FloatExpr.times(other: T): T = operatorOf("*", this, other)
    inline operator fun <reified T: GenFExpr<*>> T.div(other: T): T = operatorOf("/", this, other)
    inline operator fun <reified T: VecExpr<*>> T.div(other: FloatExpr): T = operatorOf("/", this, other)
    infix fun FloatExpr.LT(other: FloatExpr) = BoolExpr(OperatorExpr("<", this.copy(), other.copy()))
    infix fun FloatExpr.GT(other: FloatExpr) = BoolExpr(OperatorExpr(">", this.copy(), other.copy()))
    infix fun FloatExpr.LE(other: FloatExpr) = BoolExpr(OperatorExpr("<=", this.copy(), other.copy()))
    infix fun FloatExpr.GE(other: FloatExpr) = BoolExpr(OperatorExpr(">=", this.copy(), other.copy()))

    // Trig
    inline fun <reified T: GenFExpr<*>> radians(degrees: T): T     = functionOf("radians", degrees)
    inline fun <reified T: GenFExpr<*>> degrees(radians: T): T     = functionOf("degrees", radians)
    inline fun <reified T: GenFExpr<*>> sin(angle: T): T           = functionOf("sin", angle)
    inline fun <reified T: GenFExpr<*>> cos(angle: T): T           = functionOf("cos", angle)
    inline fun <reified T: GenFExpr<*>> tan(angle: T): T           = functionOf("tan", angle)
    inline fun <reified T: GenFExpr<*>> asin(x: T): T              = functionOf("asin", x)
    inline fun <reified T: GenFExpr<*>> acos(x: T): T              = functionOf("acos", x)
    inline fun <reified T: GenFExpr<*>> atan(y_over_x: T): T       = functionOf("atan", y_over_x)
    inline fun <reified T: GenFExpr<*>> atan(y: T, x: T): T        = functionOf("atan", y, x)
    inline fun <reified T: GenFExpr<*>> sinh(x: T): T              = functionOf("sinh", x)
    inline fun <reified T: GenFExpr<*>> cosh(x: T): T              = functionOf("cosh", x)
    inline fun <reified T: GenFExpr<*>> tanh(x: T): T              = functionOf("tanh", x)
    inline fun <reified T: GenFExpr<*>> asinh(x: T): T             = functionOf("asinh", x)
    inline fun <reified T: GenFExpr<*>> acosh(x: T): T             = functionOf("acosh", x)
    inline fun <reified T: GenFExpr<*>> atanh(y_over_x: T): T      = functionOf("atanh", y_over_x)
    inline fun <reified T: GenFExpr<*>> atanh(y: T, x: T): T       = functionOf("atanh", y, x)

    // Common
    inline fun <reified T: GenFExpr<*>> abs(x: T): T               = functionOf("abs", x)
    inline fun <reified T: GenIExpr<*>> abs(x: T): T               = functionOf("abs", x)
    inline fun <reified T: GenFExpr<*>> sign(x: T): T              = functionOf("sign", x)
    inline fun <reified T: GenIExpr<*>> sign(x: T): T              = functionOf("sign", x)
    inline fun <reified T: GenFExpr<*>> floor(x: T): T             = functionOf("floor", x)
    inline fun <reified T: GenIExpr<*>> floor(x: T): T             = functionOf("floor", x)
    inline fun <reified T: GenFExpr<*>> trunc(x: T): T             = functionOf("trunc", x)
    inline fun <reified T: GenFExpr<*>> round(x: T): T             = functionOf("round", x)
    inline fun <reified T: GenFExpr<*>> roundEven(x: T): T         = functionOf("roundEven", x)
    inline fun <reified T: GenFExpr<*>> ceil(x: T): T              = functionOf("ceil", x)
    inline fun <reified T: GenIExpr<*>> fract(x: T): T             = functionOf("fract", x)

    inline fun <reified T: VecExpr<*>>  mod(x: T, y: FloatExpr): T = functionOf("mod", x, y)
    inline fun <reified T: GenFExpr<*>> mod(x: T, y: T): T         = functionOf("mod", x, y)
    inline fun <reified T: GenFExpr<*>> fmod(x: T, i: T): T        = functionOf("fmod", x, i)

    inline fun <reified T: VecExpr<*>>  min(x: T, y: FloatExpr): T = functionOf("min", x, y)
    inline fun <reified T: GenFExpr<*>> min(x: T, y: T): T         = functionOf("min", x, y)
    inline fun <reified T: IVecExpr<*>> min(x: T, y: IntExpr): T   = functionOf("min", x, y)
    inline fun <reified T: GenIExpr<*>> min(x: T, y: T): T         = functionOf("min", x, y)
    inline fun <reified T: VecExpr<*>>  max(x: T, y: FloatExpr): T = functionOf("max", x, y)
    inline fun <reified T: GenFExpr<*>> max(x: T, y: T): T         = functionOf("max", x, y)
    inline fun <reified T: IVecExpr<*>> max(x: T, y: IntExpr): T   = functionOf("max", x, y)
    inline fun <reified T: GenIExpr<*>> max(x: T, y: T): T         = functionOf("max", x, y)

    inline fun <reified T: GenFExpr<*>> clamp(x: T, minVal: T, maxVal: T): T                 = functionOf("clamp", x, minVal, maxVal)
    inline fun <reified T: VecExpr<*>>  clamp(x: T, minVal: FloatExpr, maxVal: FloatExpr): T = functionOf("clamp", x, minVal, maxVal)
    inline fun <reified T: GenIExpr<*>> clamp(x: T, minVal: T, maxVal: T): T                 = functionOf("clamp", x, minVal, maxVal)
    inline fun <reified T: IVecExpr<*>> clamp(x: T, minVal: IntExpr, maxVal: IntExpr): T     = functionOf("clamp", x, minVal, maxVal)

    inline fun <reified T: GenFExpr<*>> mix(x: T, y: T, a: T): T        = functionOf("mix", x, y, a)
    inline fun <reified T: VecExpr<*>> mix(x: T, y: T, a: FloatExpr): T = functionOf("mix", x, y, a)
    inline fun <reified T, U, V: ExprLen> mix(x: T, y: T, a: U): T where T: GenExpr<*>, U : GenBExpr<*>, T: V, U: V = functionOf("mix", x, y, a)

    inline fun <reified T: GenFExpr<*>> dFdx(p: T): T = functionOf("dFdx", p)
    inline fun <reified T: GenFExpr<*>> dFdy(p: T): T = functionOf("dFdy", p)
    inline fun <reified T: GenFExpr<*>> fwidth(p: T): T = functionOf("fwidth", p)

    inline fun <reified T: GenFExpr<*>> step(edge: T, x: T): T = functionOf("step", edge, x)
    inline fun <reified T: VecExpr<*>> step(edge: FloatExpr, x: T): T = functionOf("step", edge, x)
    inline fun <reified T: GenFExpr<*>> smoothstep(edge0: T, edge1: T, x: T): T = functionOf("step", edge0, edge1, x)
    inline fun <reified T: VecExpr<*>> smoothstep(edge0: FloatExpr, edge1: FloatExpr, x: T): T = functionOf("step", edge0, edge1, x)

    inline fun <T, reified U, V: ExprLen> isnan(x: T): U where T: GenFExpr<*>, U: GenBExpr<*>, T: V, U: V = functionOf("isnan", x)
    inline fun <T, reified U, V: ExprLen> isinf(x: T): U where T: GenFExpr<*>, U: GenBExpr<*>, T: V, U: V = functionOf("isinf", x)

    inline fun <reified T: GenFExpr<T>> exp(x: T): T = functionOf("exp", x)
    inline fun <reified T: GenFExpr<T>> exp2(x: T): T = functionOf("exp2", x)
    inline fun <reified T: GenFExpr<T>> inversesqrt(x: T): T = functionOf("inversesqrt", x)
    inline fun <reified T: GenFExpr<T>> log(x: T): T = functionOf("log", x)
    inline fun <reified T: GenFExpr<T>> log2(x: T): T = functionOf("log2", x)
    inline fun <reified T: GenFExpr<T>> sqrt(x: T): T = functionOf("sqrt", x)

    //inline fun <reified T: GenFExpr<T>> fma(a: T, b: T, c: T): T = functionOf("fma", a, b, c)
    inline fun <reified T: GenFExpr<T>> pow(x: T, y: T): T = functionOf("pow", x, y)

    fun cross(x: Vec3Expr, y: Vec3Expr): Vec3Expr = functionOf("cross", x, y)
    fun <T: GenFExpr<*>> distance(p0: T, p1: T): FloatExpr = functionOf("distance", p0, p1)
    fun <T: GenFExpr<*>> dot(x: T, y: T): FloatExpr = functionOf("dot", x, y)
    inline fun <T, reified U, V: ExprLen> equal(x: T, y: T): U where T: GenExpr<*>, U: GenBExpr<*>, T: V, U: V = functionOf("equal", x, y)
    inline fun <T, reified U, V: ExprLen> notequal(x: T, y: T): U where T: GenExpr<*>, U: GenBExpr<*>, T: V, U: V = functionOf("notequal", x, y)
    inline fun <reified T: GenFExpr<*>> faceforward(N: T, I: T, Nref: T): T = functionOf("faceforward", N, I, Nref)
    inline fun <reified T: VecExpr<*>> normalize(v: T): T = functionOf("normalize", v)
    inline fun <reified T: GenFExpr<*>> reflect(I: T, N: T): T = functionOf("reflect", I, N)
    inline fun <reified T: GenFExpr<*>> distance(I: T, N: T, eta: FloatExpr): T = functionOf("distance", I, N, eta)

    fun all(x: BVecExpr<*>): BoolExpr = functionOf("all", x)
    fun any(x: BVecExpr<*>): BoolExpr = functionOf("any", x)
    inline fun <reified T: BVecExpr<*>> not(x: T): T = functionOf("not", x)
    inline fun <T, reified U, V: ExprLen> greaterThan(x: T, y: T): U where T: VecExpr<*>, U: BVecExpr<*>, T: V, U: V = functionOf("greaterThan", x, y)
    inline fun <T, reified U, V: ExprLen> greaterThan(x: T, y: T): U where T: IVecExpr<*>, U: BVecExpr<*>, T: V, U: V = functionOf("greaterThan", x, y)
    inline fun <T, reified U, V: ExprLen> greaterThanEqual(x: T, y: T): U where T: VecExpr<*>, U: BVecExpr<*>, T: V, U: V = functionOf("greaterThanEqual", x, y)
    inline fun <T, reified U, V: ExprLen> greaterThanEqual(x: T, y: T): U where T: IVecExpr<*>, U: BVecExpr<*>, T: V, U: V = functionOf("greaterThanEqual", x, y)
    inline fun <T, reified U, V: ExprLen> lessThan(x: T, y: T): U where T: VecExpr<*>, U: BVecExpr<*>, T: V, U: V = functionOf("lessThan", x, y)
    inline fun <T, reified U, V: ExprLen> lessThan(x: T, y: T): U where T: IVecExpr<*>, U: BVecExpr<*>, T: V, U: V = functionOf("lessThan", x, y)
    inline fun <T, reified U, V: ExprLen> lessThanEqual(x: T, y: T): U where T: VecExpr<*>, U: BVecExpr<*>, T: V, U: V = functionOf("lessThanEqual", x, y)
    inline fun <T, reified U, V: ExprLen> lessThanEqual(x: T, y: T): U where T: IVecExpr<*>, U: BVecExpr<*>, T: V, U: V = functionOf("lessThanEqual", x, y)

}

typealias float = KGLSL.FloatExpr
typealias vec2  = KGLSL.Vec2Expr
typealias vec3  = KGLSL.Vec3Expr
typealias vec4  = KGLSL.Vec4Expr
typealias bool  = KGLSL.BoolExpr
typealias bvec2 = KGLSL.BVec2Expr
typealias bvec3 = KGLSL.BVec3Expr
typealias bvec4 = KGLSL.BVec4Expr
typealias int   = KGLSL.IntExpr
typealias ivec2 = KGLSL.IVec2Expr
typealias ivec3 = KGLSL.IVec3Expr
typealias ivec4 = KGLSL.IVec4Expr


// Common Functions
/*
Matrix
    determinant
    groupMemoryBarrier
    inverse
    matrixCompMult
    outerProduct
    transpose
*/