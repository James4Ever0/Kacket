package com.github.std.kacket.analysis

sealed interface ProcArityRule {
    fun apply(actual: Int): Boolean
}

