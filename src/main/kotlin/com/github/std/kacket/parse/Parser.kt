package com.github.std.kacket.parse

import com.github.std.kacket.expr.Expression
import com.github.std.kacket.expr.Var
import java.io.InputStreamReader

class Parser(input: InputStreamReader) {
    private val lexer = Lexer(input)

    //    fun parse(): Expression {
//        var token = lexer.nextToken()
//
//        if (token is Identifier) {
//            if (token.value == "define" ||
//                token.value == "let" ||
//                token.value == "if" ||
//                token.value == "lambda"
//            ) {
//                throw RuntimeException()
//            }
//            return Var(token.value)
//        }
//
//        if (token !is Parenthesis || !token.isLeft()) {
//            throw RuntimeException()
//        }
//        token = lexer.nextToken()
//        val result = when {
//            token is EOF -> {
//                throw RuntimeException()
//            }
//
//            token is Identifier && token.value == "define" -> {
//                parseDefine()
//            }
//
//            token is Identifier && token.value == "if" -> {
//                parseIf()
//            }
//
//            token is Identifier && token.value == "let" -> {
//                parseLet()
//            }
//
//            token is Identifier && token.value == "lambda" -> {
//                parseProc()
//            }
//
//            token is Identifier -> {
//                parseCall()
//            }
//        }
//
//        token = lexer.nextToken()
//        if (token !is Parenthesis || token.isLeft()) {
//            throw RuntimeException()
//        }
//        return result
//    }
//    fun parseExpr(first: Token): Expression {
//        if (first is Identifier && !isReservedWord(first.value)) {
//            return Var(first.value)
//        }
//        var token = lexer.nextToken()
//        when{
//            first
//        }
//    }

    private fun isReservedWord(id: String): Boolean =
        id == "define" || id == "if" || id == "let" || id == "lambda"


    private fun parseCall(): Expression {
        TODO("Not yet implemented")
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