package com.github.std.kacket.analysis

class RestProcEnv(val out: ProcEnv) : ProcEnv {
    private val map = HashMap<String, (Int) -> Boolean>()
    fun add(vararg idsAndRules: Pair<String, (Int) -> Boolean>) {
        for ((id, rule) in idsAndRules) {
            addRule(id, rule)
        }
    }

    override fun addRule(id: String, rule: (Int) -> Boolean) {
        map[id] = rule
    }

    override fun checkRule(procId: String, actual: Int): Boolean {
        val rule = map[procId]
        if (rule != null) {
            return rule.invoke(actual)
        }
        return out.checkRule(procId, actual)
    }
}