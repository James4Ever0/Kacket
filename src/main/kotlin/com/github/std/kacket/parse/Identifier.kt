package com.github.std.kacket.parse

class Identifier(private val value: String) : Token {
    override fun toString(): String = "Identifier#$value"
}