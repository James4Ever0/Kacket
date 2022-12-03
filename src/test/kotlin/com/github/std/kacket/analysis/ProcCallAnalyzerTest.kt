package com.github.std.kacket.analysis

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

internal class ProcCallAnalyzerTest {
    @Test
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
        val analyzer = ProcCallAnalyzer(input)
    }

    @Test
    fun analyze1() {
        val code =
            """
        (define (fib n) (if (< n 2) n (+ fib (- n 1) (fib (- n 2)))))
        (define (fib-iter i n fst snd) (if (= i n) snd (fib-iter (+ i 1) n snd (+ fst snd))))
        """
        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val analyzer = ProcCallAnalyzer(input)
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
        val analyzer = ProcCallAnalyzer(input)
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
        val analyzer = ProcCallAnalyzer(input)
    }

    @Test
    fun analyze4() {
        val code =
            """
        (let ((foo '(a b c))
              (bar #t))
              (bar 12)
             (foo 114 514))
        
        ((lambda (x) x) 114 514)
        
        """
        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val analyzer = ProcCallAnalyzer(input)
    }
    @Test
    fun analyze5() {
        val code =
            """
        (letrec ([foo '(a b (c))]
                 [bar (lambda (x) (bar x))]
                 [error (lambda (x) (error x 114 514))])
           (bar 114 514)
           (error 114514))
        """
        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val analyzer = ProcCallAnalyzer(input)
    }

    @Test
    fun analyze6() {
        val code = """
                 (let loop ([lst '(a b c)]
                       [cnt 0])
                (if (null? lst)
                    cnt
                    (let ([fst (car lst)]
                          [rest (cdr lst)])
                      (if (eqv? fst 'a)
                          (loop 114 rest (+ cnt 1))
                          (loop 514 rest cnt)))))
        """.trimIndent()
        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val analyzer = ProcCallAnalyzer(input)
    }
}