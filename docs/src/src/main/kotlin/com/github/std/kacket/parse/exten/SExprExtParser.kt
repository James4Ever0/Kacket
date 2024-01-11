package com.github.std.kacket.parse.exten

import com.github.std.kacket.expr.Expression
import com.github.std.kacket.expr.exten.ExtExpr
import com.github.std.kacket.parse.Lexer
import com.github.std.kacket.parse.Parser

interface SExprExtParser {
    fun start(): String
    fun parse(lexer: Lexer, line: Int, col: Int,root: Parser): ExtExpr
}