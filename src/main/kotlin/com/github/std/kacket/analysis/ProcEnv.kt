package com.github.std.kacket.analysis

sealed interface ProcEnv {
    fun addRule(id: String, rule: (Int) -> Boolean)

    fun checkRule(procId: String, actual: Int): Boolean
}