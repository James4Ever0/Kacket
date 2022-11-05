package com.github.std.kacket.expr

interface Program {
    fun addExpr(expr: Expression)

    fun exprs(): List<Expression>
}