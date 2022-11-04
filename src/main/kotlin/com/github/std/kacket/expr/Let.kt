package com.github.std.kacket.expr

class Let(
    val variables: List<String>,
    val values: List<Expression>,
    val body: List<Expression>
) : Expression {
    override fun toString(): String {
        val builder = StringBuilder("(let (")
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