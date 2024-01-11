package com.github.std.kacket.expr

import com.github.std.kacket.parse.Identifier
import com.github.std.kacket.parse.Token

class Var(
    val id: Identifier
) : Expression {
    override fun lineNumber(): Int = id.lineNumber()
    override fun columnNumber(): Int = id.columnNumber()
    override fun toString(): String = id.value
}