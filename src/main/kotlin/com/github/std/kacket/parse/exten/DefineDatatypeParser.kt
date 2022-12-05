package com.github.std.kacket.parse.exten

import com.github.std.kacket.expr.Expression
import com.github.std.kacket.expr.exten.DefineDatatype
import com.github.std.kacket.expr.exten.ExtExpr
import com.github.std.kacket.parse.*

object DefineDatatypeParser : SExprExtParser {
    override fun start(): String = "define-datatype"

    override fun parse(lexer: Lexer, line: Int, col: Int, root: Parser): DefineDatatype {
        val typeNameToken = lexer.nextToken()
        root.shouldBeNameToken(typeNameToken)
        val typeName = (typeNameToken as Identifier).value

        val predNameToken = lexer.nextToken()
        root.shouldBeNameToken(predNameToken)
        val predName = (predNameToken as Identifier).value

        val variants = parseVariants(lexer, root)
        return DefineDatatype(typeName, predName, variants, line, col)
    }

    private fun parseVariants(lexer: Lexer, root: Parser): List<DefineDatatype.Variant> {
        val variants = mutableListOf<DefineDatatype.Variant>()
        while (!(root.isRightParenthesis(lexer.peekToken()))) {
            root.shouldBeLeftParenthesis(lexer.nextToken())

            val variNameToken = lexer.nextToken()
            root.shouldBeNameToken(variNameToken)
            val variName = (variNameToken as Identifier).value

            val fields = parseVariantFields(lexer, root)

            variants.add(DefineDatatype.Variant(variName, fields))

            root.shouldBeRightParenthesis(lexer.nextToken())
        }
        return variants
    }

    private fun parseVariantFields(lexer: Lexer, root: Parser): Map<String, Expression> {
        val fields = mutableMapOf<String, Expression>()
        while (!root.isRightParenthesis(lexer.peekToken())) {
            root.shouldBeLeftParenthesis(lexer.nextToken())

            val fieldNameToken = lexer.nextToken()
            root.shouldBeNameToken(fieldNameToken)
            val fieldName = (fieldNameToken as Identifier).value

            val pred = root.parseExpr()

            fields[fieldName] = pred

            root.shouldBeRightParenthesis(lexer.nextToken())
        }
        return fields
    }
}