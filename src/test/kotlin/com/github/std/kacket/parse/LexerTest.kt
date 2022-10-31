package com.github.std.kacket.parse

import org.junit.jupiter.api.Test

import java.io.ByteArrayInputStream
import java.io.InputStreamReader

class LexerTest {

    @Test
    fun nextToken1() {
        val code = "(define (fib n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2)))))"
        val lexer = Lexer(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        var token = lexer.nextToken()
        while (token !is EOF) {
            println(token)
            token = lexer.nextToken()
        }
    }

    @Test
    fun nextToken2() {
        val code = """(define bool #f)
            (define char #\a)
            (define text "hello") ; comment
            ;;; comment
            (define symbol 'sym)
            (define number 114.514)
        """

        val lexer = Lexer(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        var token = lexer.nextToken()
        while (token !is EOF) {
            println(token)
            token = lexer.nextToken()
        }
    }
}