package com.github.std.kacket.analysis

fun arityAny(): (Int) -> Unit = { }

fun arityEqual( expected: Int): (Int) -> Unit =
    { actual ->
        if (actual != expected) {
            throw AnalysisError("Arity Mismatch: expected:$expected, actual:$actual")
        }
    }

fun arityGreaterEqual(expected: Int): (Int) -> Unit =
    { actual ->
        if (actual < expected) {
            throw AnalysisError("Arity Mismatch: at least:$expected, got:$actual")
        }
    }


fun notProc(): (Int) -> Unit =
    { _ -> throw AnalysisError("Not a procedure") }
