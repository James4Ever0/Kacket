package com.github.std.kacket.expr

class Begin(
    val body: List<Expression>,
    private val line: Int,
    private val column: Int
) : Expression {
    override fun lineNumber(): Int = line

    override fun columnNumber(): Int = column

    override fun toString(): String {
        val builder = StringBuilder("(begin ")
        for (expr in body){
            builder.append(expr.toString())
        }
        builder.append(")")
        return builder.toString()
    }
}