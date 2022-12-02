package com.github.std.kacket.parse

import java.lang.RuntimeException

class ParseError : RuntimeException {
    constructor(token: Token) : super("Syntax Error near (${token.lineNumber()}, ${token.columnNumber()}): $token")
    constructor(line: Int, col: Int) : super("Syntax Error near ($line, $col)")
}