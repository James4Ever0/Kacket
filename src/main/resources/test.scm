 (define (fib n)
          (if (< n 2)
              n
              (+ fib (- n 1)
                 (fib 114514 (- n 2)))))

(let ((foo (lambda (bar)
                        (bar bar))))
             (foo 114 514))
