package com.github.std.kacket.expr

class Program {
    val exprs = mutableListOf<Expression>()
    fun addExpr(expr: Expression) {
        exprs.add(expr)
    }
}