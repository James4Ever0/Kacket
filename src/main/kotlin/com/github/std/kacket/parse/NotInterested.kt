package com.github.std.kacket.parse

// character, string, number, symbol
class NotInterested(
    private val lineNum: Int,
    private val columnNum: Int
) : Token {
    override fun lineNumber(): Int = lineNum

    override fun columnNumber(): Int = columnNum

    override fun toString(): String = "NotInterested"
}