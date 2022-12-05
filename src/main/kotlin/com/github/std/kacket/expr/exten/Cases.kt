package com.github.std.kacket.expr.exten

import com.github.std.kacket.expr.Expression

class Cases(
    val typeName: String,
    val case: Expression,
    val variants: List<Variant>,
    val default: Expression,
    private val line: Int,
    private val col: Int
) : ExtExpr {
    class Variant(
        val name: String,
        val fields: List<String>,
        val conseq: Expression,
        val line: Int,
        val col: Int
    ) {
        override fun toString(): String {
            val builder = StringBuilder("[ $name (")
            for (field in fields) {
                builder.append(field)
                builder.append(" ")
            }
            if (fields.isNotEmpty()) {
                builder.setLength(builder.length - 1)
            }
            builder.append(")")
            builder.append(conseq.toString())
            return builder.toString()
        }
    }

    override fun lineNumber(): Int = line

    override fun columnNumber(): Int = col
    override fun toString(): String {
        val builder = StringBuilder("(cases $typeName $case ")
        for (variant in variants) {
            builder.append(variant.toString())
        }
        builder.append("[else $default])")
        return builder.toString()
    }
}