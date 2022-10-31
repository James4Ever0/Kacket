package com.github.std.kacket.expr

class Define(
    private val name: String,
    private val expr: List<Expression>
) : Expression {
}