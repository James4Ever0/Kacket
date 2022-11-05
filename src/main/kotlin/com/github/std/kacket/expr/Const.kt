package com.github.std.kacket.expr

import com.github.std.kacket.parse.*

class Const(
    val token: Token
) : Expression {
    override fun lineNumber(): Int = token.lineNumber()
    override fun columnNumber(): Int = token.columnNumber()
    override fun toString(): String = when {
        token is Text -> {
            token.value
        }

        token is Num -> {
            token.value
        }

        token is Symbol -> {
            token.value
        }

        token is Character -> {
            token.value.toString()
        }

        token is Bool && token.value -> {
            "#t"
        }

        token is Bool && !token.value -> {
            "#f"
        }

        else -> throw ExprError()
    }
}