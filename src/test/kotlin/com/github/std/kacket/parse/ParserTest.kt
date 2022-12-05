package com.github.std.kacket.parse

import com.github.std.kacket.parse.exten.DefineDatatypeParser
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import kotlin.math.exp

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
        val expr0 = parser.parseExpr()
        val expected0 = "(define fib (lambda (n) (if (< n 2) n (+ fib (- n 1) (fib (- n 2))))))"
        assertEquals(expected0, expr0.toString())

        val expr1 = parser.parseExpr()
        val expected1 = "(define fib-iter (lambda (i n fst snd) (if (= i n) snd (fib-iter (+ i 1) n snd (+ fst snd)))))"
        assertEquals(expected1, expr1.toString())

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
        val expected0 = "(let ([a 1][b 2]) (let ([c 3]) (+ a b c)))"
        assertEquals(expected0, expr0.toString())

        val expr1 = parser.parseExpr()
        val expected1 = "(define x 10)"
        assertEquals(expected1, expr1.toString())
    }

    @Test
    fun parseExpr8() {
        val code = """
            '(a b c)
            '(a (b c))
            '(a 'b c)
            '(#t 3 '(b c))
            '()
        """.trimIndent()
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))

        val expr0 = parser.parseExpr()
        val expected0 = "'(a b c)"
        assertEquals(expected0, expr0.toString())

        val expr1 = parser.parseExpr()
        val expected1 = "'(a (b c))"
        assertEquals(expected1, expr1.toString())

        val expr2 = parser.parseExpr()
        val expected2 = "'(a 'b c)"
        assertEquals(expected2, expr2.toString())

        val expr3 = parser.parseExpr()
        val expected3 = "'(#t 3 '(b c))"
        assertEquals(expected3, expr3.toString())

        val expr4 = parser.parseExpr()
        val expected4 = "'()"
        assertEquals(expected4, expr4.toString())
    }

    @Test
    fun parseExpr9() {
        val code =
            """
        (let ((foo '(a b c))
              (bar #t))
             (bar 12)
             (foo 114 514))
        """
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))

        val expr0 = parser.parseExpr()
        val expected0 = "(let ([foo '(a b c)][bar #t]) (bar 12)(foo 114 514))"
        assertEquals(expected0, expr0.toString())
    }

    @Test
    @Disabled
    fun parseExpr10() {
        val code = " ('(a b c) 114 514) "
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))

        parser.parseExpr()
    }

    @Test
    fun parseExpr11() {
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
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))

        val expr0 = parser.parseExpr()
        val expected0 =
            "(letrec ([loop (lambda (lst cnt) (if (null? lst) cnt (let ([fst (car lst)][rest (cdr lst)]) (if (eqv? fst a) (loop 114 rest (+ cnt 1)) (loop 514 rest cnt)))))]) (loop '(a b c) 0))"
        assertEquals(expected0, expr0.toString())

    }

    @Test
    fun parseExpr12() {
        val code = """
            (let* ([foo '(a b c)]
                   [f1 (lambda (x) x)]
                   [f2 (f1 foo)])
               (f2 114 514))
               
            (let* () 114514)
        """.trimIndent()
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))

        val expr0 = parser.parseExpr()
        val expected0 = "(let ([foo '(a b c)]) (let ([f1 (lambda (x) x)]) (let ([f2 (f1 foo)]) (f2 114 514))))"
        assertEquals(expected0, expr0.toString())


        val expr1 = parser.parseExpr()
        val expected1 = "(let () 114514)"
        assertEquals(expected1, expr1.toString())
    }

    @Test
    fun parseExpr13() {
        val code = """
             (cond [(foo1) bar1] [foo2 bar2] [else bar3])
             (cond [(foo1) bar1] [foo2 bar2])
             (cond) 
             (cond [else 2])
        """.trimIndent()
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))

        val expr0 = parser.parseExpr()
        val expected0 = "(if (foo1) bar1 (if foo2 bar2 bar3))"
        assertEquals(expected0, expr0.toString())

        val expr1 = parser.parseExpr()
        val expected1 = "(if (foo1) bar1 (if foo2 bar2 '()))"
        assertEquals(expected1, expr1.toString())

        val expr2 = parser.parseExpr()
        val expected2 = "'()"
        assertEquals(expected2, expr2.toString())

        val expr3 = parser.parseExpr()
        val expected3 = "2"
        assertEquals(expected3, expr3.toString())
    }

    @Test
    fun parseExpr14() {
        val code = """
            (let ([foo '(a b 9 (c d))]
                  [bar (lambda (x) x)])
              (begin 
                 (bar)
                 (foo)))
        """.trimIndent()

        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))

        val expr0 = parser.parseExpr()
        val expected0 = "(let ([foo '(a b 9 (c d))][bar (lambda (x) x)]) (begin (bar)(foo)))"
        assertEquals(expected0, expr0.toString())
    }

    @Test
    fun parseExpr15() {
        val code = """
            (define-datatype expression expression?
               (const-exp
                (num number?))
               (if-exp
                (exp1 expression?)
                (exp2 expression?)
                (exp3 expression?))
               (zero?-exp
                (exp1 expression?))
               (var-exp
                (var identifier?))
               (diff-exp
                (exp1 expression?)
                (exp2 expression?))
               (let-exp
                (var  identifier?)
                (exp  expression?)
                (body expression?))
               (letrec-exp
                (p-name identifier?)
                (b-var identifier?)
                (p-body expression?)
                (letrec-body expression?))
               (proc-exp
                (var identifier?)
                (body expression?))
               (call-exp
                (rator expression?)
                (rand expression?))
               )
        """.trimIndent()
        val parser = Parser(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
        parser.addSExprExt(DefineDatatypeParser)

        val expected0 =
            "(define-datatype expression expression? [const-exp (num number?)][if-exp (exp1 expression?)(exp2 expression?)(exp3 expression?)][zero?-exp (exp1 expression?)][var-exp (var identifier?)][diff-exp (exp1 expression?)(exp2 expression?)][let-exp (var identifier?)(exp expression?)(body expression?)][letrec-exp (p-name identifier?)(b-var identifier?)(p-body expression?)(letrec-body expression?)][proc-exp (var identifier?)(body expression?)][call-exp (rator expression?)(rand expression?)])"
        val expr0 = parser.parseExpr()
        assertEquals(expected0, expr0.toString())
    }
}