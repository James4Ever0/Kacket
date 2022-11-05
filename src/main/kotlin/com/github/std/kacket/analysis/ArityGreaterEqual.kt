package com.github.std.kacket.analysis

class ArityGreaterEqual(private val arity: Int) : ProcArityRule {
    override fun apply(actual: Int): Boolean = actual >= arity
}