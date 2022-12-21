package com.github.std.kacket.analysis

import com.github.std.kacket.analysis.exten.CasesAnalyzer
import com.github.std.kacket.analysis.exten.DefineDatatypeAnalyzer
import com.github.std.kacket.parse.Lexer
import com.github.std.kacket.parse.Parser
import com.github.std.kacket.parse.exten.CasesParser
import com.github.std.kacket.parse.exten.DefineDatatypeParser
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
        val lexer = Lexer(input)
        val parser = Parser(lexer)
        val analyzer = ProcCallAnalyzer(parser)
        analyzer.analyzeProgram()
    }

    @Test
    fun analyze1() {
        val code =
            """
        (define (fib n) (if (< n 2) n (+ fib (- n 1) (fib (- n 2)))))
        (define (fib-iter i n fst snd) (if (= i n) snd (fib-iter (+ i 1) n snd (+ fst snd))))
        """
        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val lexer = Lexer(input)
        val parser = Parser(lexer)
        val analyzer = ProcCallAnalyzer(parser)
        analyzer.analyzeProgram()
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
        val lexer = Lexer(input)
        val parser = Parser(lexer)
        val analyzer = ProcCallAnalyzer(parser)
        analyzer.analyzeProgram()
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
        val lexer = Lexer(input)
        val parser = Parser(lexer)
        val analyzer = ProcCallAnalyzer(parser)
        analyzer.analyzeProgram()
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
        val lexer = Lexer(input)
        val parser = Parser(lexer)
        val analyzer = ProcCallAnalyzer(parser)
        analyzer.analyzeProgram()
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
        val lexer = Lexer(input)
        val parser = Parser(lexer)
        val analyzer = ProcCallAnalyzer(parser)
        analyzer.analyzeProgram()
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
        val lexer = Lexer(input)
        val parser = Parser(lexer)
        val analyzer = ProcCallAnalyzer(parser)
        analyzer.analyzeProgram()
    }

    @Test
    fun analyze7() {
        val code = """
            (let ([foo '(a b 9 (c d))]
                  [bar (lambda (x) x)])
              (begin 
                 (bar)
                 (foo)))
        """.trimIndent()
        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val lexer = Lexer(input)
        val parser = Parser(lexer)
        val analyzer = ProcCallAnalyzer(parser)
        analyzer.analyzeProgram()
    }

    @Test
    fun analyze8() {
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
                (body (lambda(x y) (expression? x))))
               (call-exp
                (rator expression?)
                (rand expression?))
               )
          (define identifier?
             (lambda (exp)
               (and (symbol? exp)
                    (not (eqv? exp 'lambda)))))
          
          (call-exp 114 514 114514)
        """.trimIndent()

        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val lexer = Lexer(input)
        val parser = Parser(lexer)
        parser.addSExprExt(DefineDatatypeParser)

        val analyzer = ProcCallAnalyzer(parser)
        analyzer.addExtAnalyzer(DefineDatatypeAnalyzer)

        analyzer.analyzeProgram()
    }

    @Test
    fun analyze9() {
        val code = """
            (define-datatype Tree tree?
              (BinTree (val number?)
                       (left tree?)
                       (right tree?))
              (Empty))
              
            (define t0 (BinTree 11 (BinTree 4 (Empty) (Empty))
                                   514
                                   (BinTree 514 (Empty) (Empty))))
            
            (define sum 
               (lambda (tree)
                 (cases Tree tree
                   (Empty (foo bar) 0)
                   (BinTree (val left right)
                     (+ val (sum left) (sum right))))))
        """.trimIndent()

        val input = InputStreamReader(ByteArrayInputStream(code.toByteArray()))
        val lexer = Lexer(input)
        val parser = Parser(lexer)

        parser.addSExprExt(DefineDatatypeParser)
        parser.addSExprExt(CasesParser)

        val analyzer = ProcCallAnalyzer(parser)
        analyzer.addExtAnalyzer(DefineDatatypeAnalyzer)
        analyzer.addExtAnalyzer(CasesAnalyzer)

        analyzer.analyzeProgram()
    }
}