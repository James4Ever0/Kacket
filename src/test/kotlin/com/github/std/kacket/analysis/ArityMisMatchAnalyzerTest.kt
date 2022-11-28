package com.github.std.kacket.analysis

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

internal class ArityMisMatchAnalyzerTest {
    @Test
    @Disabled
    fun analyze0() {
        val code =
            """
       (define (accumulate op initial sequence)
               (if (null? sequence)
                   initial
                   (op (car sequence)
                       (accumulate op
                                   initial
                                   (cdr sequence)))))

        (define (map p sequence)
            (accumulate (lambda (x y) (cons (p x) y))
                nil sequence))

        (define (append seq1 seq2)
            (accumulate cons seq2 seq1))

        (define (length sequence)
            (accumulate (lambda (x y) (+ y 1)) 0 sequence))
        (length (list 1 2 3 4 7 5))
        """
        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val analyzer = ArityMisMatchAnalyzer(input)
    }

    @Test
    fun analyze1() {
        val code =
            """
        (define (fib n) (if (< n 2) n (+ fib (- n 1) (fib (- n 2)))))
        (define (fib-iter i n fst snd) (if (= i n) snd (fib-iter (+ i 1) n snd (+ fst snd))))
        """
        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val analyzer = ArityMisMatchAnalyzer(input)
    }
    @Test
    fun analyze2() {
        val code =
            """
        (define (fib n) 
          (if (< n 2) 
              n 
              (+ fib (- n 1) 
                 (fib 114514 (- n 2)))))
        """
        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val analyzer = ArityMisMatchAnalyzer(input)
    }
    @Test
    fun analyze3() {
        val code =
            """
        (let ((foo (lambda (bar) 
                        (bar bar))))
             (foo 114 514))
        """
        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val analyzer = ArityMisMatchAnalyzer(input)
    }

}