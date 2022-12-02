package com.github.std.kacket.parse

import com.github.std.kacket.expr.*
import java.io.Reader

class Parser(input: Reader) {
    private val lexer = Lexer(input)
    fun isEnd(): Boolean = lexer.peekToken() is EOF

    fun parseExpr(): Expression {
        val token = lexer.nextToken()
        return when {
            token is Identifier && !isReservedWord(token.value) -> {
                Var(token)
            }

            token is Character || token is Bool || token is Num || token is Symbol || token is Text -> {
                Const(token)
            }

            isLeftParenthesis(token) -> {
                parseSExpr(token.lineNumber(), token.columnNumber())
            }

            token is Punctuation && token.char == '\'' -> {
                parseQuote(token.lineNumber(), token.columnNumber())
            }

            else -> {
                throw ParseError(token)
            }
        }
    }

    private fun isRightParenthesis(token: Token): Boolean =
        token is Punctuation && token.isRightParenthesis()

    private fun isLeftParenthesis(token: Token): Boolean =
        token is Punctuation && token.isLeftParenthesis()

    private fun shouldBeLeftParenthesis(token: Token) {
        if (!(isLeftParenthesis(token))) {
            throw ParseError(token)
        }
    }

    private fun shouldBeRightParenthesis(token: Token) {
        if (!(isRightParenthesis(token))) {
            throw ParseError(token)
        }
    }

    private fun parseQuote(lineNumber: Int, columnNumber: Int): Quote {
        val start = lexer.nextToken()
//        shouldBeLeftParenthesis(start)

        val elements = mutableListOf<Quote.QuoteElement>()
        var token = lexer.nextToken()
        while (!(isRightParenthesis(token))) {
            elements.add(parseQuoteElement(token))
            token = lexer.nextToken()
        }

        shouldBeRightParenthesis(token)
        return Quote(lineNumber, columnNumber, elements)
    }

    private fun parseQuoteElement(token: Token): Quote.QuoteElement {
        return when {
            token is Character || token is Bool || token is Num || token is Identifier || token is Symbol || token is Text -> {
                Quote.ElementConst(token)
            }

            token is Punctuation && token.char == '\'' -> {
                Quote.ElementQuote(parseQuote(token.lineNumber(), token.columnNumber()))
            }

            isLeftParenthesis(token) -> {
                val elements = mutableListOf<Quote.QuoteElement>()
                var next = lexer.nextToken()
                while (!(isRightParenthesis(next))) {
                    elements.add(parseQuoteElement(next))
                    next = lexer.nextToken()
                }
                shouldBeRightParenthesis(next)
                Quote.ElementQuotes(elements)
            }

            else -> throw ParseError(token)
        }
    }

    private fun parseSExpr(line: Int, column: Int): Expression {
        val token = lexer.nextToken()
        val result = when {
            token is Identifier && token.value == "define" -> parseDefine(line, column)
            token is Identifier && token.value == "if" -> parseIf(line, column)
            token is Identifier && token.value == "let" -> parseLet(line, column)
            token is Identifier && token.value == "lambda" -> parseProc(line, column)
            token is Identifier -> parseCall(token, line, column)
            isLeftParenthesis(token) -> parseCall(token, line, column)
            else -> throw ParseError(token)
        }

        shouldBeRightParenthesis(lexer.nextToken())
        return result
    }

    private fun isReservedWord(id: String): Boolean =
        id == "define" || id == "if" || id == "let" || id == "lambda"

    private fun parseCall(token: Token, line: Int, column: Int): Expression {
        fun parseArgs(): List<Expression> {
            val args = mutableListOf<Expression>()
            var peek = lexer.peekToken()
            while (!(isRightParenthesis(peek))) {
                args.add(parseExpr())
                peek = lexer.peekToken()
            }
            return args
        }
        return when {
            isLeftParenthesis(token) -> {
                val proc = parseSExpr(token.lineNumber(), token.columnNumber())
                val args = parseArgs()
                Call(proc, args, line, column)
            }

            token is Identifier && !isReservedWord(token.value) -> {
                val proc = Var(token)
                val args = parseArgs()
                Call(proc, args, line, column)
            }

            else -> throw ParseError(token)
        }
    }

