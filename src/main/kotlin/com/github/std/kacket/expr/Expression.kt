package com.github.std.kacket.expr

interface Expression {
    fun lineNumber(): Int
    fun columnNumber(): Int
}