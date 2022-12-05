package com.github.std.kacket.analysis.exten

import com.github.std.kacket.analysis.InitProcEnv
import com.github.std.kacket.analysis.ProcCallAnalyzer
import com.github.std.kacket.analysis.ProcEnv
import com.github.std.kacket.expr.exten.ExtExpr

interface ExtAnalyzer {
    fun modifyEnv(env: InitProcEnv, expr: ExtExpr)
    fun support(expr: ExtExpr): Boolean
    fun analyze(expr: ExtExpr, env: ProcEnv, root: ProcCallAnalyzer)
}