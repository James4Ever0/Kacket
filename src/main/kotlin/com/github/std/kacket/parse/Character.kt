package com.github.std.kacket.parse

class Character(
    private val lineNum: Int,
    private val columnNum: Int,
    val value: Char
) : Token {
    override fun lineNumber(): Int = lineNum

    override fun columnNumber(): Int = columnNum
    override fun toString(): String = "Character#$value"
}