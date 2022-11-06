package com.github.std.kacket.analysis

fun arityAny(): (Int) -> Boolean = { _ -> true }

fun arityEqual(expected: Int): (Int) -> Boolean =
    { actual -> actual == expected }

fun arityGreaterEqual(expected: Int): (Int) -> Boolean =
    { actual -> actual >= expected }

