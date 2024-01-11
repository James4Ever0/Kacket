package com.github.std.kacket.parse

sealed interface Token {
    fun lineNumber(): Int
    fun columnNumber(): Int
}