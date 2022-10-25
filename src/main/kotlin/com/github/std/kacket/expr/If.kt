package com.github.std.kacket.expr

class If(
    private val pred: Expression,
    private val conseq: Expression,
    private val alter: Expression
) : Expression {
}