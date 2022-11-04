package com.github.std.kacket.parse

import com.github.std.kacket.expr.Let
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

internal class ParserTest {

    @Test
    fun parseExpr0() {
        val code = "(define fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))"
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        val expr = parser.parseExpr()
        println(expr)
    }

    @Test
    fun parseExpr1() {
        val code = "(define (fib n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))"
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        val expr = parser.parseExpr()
        println(expr)
    }

    @Test
    fun parseExpr2() {
        val code = "(define (fib n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))"
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        val expr = parser.parseExpr()
        println(expr)
    }

    @Test
    fun parseExpr3() {
        val code = "(let ((fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))) (fib 10))"
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        val expr = parser.parseExpr()
        println(expr.toString())
    }

    @Test
    fun parseExpr5() {
        val code = "(let ((a 1) (b 'sym) (c \"hello\") (d #t) (e #f) (g #\\a)) a)"
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        val expr = parser.parseExpr()
        println(expr.toString())
    }
}