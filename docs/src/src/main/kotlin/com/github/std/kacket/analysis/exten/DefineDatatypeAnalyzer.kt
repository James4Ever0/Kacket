package com.github.std.kacket.analysis.exten

import com.github.std.kacket.analysis.*
import com.github.std.kacket.expr.*
import com.github.std.kacket.expr.exten.DefineDatatype
import com.github.std.kacket.expr.exten.ExtExpr
import kotlin.math.exp

object DefineDatatypeAnalyzer : ExtAnalyzer {
    override fun modifyEnv(env: InitProcEnv, expr: ExtExpr) {
        val dtdf = expr as DefineDatatype
        env.addRule(dtdf.predName, arityEqual(1))
        for (variant in dtdf.variants) {
            env.addRule(variant.name, arityEqual(variant.fields.size))
        }
    }

    override fun support(expr: ExtExpr): Boolean = expr is DefineDatatype
    override fun analyze(expr: ExtExpr, env: ProcEnv, root: ProcCallAnalyzer) {
        val dtdf = expr as DefineDatatype
        for (variant in dtdf.variants) {
            for (pred in variant.fields.values) {
                when (pred) {
                    is Var -> {
                        try {
                            env.applyRule(pred.id.value, 1)
                        } catch (ex: AnalysisError) {
                            println("Expect a procedure with 1 arity, at (${pred.lineNumber()}, ${pred.columnNumber()}), procedure:${pred.id.value}")
                        }
                    }

                    is Procedure -> {
                        if (pred.args.size != 1) {
                            println("Expect a procedure with 1 arity, at (${pred.lineNumber()}, ${pred.columnNumber()}): procedure:<procedure>")
                        }
                        root.analyzeExpr(pred, env)
                    }

                    is Const -> {
                        throw AnalysisError("Expect a procedure, at (${pred.lineNumber()}, ${pred.columnNumber()})")
                    }

                    is Define -> {
                        throw AnalysisError("Expect a procedure, at (${pred.lineNumber()}, ${pred.columnNumber()})")
                    }

                    is Quote -> {
                        throw AnalysisError("Expect a procedure, at (${pred.lineNumber()}, ${pred.columnNumber()})")
                    }

                    else -> {
                        root.analyzeExpr(pred, env)
                    }
                }
            }
        }
    }
}