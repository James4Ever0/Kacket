package com.github.std.kacket.parse

import java.lang.RuntimeException

class ParseError : RuntimeException {
    constructor(token: Token) : super("Syntax Error near (${token.lineNumber()}, ${token.columnNumber()}): $token")
    constructor(msg:String) : super(msg)
}