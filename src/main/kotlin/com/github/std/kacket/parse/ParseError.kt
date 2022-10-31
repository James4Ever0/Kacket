package com.github.std.kacket.parse

import java.lang.RuntimeException

class ParseError(val token: Token) :
    RuntimeException("Syntax Error near (${token.lineNumber()}, ${token.columnNumber()})")