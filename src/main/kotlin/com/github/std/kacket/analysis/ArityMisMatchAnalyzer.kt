package com.github.std.kacket.analysis

import com.github.std.kacket.expr.*
import com.github.std.kacket.parse.Parser
import java.io.Reader

class ArityMisMatchAnalyzer(input: Reader) {
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
                // TODO: Other situations
                initEnv.addRule(expr.name, arityEqual(expr.expr.args.size))
            }
        }
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
            is Procedure -> analyzeProc(expr, env)
            is Var -> ignore()
            is Const -> ignore()
        }
    }

    private fun ignore() = Unit

    private fun analyzeProc(proc: Procedure, env: ProcEnv) {
        val extended = RestProcEnv(env)
        for (name in proc.args) {
            extended.addRule(name, arityAny())
        }
        analyzeExprs(proc.body, extended)
    }

    private fun analyzeLet(let: Let, env: ProcEnv) {
        val extended = RestProcEnv(env)

        for ((name, expr) in let.variables zip let.values) {
            analyzeExpr(expr, env)
            when (expr) {
                is Procedure -> {
                    extended.addRule(name, arityEqual(expr.args.size))
                }

                is Define -> {
                    throw AnalysisError("Invalid Define at (${expr.lineNumber()}, ${expr.columnNumber()})")
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
            extended.addRule(define.name, arityEqual(define.expr.args.size))
            analyzeExpr(define.expr, extended)
        } else {
            analyzeExpr(define.expr, env)
        }
    }

    private fun arityMisMatch(msg: String, line: Int, col: Int) {
        println("arity mismatch at ($line, $col): $msg")
    }

    private fun analyzeCall(call: Call, env: ProcEnv) {
        val proc = call.proc
        val args = call.args
        when (proc) {
            is Var -> {
                if (!env.checkRule(proc.id.value, args.size)) {
                    arityMisMatch(proc.id.value, proc.lineNumber(), proc.columnNumber())
                }
            }

            is Procedure -> {
                if (proc.args.size != args.size) {
                    arityMisMatch("<procedure>", proc.lineNumber(), proc.columnNumber())
                }
                analyzeProc(proc, env)
            }

            is Const -> {
                throw AnalysisError("Invalid Const at (${proc.lineNumber()}, ${proc.columnNumber()})")
            }

            is Define -> {
                throw AnalysisError("Invalid Define at (${proc.lineNumber()}, ${proc.columnNumber()})")
            }

            else -> {
                analyzeExpr(proc, env)
            }

        }
        analyzeExprs(args, env)
    }
}