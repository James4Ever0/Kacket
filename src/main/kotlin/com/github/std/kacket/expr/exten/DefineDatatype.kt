package com.github.std.kacket.expr.exten

import com.github.std.kacket.analysis.ProcEnv
import com.github.std.kacket.expr.Expression

class DefineDatatype(
    val typeName: String,
    val predName: String,
    val variants: List<Variant>,
    private val line: Int,
    private val col: Int
) : ExtExpr {
    class Variant(
        val name: String,
        val fields: Map<String, Expression>
    ) {
        override fun toString(): String {
            val builder = StringBuilder("[$name ")
            for ((fieldName, expr) in fields) {
                builder.append("($fieldName ")
                builder.append(expr.toString())
                builder.append(")")
            }
            builder.append("]")
            return builder.toString()
        }
    }

    override fun lineNumber(): Int = line

    override fun columnNumber(): Int = col
    override fun toString(): String {
        val builder = StringBuilder("(define-datatype $typeName $predName ")
        for (variant in variants) {
            builder.append(variant.toString())
        }
        builder.append(")")
        return builder.toString()
    }
}