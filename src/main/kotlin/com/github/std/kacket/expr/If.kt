package com.github.std.kacket.expr

class If(
    val pred: Expression,
    val conseq: Expression,
    val alter: Expression,
    private val line: Int,
    private val column: Int
) : Expression {
    override fun lineNumber(): Int = line
    override fun columnNumber(): Int = column
    override fun toString(): String {
        val builder = StringBuilder("(if ")
        builder.append(pred.toString())
        builder.append(' ')
        builder.append(conseq.toString())
        builder.append(' ')
        builder.append(alter.toString())
        builder.append(')')
        return builder.toString()
    }
}