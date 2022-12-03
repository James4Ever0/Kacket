 (define (fib n)
           (if (< n 2)
               n
               (+ fib (- n 1)
                  (fib 114514 (- n 2)))))

 (let ((foo (lambda (bar)
                         (bar bar))))
              (foo 114 514))
 (let ((foo '(a b c))
       (bar #t))
    (bar 12)
    (foo 114 514))

 ((lambda (x) x) 114 514)

 ('(a b c) 114 514)

 ((define a 10) 10)

 (letrec ([foo '(a b (c))]
           [bar (lambda (x) (bar x))]
           [error (lambda (x) (error x 114 514))])
   (bar 114 514)
   (error 114514))


