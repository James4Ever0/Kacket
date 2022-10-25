package com.github.std.kacket.parse

class Parenthesis(private val char: Char) : Token {
    fun isLeft(): Boolean = char == '(' || char == '['
    override fun toString(): String = "Parenthesis#$char"
}