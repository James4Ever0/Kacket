// (let ((x 5) (y 4))
//   (+ x y))

((x, y) => x + y)(5, 4)

// (letrec ((fact
//          (lambda (n)
//             (if (= n 0)
//                 1
//                 (* n (fact (- n 1)))))))
//     (fact 10))

// ((fact) => fact(10))((n) => {
//     if (n === 0) 
//     return 1
//     else return fact(n - 1) * n
// })

((fact) => fact(10, fact))((n, fact) => {
    if (n === 0) 
    return 1
    else return fact(n - 1, fact) * n
})

// (let ((fact
//          (lambda (n fact)
//             (if (= n 0)
//                 1
//                 (* n (fact (- n 1) fact))))))
//     (fact 10 fact))