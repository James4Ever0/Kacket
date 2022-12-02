package com.github.std.kacket.parse

class Punctuation(
    private val char: Char,
    private val lineNum: Int,
    private val columnNum: Int
) : Token {
    fun isLeftParenthesis(): Boolean = char == '(' || char == '['
    fun isRightParenthesis(): Boolean = char == ')' || char == ']'

    override fun lineNumber(): Int = lineNum
    override fun columnNumber(): Int = columnNum

    override fun toString(): String =
        char.toString()
}