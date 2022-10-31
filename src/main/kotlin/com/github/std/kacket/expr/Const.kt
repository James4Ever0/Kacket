package com.github.std.kacket.expr

import com.github.std.kacket.parse.Token

class Const(
    private val value: Token
) : Expression {
}