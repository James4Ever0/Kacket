package com.github.std.kacket.parse.exten

import com.github.std.kacket.expr.Quote
import com.github.std.kacket.expr.exten.Cases
import com.github.std.kacket.expr.exten.DefineDatatype
import com.github.std.kacket.expr.exten.ExtExpr
import com.github.std.kacket.parse.Identifier
import com.github.std.kacket.parse.Lexer
import com.github.std.kacket.parse.Parser

object CasesParser : SExprExtParser {
    override fun start(): String = "cases"

    override fun parse(lexer: Lexer, line: Int, col: Int, root: Parser): ExtExpr {
        val typeNameToken = lexer.nextToken()
        root.shouldBeNameToken(typeNameToken)
        val typeName = (typeNameToken as Identifier).value

        val case = root.parseExpr()

        val variants = mutableListOf<Cases.Variant>()
        while (!(root.isRightParenthesis(lexer.peekToken()))) {
            root.shouldBeLeftParenthesis(lexer.nextToken())

            val next = lexer.nextToken()
            if (next is Identifier && next.value == "else") {
                val default = root.parseExpr()
                root.shouldBeRightParenthesis(lexer.nextToken())
                return Cases(typeName, case, variants, default, line, col)
            } else {
                root.shouldBeNameToken(next)
                val variName = (next as Identifier).value

                val fields = mutableListOf<String>()
                root.shouldBeLeftParenthesis(lexer.nextToken())
                while (!root.isRightParenthesis(lexer.peekToken())) {
                    val nameToken = lexer.nextToken()
                    root.shouldBeNameToken(nameToken)
                    val fieldName = (nameToken as Identifier).value
                    fields.add(fieldName)
                }
                root.shouldBeRightParenthesis(lexer.nextToken())

                val conseq = root.parseExpr()

                root.shouldBeRightParenthesis(lexer.nextToken())

                variants.add(Cases.Variant(variName, fields, conseq, next.lineNumber(), next.columnNumber()))
            }
        }

        return Cases(typeName, case, variants, Quote.NIL, line, col)
    }
}