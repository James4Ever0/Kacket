package com.github.std.kacket.expr

import com.github.std.kacket.parse.*


class Quote(
    private val line: Int,
    private val column: Int,
    private val elements: List<QuoteElement>
) : Expression {
    companion object {
        val NIL = Quote(-1, -1, listOf())
    }

    interface QuoteElement
    class ElementConst(private val token: Token) : QuoteElement {
        override fun toString(): String = when {
            token is Text -> {
                token.value
            }

            token is Num -> {
                token.value
            }

            token is Identifier -> {
                token.value
            }

            token is Symbol -> {
                "'${token.value}"
            }

            token is Character -> {
                token.value.toString()
            }

            token is Bool && token.value -> {
                "#t"
            }

            token is Bool && !token.value -> {
                "#f"
            }

            else -> throw ExprError()
        }

    }

    class ElementQuote(private val quote: Quote) : QuoteElement {
        override fun toString(): String {
            return quote.toString()
        }
    }

    class ElementQuotes(private val elements: List<QuoteElement>) : QuoteElement {
        override fun toString(): String {
            val builder = StringBuilder("(")
            for (element in elements) {
                builder.append(element.toString())
                builder.append(' ')
            }
            if (elements.isNotEmpty()) {
                builder.setLength(builder.length - 1)
            }
            builder.append(")")
            return builder.toString()
        }
    }

    override fun lineNumber(): Int = line

    override fun columnNumber(): Int = column

    override fun toString(): String {
        val builder = StringBuilder("'(")
        for (element in elements) {
            builder.append(element.toString())
            builder.append(' ')
        }
        if (elements.isNotEmpty()) {
            builder.setLength(builder.length - 1)
        }
        builder.append(")")
        return builder.toString()
    }
}