package com.github.std.kacket.analysis.exten

import com.github.std.kacket.analysis.AnalysisError
import com.github.std.kacket.analysis.InitProcEnv
import com.github.std.kacket.analysis.ProcCallAnalyzer
import com.github.std.kacket.analysis.ProcEnv
import com.github.std.kacket.expr.exten.Cases
import com.github.std.kacket.expr.exten.ExtExpr

object CasesAnalyzer : ExtAnalyzer {
    override fun modifyEnv(env: InitProcEnv, expr: ExtExpr) {
        return
    }

    override fun support(expr: ExtExpr): Boolean = expr is Cases

    override fun analyze(expr: ExtExpr, env: ProcEnv, root: ProcCallAnalyzer) {
        val case = expr as Cases
        with(case) {
            root.analyzeExpr(case.case, env)
            variants.forEach {
                try {
                    env.applyRule(it.name, it.fields.size)
                } catch (ex: AnalysisError) {
                    println("Cases: Wrong fields count, at (${it.line},${it.col}), name:${it.name}")
                }
                root.analyzeExpr(it.conseq, env)
            }
            root.analyzeExpr(default, env)
        }
    }
}