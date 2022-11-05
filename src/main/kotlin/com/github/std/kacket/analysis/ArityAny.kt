package com.github.std.kacket.analysis

object ArityAny : ProcArityRule {
    override fun apply(actual: Int): Boolean = true
}