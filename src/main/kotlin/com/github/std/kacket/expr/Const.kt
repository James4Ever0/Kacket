package com.github.std.kacket.expr

import com.github.std.kacket.parse.*

class Const(
    val value: Token
) : Expression {
    override fun toString(): String = when {
        value is Text -> {
            value.value
        }

        value is Num -> {
            value.value
        }

        value is Symbol -> {
            value.value
        }

        value is Character -> {
            value.value.toString()
        }

        value is Bool && value.value -> {
            "#t"
        }

        value is Bool && !value.value -> {
            "#f"
        }

        else -> throw ExprError()
    }
}