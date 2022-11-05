package com.github.std.kacket.parse

import com.github.std.kacket.expr.*
import java.io.InputStreamReader
import java.io.Reader

class Parser(input: Reader) {
    private val lexer = Lexer(input)
    fun parseExpr(): Expression {
        val token = lexer.nextToken()
        return when {
            token is Identifier && !isReservedWord(token.value) -> {
                Var(token.value)
            }

            token is Character || token is Bool || token is Num || token is Symbol || token is Text -> {
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
            token is Identifier && token.value == "let" -> parseLet()
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
        fun parseLetPair(variables: MutableList<String>, values: MutableList<Expression>) {
            val start = lexer.nextToken()
            if (start !is Parenthesis || !start.isLeft()) {
                throw ParseError(start)
            }
            val id = lexer.nextToken()
            if (id !is Identifier || isReservedWord(id.value)) {
                throw ParseError(id)
            }
            variables.add(id.value)
            val value = parseExpr()
            values.add(value)
            val end = lexer.nextToken()
            if (end !is Parenthesis || end.isLeft()) {
                throw ParseError(end)
            }
        }

        val token = lexer.nextToken()
        if (token !is Parenthesis || !token.isLeft()) {
            throw ParseError(token)
        }

        val variables = mutableListOf<String>()
        val values = mutableListOf<Expression>()
        parseLetPair(variables, values)

        var peek = lexer.peekToken()
        while (peek is Parenthesis && peek.isLeft()) {
            parseLetPair(variables, values)
            peek = lexer.peekToken()
        }

        val end = lexer.nextToken()
        if (end !is Parenthesis || end.isLeft()) {
            throw ParseError(end)
        }

        val body = mutableListOf<Expression>()
        body.add(parseExpr())

        peek = lexer.peekToken()
        while (peek !is Parenthesis || peek.isLeft()) {
            body.add(parseExpr())
        }
        return Let(variables, values, body)
    }

    private fun parseProc(): Expression {
        val start = lexer.nextToken()
        if (start !is Parenthesis || !start.isLeft()) {
            throw ParseError(start)
        }
        val args = mutableListOf<String>()
        var peek = lexer.peekToken()
        while (peek !is Parenthesis || peek.isLeft()) {
            val id = lexer.nextToken()
            if (id !is Identifier || isReservedWord(id.value)) {
                throw ParseError(id)
            }
            args.add(id.value)
            peek = lexer.peekToken()
        }

        val endOfArgs = lexer.nextToken()
        if (endOfArgs !is Parenthesis || endOfArgs.isLeft()) {
            throw ParseError(endOfArgs)
        }

        val body = mutableListOf<Expression>()
        body.add(parseExpr())
        peek = lexer.peekToken()
        while (peek !is Parenthesis || peek.isLeft()) {
            body.add(parseExpr())
            peek = lexer.peekToken()
        }
        return Procedure(args, body)
    }

    private fun parseIf(): Expression {
        val pred = parseExpr()
        val conseq = parseExpr()
        val alter = parseExpr()
        return If(pred, conseq, alter)
    }

    private fun parseDefine(): Expression {
        fun parseProcSyntaxSugarDefine(): Expression {
            val name = lexer.nextToken()
            if (name !is Identifier || isReservedWord(name.value)) {
                throw ParseError(name)
            }

            val args = mutableListOf<String>()
            var peek = lexer.peekToken()
            while (!(peek is Parenthesis && !peek.isLeft())) {
                val id = lexer.nextToken()
                if (id !is Identifier || isReservedWord(id.value)) {
                    throw ParseError(id)
                }
                args.add(id.value)
                peek = lexer.peekToken()
            }
            val endOfArgs = lexer.nextToken()
            if (!(endOfArgs is Parenthesis && !endOfArgs.isLeft())) {
                throw ParseError(endOfArgs)
            }
            val body = mutableListOf<Expression>()
            body.add(parseExpr())

            peek = lexer.peekToken()
            while (!(peek is Parenthesis && !peek.isLeft())) {
                body.add(parseExpr())
                peek = lexer.peekToken()
            }
            return Define(name.value, Procedure(args, body))
        }

        fun parseIdDefine(id: Identifier): Expression {
            return Define(id.value, parseExpr())
        }

        val token = lexer.nextToken()
        return when {
            token is Parenthesis && token.isLeft() -> {
                parseProcSyntaxSugarDefine()
            }

            token is Identifier && !isReservedWord(token.value) -> {
                parseIdDefine(token)
            }

            else -> throw ParseError(token)
        }
    }
}