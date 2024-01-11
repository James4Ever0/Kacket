package com.github.std.kacket.analysis

class RestProcEnv(private val out: ProcEnv) : ProcEnv {
    private val map = HashMap<String, (Int) -> Unit>()

    override fun addRule(id: String, rule: (Int) -> Unit) {
        map[id] = rule
    }

    override fun applyRule(procId: String, actual: Int) {
        val rule = map[procId]
        if (rule == null) {
            out.applyRule(procId, actual)
            return
        }
        rule.invoke(actual)
    }
}