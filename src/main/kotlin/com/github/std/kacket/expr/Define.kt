package com.github.std.kacket.expr

class Define(
    val name: String,
    val expr: Expression
) : Expression {
    override fun toString(): String {
        val builder = StringBuilder("(define ")
        builder.append(name)
        builder.append(' ')
        builder.append(expr.toString())
        builder.append(')')
        return builder.toString()
    }
}