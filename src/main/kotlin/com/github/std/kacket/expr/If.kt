package com.github.std.kacket.expr

class If(
    val pred: Expression,
    val conseq: Expression,
    val alter: Expression
) : Expression {
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