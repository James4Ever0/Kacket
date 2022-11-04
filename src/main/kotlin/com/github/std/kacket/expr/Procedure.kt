package com.github.std.kacket.expr

class Procedure(
    val args: List<String>,
    val body: List<Expression>
) : Expression {
    override fun toString(): String {
        val builder = StringBuilder("(lambda ")
        builder.append('(')
        for (arg in args) {
            builder.append(arg)
            builder.append(' ')
        }
        builder.setLength(builder.length - 1)
        builder.append(") ")
        for (expr in body) {
            builder.append(expr)
        }
        builder.append(')')
        return builder.toString()
    }
}