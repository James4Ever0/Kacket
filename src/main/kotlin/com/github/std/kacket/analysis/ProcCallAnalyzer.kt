package com.github.std.kacket.analysis

import com.github.std.kacket.expr.*
import com.github.std.kacket.parse.Parser
import java.io.Reader

class ProcCallAnalyzer(input: Reader) {
    private val parser = Parser(input)
    private val initEnv = InitProcEnv()
    private val body = mutableListOf<Expression>()

    init {
        init()
        analyzeProgram()
    }

    private fun init() {
        while (!parser.isEnd()) {
            val expr = parser.parseExpr()
            body.add(expr)
            if (expr is Define && expr.expr is Procedure) {
                addProcRule(initEnv, expr.name, expr.expr)
            }
        }
    }

    private fun addProcRule(env: ProcEnv, id: String, proc: Procedure) {
        // TODO: Other situations
        env.addRule(id, arityEqual(proc.args.size))
    }

    private fun analyzeProgram() {
        analyzeExprs(body, initEnv)
    }

    private fun analyzeExprs(exprs: List<Expression>, env: ProcEnv) {
        for (expr in exprs) {
            analyzeExpr(expr, env)
        }
    }

    private fun analyzeExpr(expr: Expression, env: ProcEnv) {
        when (expr) {
            is Call -> analyzeCall(expr, env)
            is Define -> analyzeDefine(expr, env)
            is If -> analyzeIf(expr, env)
            is Let -> analyzeLet(expr, env)
            is Letrec -> analyzeLetrec(expr, env)
            is Procedure -> analyzeProc(expr, env)
            is Var -> ignore()
            is Const -> ignore()
            is Quote -> ignore()
        }
    }


    private fun ignore() {}

    private fun analyzeProc(proc: Procedure, env: ProcEnv) {
        val extended = RestProcEnv(env)
        for (name in proc.args) {
            extended.addRule(name, arityAny())
        }
        analyzeExprs(proc.body, extended)
    }

    private fun analyzeLetrec(letrec: Letrec, env: ProcEnv) {
        val extended = RestProcEnv(env)
        for ((name, expr) in letrec.variables zip letrec.values) {
            when (expr) {
                is Procedure -> {
                    addProcRule(extended, name, expr)
                }

                is Define -> {
                    throw AnalysisError("Invalid Define at (${expr.lineNumber()}, ${expr.columnNumber()})")
                }

                is Const -> {
                    extended.addRule(name, notProc())
                }

                is Quote -> {
                    extended.addRule(name, notProc())
                }

                else -> {
                    extended.addRule(name, arityAny())
                }
            }
        }
        for (expr in letrec.values) {
            analyzeExpr(expr, extended)
        }
        analyzeExprs(letrec.body, extended)
    }

    private fun analyzeLet(let: Let, env: ProcEnv) {
        val extended = RestProcEnv(env)

        for ((name, expr) in let.variables zip let.values) {
            analyzeExpr(expr, env)
            when (expr) {
                is Procedure -> {
                    addProcRule(extended, name, expr)
                }

                is Define -> {
                    throw AnalysisError("Invalid Define at (${expr.lineNumber()}, ${expr.columnNumber()})")
                }

                is Const -> {
                    extended.addRule(name, notProc())
                }

                is Quote -> {
                    extended.addRule(name, notProc())
                }

                else -> {
                    extended.addRule(name, arityAny())
                }
            }
        }
        analyzeExprs(let.body, extended)
    }

    private fun analyzeIf(ifExpr: If, env: ProcEnv) {
        analyzeExpr(ifExpr.pred, env)
        analyzeExpr(ifExpr.conseq, env)
        analyzeExpr(ifExpr.alter, env)
    }

    private fun analyzeDefine(define: Define, env: ProcEnv) {
        if (define.expr is Procedure) {
            val extended = RestProcEnv(env)
            addProcRule(extended, define.name, define.expr)
            analyzeExpr(define.expr, extended)
        } else {
            analyzeExpr(define.expr, env)
        }
    }


    private fun analyzeCall(call: Call, env: ProcEnv) {
        val proc = call.proc
        val args = call.args
        when (proc) {
            is Var -> {
                try {
                    env.applyRule(proc.id.value, args.size)
                } catch (ex: AnalysisError) {
                    println("${ex.message}, at (${proc.lineNumber()}, ${proc.columnNumber()}), procedure:${proc.id.value}")
                }
            }

            is Procedure -> {
                if (proc.args.size != args.size) {
                    println("Arity Mismatch, at (${proc.lineNumber()}, ${proc.columnNumber()}), procedure:<procedure>")
                }
                analyzeProc(proc, env)
            }

            is Const -> {
                throw AnalysisError("Invalid Const, at (${proc.lineNumber()}, ${proc.columnNumber()})")
            }

            is Define -> {
                throw AnalysisError("Invalid Define, at (${proc.lineNumber()}, ${proc.columnNumber()})")
            }

            is Quote -> {
                throw AnalysisError("Invalid Quote at (${proc.lineNumber()}, ${proc.columnNumber()})")
            }

            else -> {
                analyzeExpr(proc, env)
            }

        }
        analyzeExprs(args, env)
    }
}