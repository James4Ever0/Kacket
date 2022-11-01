package com.github.std.kacket.expr

class Call(
    proc: Expression, // Var, Procedure: known; If, Let: maybe; Call: unknown; Others: Error
    args: List<Expression>
) : Expression {
}