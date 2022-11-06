package com.github.std.kacket.analysis

import com.github.std.kacket.expr.Define
import com.github.std.kacket.expr.Expression
import com.github.std.kacket.expr.Procedure
import com.github.std.kacket.parse.Parser
import java.io.Reader

class ArityMisMatchAnalyzer(input: Reader) {
    private val parser = Parser(input)
    val initEnv = InitProcEnv()
    private val exprs = mutableListOf<Expression>()

    init {
        while (!parser.isEnd()) {
            val expr = parser.parseExpr()
            exprs.add(expr)
            if (expr is Define && expr.expr is Procedure) {
                // TODO: Other situations
                initEnv.addRule(expr.name, arityEqual(expr.expr.args.size))
            }
        }
    }

    fun analyze() {

    }
}