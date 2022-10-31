package com.github.std.kacket.parse

class Parenthesis(
    private val char: Char,
    private val lineNum: Int,
    private val columnNum: Int
) : Token {
    fun isLeft(): Boolean = char == '(' || char == '['
    override fun lineNumber(): Int = lineNum
    override fun columnNumber(): Int = columnNum

    override fun toString(): String =
        if (isLeft())
            "Parenthesis#Left@(${lineNumber()},${columnNumber()})"
        else "Parenthesis#Right@(${lineNumber()},${columnNumber()})"
}