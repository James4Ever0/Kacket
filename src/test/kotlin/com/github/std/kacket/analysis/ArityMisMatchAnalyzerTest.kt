package com.github.std.kacket.analysis

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

internal class ArityMisMatchAnalyzerTest {
    @Test
    fun analyze() {
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
        val analyzer = ArityMisMatchAnalyzer(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        println(analyzer.initEnv.checkRule("map", 2))
    }
}