package com.github.std.kacket.expr

class Var(val name: String) : Expression {
    override fun toString(): String = name
}