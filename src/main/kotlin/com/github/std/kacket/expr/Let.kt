package com.github.std.kacket.expr

class Let(
    val variables: List<String>,
    val values: List<Expression>,
    val body: List<Expression>
) : Expression {
}