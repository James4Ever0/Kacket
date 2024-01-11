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

        map["car"] = arityEqual(1)
        map["cdr"] = arityEqual(1)
        map["cadr"] = arityEqual(1)
        map["cddr"] = arityEqual(1)
        map["caddr"] = arityEqual(1)
        map["cdddr"] = arityEqual(1)
        map["cadddr"] = arityEqual(1)
        map["cons"] = arityEqual(2)
        map["append"] = arityAny()
        map["list"] = arityAny()
        map["length"] = arityGreaterEqual(1)
        map["list-ref"] = arityEqual(2)
        map["reverse"] = arityEqual(1)
        map["map"] = arityGreaterEqual(2)

        map["make-vector"] = arityGreaterEqual(1)
        map["vector-set!"] = arityEqual(3)
        map["vector-ref"] = arityEqual(2)
        map["vector"] = arityAny()
        map["vector-length"] = arityEqual(1)

        map["set!"] = arityEqual(2)

        map["and"] = arityAny()
        map["or"] = arityAny()
        map["not"] = arityEqual(1)

        map["number?"] = arityEqual(1)
        map["symbol?"] = arityEqual(1)
        map["list?"] = arityEqual(1)
        map["boolean?"] = arityEqual(1)
        map["integer?"] = arityEqual(1)
        map["zero?"] = arityEqual(1)
        map["vector?"] = arityEqual(1)
        map["pair?"]  = arityEqual(1)
        map["write"] = arityGreaterEqual(1) // TODO: <=2 && >=1
        map["newline"] = arityAny() // TODO: <=1 && >=0

    }

    override fun addRule(id: String, rule: (Int) -> Unit) {
        map[id] = rule
    }

    override fun applyRule(procId: String, actual: Int) {
        val rule = map[procId] ?: throw AnalysisError("Can't find procedure $procId")
        rule.invoke(actual)
    }

}