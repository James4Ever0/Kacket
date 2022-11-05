package com.github.std.kacket.analysis

class ArityEqual(private val arity: Int) : ProcArityRule {
    override fun apply(actual: Int): Boolean = arity == actual
}