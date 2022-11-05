package com.github.std.kacket.expr

class Call(
    val proc: Expression, // Var, Procedure: known; If, Let: maybe; Call: unknown; Others: Error
    val args: List<Expression>,
    private val line: Int,
    private val column: Int
) : Expression {
    override fun lineNumber(): Int = line
    override fun columnNumber(): Int = column

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