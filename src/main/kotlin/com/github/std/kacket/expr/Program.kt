package com.github.std.kacket.expr

class Program  {
    private val exprs = mutableListOf<Expression>()
    fun addExpr(expr: Expression) {
        exprs.add(expr)
    }
}