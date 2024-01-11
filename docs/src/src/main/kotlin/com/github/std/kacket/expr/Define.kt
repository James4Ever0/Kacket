package com.github.std.kacket.expr

class Define(
    val name: String,
    val expr: Expression,
    private val line: Int,
    private val column: Int
) : Expression {
    override fun lineNumber(): Int = line
    override fun columnNumber(): Int = column
    override fun toString(): String {
        val builder = StringBuilder("(define ")
        builder.append(name)
        builder.append(' ')
        builder.append(expr.toString())
        builder.append(')')
        return builder.toString()
    }
}