package com.github.std.kacket.parse

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

        val expected = "(define fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))"
        assertEquals(expected, expr.toString())
    }

    @Test
    fun parseExpr1() {
        val code = "(define (fib n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))"
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        val expr = parser.parseExpr()

        val expected = "(define fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))"
        assertEquals(expected, expr.toString())
    }

    @Test
    fun parseExpr2() {
        val code = "(define (fib n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))"
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        val expr = parser.parseExpr()

        val expected = "(define fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))"
        assertEquals(expected, expr.toString())
    }

    @Test
    fun parseExpr3() {
        val code = "(let ((fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2))))))) (fib 10))"
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        val expr = parser.parseExpr()

        val expected = "(let ([fib (lambda (n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2)))))]) (fib 10))"
        assertEquals(expected, expr.toString())
    }

    @Test
    fun parseExpr5() {
        val code = "(let ((a 1) (b 'sym) (c \"hello\") (d #t) (e #f) (g #\\a)) a)"
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        val expr = parser.parseExpr()

        val expected = "(let ([a 1][b sym][c hello][d #t][e #f][g a]) a)"
        assertEquals(expected, expr.toString())
    }

    @Test
    fun parseExpr6() {
        val code =
            """
        (define (fib n) (if (< n 2) n (+ fib (- n 1) (fib (- n 2)))))
        (define (fib-iter i n fst snd) (if (= i n) snd (fib-iter (+ i 1) n snd (+ fst snd))))
        """
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        var expr0 = parser.parseExpr()
        var expr1 = parser.parseExpr()

    }

    @Test
    fun parseExpr7() {
        val code = """
            (let ((a 1) (b 2))
              (let ((c 3))
                (+ a b c)))
            (define x 10)
        """.trimIndent()
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        val expr0 = parser.parseExpr()
        println(expr0)
        val expr1 = parser.parseExpr()
        println(expr1)
    }
}