package com.github.std.kacket.parse

import com.github.std.kacket.expr.Expression
import java.io.InputStreamReader

class Parser(input: InputStreamReader) {
    private val lexer = Lexer(input)
    fun parse(): Expression {
        TODO()
    }
}