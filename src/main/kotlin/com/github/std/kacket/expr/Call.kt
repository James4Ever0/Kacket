package com.github.std.kacket.expr

class Call(
    val proc: Expression, // Var, Procedure: known; If, Let: maybe; Call: unknown; Others: Error
    val args: List<Expression>
) : Expression {
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append('(')
        builder.append(proc.toString())
        for (arg in args) {
            builder.append(' ')
            builder.append(arg.toString())
        }
        builder.append(')')
        return builder.toString()
    }
}