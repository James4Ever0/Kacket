package com.github.std.kacket.parse

import com.github.std.kacket.expr.Call
import com.github.std.kacket.expr.Const
import com.github.std.kacket.expr.Expression
import com.github.std.kacket.expr.Var
import java.io.InputStreamReader

class Parser(input: InputStreamReader) {
    private val lexer = Lexer(input)
    fun parseExpr(): Expression {
        val token = lexer.nextToken()
        return when {
            token is Identifier && !isReservedWord(token.value) -> {
                Var(token.value)
            }

            token is Bool || token is Num || token is Symbol || token is Text -> {
                Const(token)
            }

            token is Parenthesis && token.isLeft() -> {
                parseSExpr()
            }

            else -> {
                throw ParseError(token)
            }
        }
    }

    private fun checkSExprTail() {
        val tail = lexer.nextToken()
        if (tail !is Parenthesis || tail.isLeft()) {
            throw throw ParseError(tail)
        }
    }

    private fun parseSExpr(): Expression {
        val token = lexer.nextToken()
        val result = when {
            token is Identifier && token.value == "define" -> parseDefine()
            token is Identifier && token.value == "if" -> parseIf()
            token is Identifier && token.value == "lambda" -> parseProc()
            token is Identifier -> parseCall(token)
            token is Parenthesis && token.isLeft() -> parseCall(token)
            else -> throw ParseError(token)
        }
        checkSExprTail()
        return result
    }

    private fun isReservedWord(id: String): Boolean =
        id == "define" || id == "if" || id == "let" || id == "lambda"

    // (VarExpr args)
    private fun parseCall(token: Token): Expression {
        fun parseArgs(): List<Expression> {
            val args = mutableListOf<Expression>()
            var peek = lexer.peekToken()
            while (!(peek is Parenthesis && !peek.isLeft())) {
                args.add(parseExpr())
                peek = lexer.peekToken()
            }
            return args
        }
        return when {
            token is Parenthesis && token.isLeft() -> {
                val proc = parseSExpr()
                val args = parseArgs()
                Call(proc, args)
            }

            token is Identifier && !isReservedWord(token.value) -> {
                val proc = Var(token.value)
                val args = parseArgs()
                Call(proc, args)
            }

            else -> throw ParseError(token)
        }
    }

    private fun parseLet(): Expression {
        TODO("Not yet implemented")
    }

    private fun parseProc(): Expression {
        TODO("Not yet implemented")
    }

    private fun parseIf(): Expression {
        TODO("Not yet implemented")
    }

    private fun parseDefine(): Expression {
        TODO("Not yet implemented")
    }
}