    private fun parseLet(line: Int, column: Int): Expression {
        fun parseLetPair(variables: MutableList<String>, values: MutableList<Expression>) {
            val start = lexer.nextToken()
            shouldBeLeftParenthesis(start)
            val id = lexer.nextToken()
            if (id !is Identifier || isReservedWord(id.value)) {
                throw ParseError(id)
            }
            variables.add(id.value)
            val value = parseExpr()
            values.add(value)
            val end = lexer.nextToken()
            shouldBeRightParenthesis(end)
        }

        val token = lexer.nextToken()
        shouldBeLeftParenthesis(token)

        val variables = mutableListOf<String>()
        val values = mutableListOf<Expression>()
        parseLetPair(variables, values)

        var peek = lexer.peekToken()
        while (isLeftParenthesis(peek)) {
            parseLetPair(variables, values)
            peek = lexer.peekToken()
        }

        val end = lexer.nextToken()
        shouldBeRightParenthesis(end)

        val body = mutableListOf<Expression>()
        body.add(parseExpr())

        peek = lexer.peekToken()
        while (!(isRightParenthesis(peek))) {
            body.add(parseExpr())
        }
        return Let(variables, values, body, line, column)
    }

    private fun parseProc(line: Int, column: Int): Expression {
        val start = lexer.nextToken()
        shouldBeLeftParenthesis(start)

        val args = mutableListOf<String>()
        var peek = lexer.peekToken()
        while (!(isRightParenthesis(peek))) {
            val id = lexer.nextToken()
            if (id !is Identifier || isReservedWord(id.value)) {
                throw ParseError(id)
            }
            args.add(id.value)
            peek = lexer.peekToken()
        }

        val endOfArgs = lexer.nextToken()
        shouldBeRightParenthesis(endOfArgs)

        val body = mutableListOf<Expression>()
        body.add(parseExpr())
        peek = lexer.peekToken()
        while (!(isRightParenthesis(peek))) {
            body.add(parseExpr())
            peek = lexer.peekToken()
        }
        return Procedure(args, body, line, column)
    }

    private fun parseIf(line: Int, column: Int): Expression {
        val pred = parseExpr()
        val conseq = parseExpr()
        val alter = parseExpr()
        return If(pred, conseq, alter, line, column)
    }

    private fun parseDefine(line: Int, column: Int): Expression {
        fun parseProcSyntaxSugarDefine(): Expression {
            val name = lexer.nextToken()
            if (name !is Identifier || isReservedWord(name.value)) {
                throw ParseError(name)
            }

            val args = mutableListOf<String>()
            var peek = lexer.peekToken()
            while (!(isRightParenthesis(peek))) {
                val id = lexer.nextToken()
                if (id !is Identifier || isReservedWord(id.value)) {
                    throw ParseError(id)
                }
                args.add(id.value)
                peek = lexer.peekToken()
            }
            val endOfArgs = lexer.nextToken()
            shouldBeRightParenthesis(endOfArgs)

            val body = mutableListOf<Expression>()
            body.add(parseExpr())

            peek = lexer.peekToken()
            while (!(isRightParenthesis(peek))) {
                body.add(parseExpr())
                peek = lexer.peekToken()
            }
            return Define(name.value, Procedure(args, body, line, column), line, column)
        }

        fun parseIdDefine(id: Identifier): Expression {
            return Define(id.value, parseExpr(), line, column)
        }

        val token = lexer.nextToken()
        return when {
            isLeftParenthesis(token) -> {
                parseProcSyntaxSugarDefine()
            }

            token is Identifier && !isReservedWord(token.value) -> {
                parseIdDefine(token)
            }

            else -> throw ParseError(token)
        }
    }
}