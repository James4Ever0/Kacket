package com.github.std.kacket.expr

sealed interface Expression {
    fun lineNumber(): Int
    fun columnNumber(): Int
}