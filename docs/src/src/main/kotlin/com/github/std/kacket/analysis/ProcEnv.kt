package com.github.std.kacket.analysis

sealed interface ProcEnv {
    fun addRule(id: String, rule: (Int) -> Unit)

    fun applyRule(procId: String, actual: Int)
}