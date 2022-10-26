package com.github.std.kacket.parse

import java.lang.StringBuilder

class Number(
    private val lineNum: Int,
    private val columnNum: Int,
    val value: String
) : Token {
    override fun lineNumber(): Int = lineNum

    override fun columnNumber(): Int = columnNum
    override fun toString(): String = "Number$value"
}