package com.github.std.kacket.parse

class Bool(
    private val lineNum: Int,
    private val columnNum: Int,
    val value: Boolean
) : Token {
    override fun lineNumber(): Int {
        return lineNum
    }

    override fun columnNumber(): Int {
        return columnNum
    }

    override fun toString(): String = "Bool#$value"
}