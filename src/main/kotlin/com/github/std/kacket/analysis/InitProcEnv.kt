package com.github.std.kacket.analysis

class InitProcEnv : ProcEnv {
    private val map = HashMap<String, (Int) -> Unit>()

    init {
        addPrimitiveProcsRules()
    }

    private fun addPrimitiveProcsRules() {
        map["+"] = arityAny()
        map["-"] = arityAny()
        map["*"] = arityAny()
        map["/"] = arityAny()
        map["="] = arityGreaterEqual(1)
        map[">"] = arityGreaterEqual(1)
        map[">="] = arityGreaterEqual(1)
        map["<"] = arityGreaterEqual(1)
        map["<="] = arityGreaterEqual(1)

        map["null?"] = arityEqual(1)
        map["eq?"] = arityEqual(2)
        map["eqv?"] = arityEqual(2)
        map["equal?"] = arityEqual(2)
        map["list?"] = arityEqual(1)

        map["car"] = arityEqual(1)
        map["cdr"] = arityEqual(1)
        map["cadr"] = arityEqual(1)
        map["cddr"] = arityEqual(1)
        map["caddr"] = arityEqual(1)
        map["cdddr"] = arityEqual(1)

        map["map"] = arityGreaterEqual(2)

    }

    override fun addRule(id: String, rule: (Int) -> Unit) {
        map[id] = rule
    }

    override fun applyRule(procId: String, actual: Int) {
        val rule = map[procId] ?: throw AnalysisError("Can't find procedure $procId")
        rule.invoke(actual)
    }

}