package com.github.std.kacket.parse

import com.github.std.kacket.expr.Expression
import java.io.InputStreamReader
import java.lang.RuntimeException

class Parser(input: InputStreamReader) {
    private val lexer = Lexer(input)
//    fun parse(): Expression {
//        var token = lexer.nextToken()
//        while (token != EOF) {
//            val result = when {
//                token is Parenthesis && token.isLeft() -> {
//                    val result = parse()
//                    token = lexer.nextToken()
//                    if (token !is Parenthesis || token.isLeft()) {
//                        throw RuntimeException("Syntax Error")
//                    }
//                    result
//                }
//            }
//        }
//    }
}