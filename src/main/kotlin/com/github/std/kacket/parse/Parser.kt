package com.github.std.kacket.parse

import com.github.std.kacket.expr.*
import com.github.std.kacket.parse.exten.SExprExtParser
import java.io.Reader

class Parser(
    input: Reader,
    line: Int = 1,
    column: Int = 1
) {
    private val lexer = Lexer(input, line, column)
    private val sExprExts = mutableListOf<SExprExtParser>()
    private val reservedWords = mutableListOf("define", "if", "else", "let", "letrec", "let*", "cond", "lambda")
    fun isEnd(): Boolean = lexer.peekToken() is EOF

    fun addSExprExt(ext: SExprExtParser): Parser {
        reservedWords.add(ext.start())
        sExprExts.add(ext)
        return this
    }

    fun isRightParenthesis(token: Token): Boolean = token is Punctuation && token.isRightParenthesis()

    fun isLeftParenthesis(token: Token): Boolean = token is Punctuation && token.isLeftParenthesis()

    fun shouldBeLeftParenthesis(token: Token) {
        if (!(isLeftParenthesis(token))) {
            throw ParseError(token)
        }
    }

    fun shouldBeRightParenthesis(token: Token) {
        if (!(isRightParenthesis(token))) {
            throw ParseError(token)
        }
    }

    fun shouldBeNameToken(token: Token) {
        if (token !is Identifier || reservedWords.contains(token.value)) {
            throw ParseError(token)
        }
    }

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
            token is Identifier && token.value == "letrec" -> parseLetrec(line, column)
            token is Identifier && token.value == "let*" -> parseLetstar(line, column)
            token is Identifier && token.value == "cond" -> parseCond(line, column)
            token is Identifier && token.value == "begin" -> parseBegin(line, column)
            token is Identifier -> {
                val ext = sExprExts.find { it.start() == token.value }
                ext?.parse(lexer, line, column, this) ?: parseCall(token, line, column)
            }

            isLeftParenthesis(token) -> parseCall(token, line, column)
            else -> throw ParseError(token)
        }

        shouldBeRightParenthesis(lexer.nextToken())
        return result
    }

    private fun parseBegin(line: Int, column: Int): Begin {
        var peek = lexer.peekToken()
        val body = mutableListOf<Expression>()
        while (!(isRightParenthesis(peek))) {
            body.add(parseExpr())
            peek = lexer.peekToken()
        }
        return Begin(body, line, column)
    }

    private fun isReservedWord(id: String): Boolean =
        reservedWords.contains(id)


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

            else -> {
                val msg =
                    "Syntax Error at(${token.lineNumber()}, ${token.columnNumber()}), expect an Identifier or S-Expression"
                throw ParseError(msg)
            }
        }
    }

    private fun parseLetrec(line: Int, column: Int): Expression {

        val variables = mutableListOf<String>()
        val values = mutableListOf<Expression>()
        parseLetPairs(variables, values)

        val body = mutableListOf<Expression>()
        parseLetBody(body)

        return Letrec(variables, values, body, line, column)
    }

    private fun parseLetPair(variables: MutableList<String>, values: MutableList<Expression>) {
        shouldBeLeftParenthesis(lexer.nextToken())

        val id = lexer.nextToken()
        if (id !is Identifier || isReservedWord(id.value)) {
            throw ParseError(id)
        }
        variables.add(id.value)

        val value = parseExpr()
        values.add(value)

        shouldBeRightParenthesis(lexer.nextToken())
    }

    private fun parseLetPairs(variables: MutableList<String>, values: MutableList<Expression>) {
        shouldBeLeftParenthesis(lexer.nextToken())

//        parseLetPair(variables, values)

        var peek = lexer.peekToken()
        while (isLeftParenthesis(peek)) {
            parseLetPair(variables, values)
            peek = lexer.peekToken()
        }

        shouldBeRightParenthesis(lexer.nextToken())
    }

    private fun parseLetBody(body: MutableList<Expression>) {
        body.add(parseExpr())

        var peek = lexer.peekToken()
        while (!(isRightParenthesis(peek))) {
            body.add(parseExpr())
            peek = lexer.peekToken()
        }
    }

    private fun parseNormalLet(line: Int, column: Int): Expression {

        val variables = mutableListOf<String>()
        val values = mutableListOf<Expression>()
        parseLetPairs(variables, values)

        val body = mutableListOf<Expression>()
        parseLetBody(body)

        return Let(variables, values, body, line, column)
    }

    private fun parseNamedLet(line: Int, column: Int): Expression {
        val token = lexer.nextToken()
        if (token !is Identifier || isReservedWord(token.value)) {
            throw ParseError("Invalid Let near ($line, $column)")
        }

        val procArgs = mutableListOf<String>()
        val values = mutableListOf<Expression>()
        parseLetPairs(procArgs, values)

        val procBody = mutableListOf<Expression>()
        parseLetBody(procBody)

        val proc = Procedure(procArgs, procBody, line, column)
        val letrecBody: List<Expression> = listOf(Call(Var(token), values, line, column))

        return Letrec(
            listOf(token.value), listOf(proc), letrecBody, line, column
        )
    }

    private fun parseLetstar(line: Int, column: Int): Expression {
        val variables = mutableListOf<String>()
        val values = mutableListOf<Expression>()
        parseLetPairs(variables, values)

        val body = mutableListOf<Expression>()
        parseLetBody(body)

        if (variables.isEmpty() && values.isEmpty()) {
            return Let(variables, values, body, line, column)
        }

        var index = variables.size - 1
        var let = Let(
            listOf(variables[index]), listOf(values[index]), body, line, column
        )
        while (index > 0) {
            index--
            let = Let(
                listOf(variables[index]), listOf(values[index]), listOf(let), line, column
            )
        }
        return let
    }

    private fun parseLet(line: Int, column: Int): Expression {
        val token = lexer.peekToken()
        return when {
            isLeftParenthesis(token) -> {
                parseNormalLet(line, column)
            }

            token is Identifier && !isReservedWord(token.value) -> {
                parseNamedLet(line, column)
            }

            else -> throw ParseError("Invalid Let near ($line, $column)")
        }


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

    private fun parseCond(line: Int, column: Int): Expression {
        fun buildIf(preds: MutableList<Expression>, values: MutableList<Expression>, default: Expression): Expression {
            if (preds.isEmpty() && values.isEmpty()) {
                return default
            }
            var index = preds.size - 1
            var innerIf = If(preds[index], values[index], default, line, column)
            while (index > 0) {
                index--
                innerIf = If(preds[index], values[index], innerIf, line, column)
            }
            return innerIf
        }

        fun buildIf(preds: MutableList<Expression>, values: MutableList<Expression>): Expression {
            // TODO: what is the ALTER of last IF?
            if (preds.isEmpty() && values.isEmpty()) {
                return Quote.NIL
            }
            return buildIf(preds, values, Quote.NIL)
        }

        val preds = mutableListOf<Expression>()
        val values = mutableListOf<Expression>()

        var peek = lexer.peekToken()
        while (!isRightParenthesis(peek)) {
            shouldBeLeftParenthesis(lexer.nextToken())
            val next = lexer.peekToken()
            if (next is Identifier && next.value == "else") {
                lexer.nextToken()
                val default = parseExpr()
                shouldBeRightParenthesis(lexer.nextToken())
                return buildIf(preds, values, default)
            } else {
                preds.add(parseExpr())
                values.add(parseExpr())
                shouldBeRightParenthesis(lexer.nextToken())
            }
            peek = lexer.peekToken()
        }
        return buildIf(preds, values)
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