package com.github.std.kacket.parse

class Identifier(
    private val value: String,
    private val lineNum: Int,
    private val columnNum: Int
) : Token {
    override fun lineNumber(): Int = lineNum

    override fun columnNumber(): Int = columnNum

    override fun toString(): String = "Identifier#$value"
}