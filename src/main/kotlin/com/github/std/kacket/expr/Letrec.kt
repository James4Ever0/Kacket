package com.github.std.kacket.expr

class Letrec(
    val variables: List<String>,
    val values: List<Expression>,
    val body: List<Expression>,
    private val line: Int,
    private val column: Int
) : Expression {
    override fun lineNumber(): Int = line
    override fun columnNumber(): Int = column
    override fun toString(): String {
        val builder = StringBuilder("(letrec (")
        for ((variable, value) in variables zip values) {
            builder.append('[')
            builder.append(variable)
            builder.append(' ')
            builder.append(value)
            builder.append(']')
        }
        builder.append(") ")
        for (expr in body) {
            builder.append(expr.toString())
        }
        builder.append(')')
        return builder.toString()
    }
}