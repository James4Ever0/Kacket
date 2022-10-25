package com.github.std.kacket.parse

import org.apache.commons.lang3.CharUtils.isAsciiPrintable
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder

class Lexer(input: InputStreamReader) {
    private val reader = BufferedReader(input)
    private val tokenBuffer = mutableListOf<Token>()
    private var columnNum = 0
    private var lineNum = 0

    companion object {
        const val BUFFER_SIZE = 50
    }

    private fun lex() {

        var tokenCount = 0
        var read = reader.read()
        if (read == -1) {
            tokenBuffer.add(EOF(lineNum, columnNum))
            return
        }
        while (read != -1 && tokenCount < BUFFER_SIZE) {
            val char = read.toChar()
            val (readNext, count) = when {
                char == '\n' -> {
                    columnNum = 0
                    lineNum++
                    reader.read() to 0
                }

                char.isWhitespace() -> {
                    reader.read() to 1
                }

                isParenthesis(char) -> {
                    tokenCount++
                    lexParenthesis(char)
                }

                isIdentifierStart(char) -> {
                    tokenCount++
                    lexIdentifier(char)
                }

                isCharStart(char) -> {
                    tokenCount++
                    lexChar()
                }

                isStringStart(char) -> {
                    tokenCount++
                    lexString(char)
                }

                isNumberStart(char) -> {
                    tokenCount++
                    lexNumber(char)
                }

                isSymbolStart(char) -> {
                    tokenCount++
                    lexSymbol(char)
                }

                else -> {
                    throw RuntimeException("Unknown Token at ($lineNum, $columnNum)")
                }
            }
            read = readNext
            columnNum += count
        }
    }

    private fun lexSymbol(first: Char): Pair<Int, Int> {
        fun isSymbolChar(char: Char): Boolean =
            !char.isWhitespace() && isAsciiPrintable(char)

        var read = reader.read()
        var count = 0
        while (read != -1 && isSymbolChar(read.toChar())) {
            count++
            read = reader.read()
        }
        tokenBuffer.add(NotInterested(lineNum, columnNum))
        return read to count
    }

    private fun isSymbolStart(char: Char): Boolean = char == '\''

    // (read, count)
    private fun lexNumber(first: Char): Pair<Int, Int> {
        var count = 0
        var read = first.code
        while (read != -1 && read.toChar().isDigit()) {
            count++
            read = reader.read()
        }
        if (read.toChar() == '.') {
            count++
            read = reader.read()
        }
        while (read.toChar().isDigit()) {
            count++
            read = reader.read()
        }
        tokenBuffer.add(NotInterested(lineNum, columnNum))
        return read to count
    }

    private fun isNumberStart(char: Char): Boolean = char.isDigit()
    private fun isParenthesis(char: Char): Boolean =
        char == '(' || char == ')' || char == '[' || char == ']'

    private fun lexParenthesis(first: Char): Pair<Int, Int> {
        tokenBuffer.add(Parenthesis(first, lineNum, columnNum))
        return reader.read() to 1
    }

    private fun lexString(first: Char): Pair<Int, Int> {
        // TODO: escape characters
        var read = reader.read()
        var count = 0
        while (read != -1 && read.toChar() != '\"') {
            read = reader.read()
            count++
        }
        tokenBuffer.add(NotInterested(lineNum, columnNum))
        return read to count
    }

    private fun isStringStart(char: Char): Boolean = char == '\"'

    private fun lexChar(): Pair<Int, Int> {
        reader.read()
        tokenBuffer.add(NotInterested(lineNum, columnNum))
        return reader.read() to 1
    }

    private fun isCharStart(char: Char): Boolean {
        if (char == '#') {
            reader.mark(1)
            if (reader.read().toChar() == '\\') {
                return true
            }
            reader.reset()
            return false
        }
        return false
    }

    private fun lexIdentifier(first: Char): Pair<Int, Int> {
        fun isIdentifierChar(char: Char): Boolean =
            !char.isWhitespace() && isAsciiPrintable(char)

        var read = first.code
        var count = 0
        val builder = StringBuilder()
        while (read != 1 && isIdentifierChar(read.toChar())) {
            count++
            builder.append(read.toChar())
            read = reader.read()
        }
        tokenBuffer.add(Identifier(builder.toString(), lineNum, columnNum))
        return read to count
    }

    // TODO: check standard
    private fun isIdentifierStart(char: Char): Boolean =
        !char.isWhitespace() && !char.isDigit() && isAsciiPrintable(char)


    fun nextToken(): Token {
        if (tokenBuffer.isEmpty()) {
            lex()
        }
        return tokenBuffer.removeFirst()
    }
}