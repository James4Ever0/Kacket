package com.github.std.kacket.parse

import org.apache.commons.lang3.CharUtils.isAsciiPrintable
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.lang.StringBuilder
import java.util.Deque
import java.util.LinkedList

class Lexer(input: Reader) {
    private val reader = BufferedReader(input)
    private val tokenBuffer = LinkedList<Token>()
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
        while (read != -1) {
            if (tokenCount > BUFFER_SIZE) {
                break
            }
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

                char == ';' -> {
                    lexComment()
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

                isBoolStart(char) -> {
                    tokenCount++
                    lexBool()
                }

                isStringStart(char) -> {
                    tokenCount++
                    lexString()
                }

                isNumberStart(char) -> {
                    tokenCount++
                    lexNumber(char)
                }

                isSymbolStart(char) -> {
                    tokenCount++
                    lexSymbol()
                }

                else -> {
                    throw LexError("Unknown Token at ($lineNum, $columnNum)")
                }
            }
            read = readNext
            columnNum += count
        }
    }

    private fun lexComment(): Pair<Int, Int> {
        var read = reader.read()
        while (read != -1 && read.toChar() != '\n') {
            read = reader.read()
        }
        return read to 0
    }

    private fun lexBool(): Pair<Int, Int> {
        val read = reader.read()
        val value = read.toChar() == 't'
        tokenBuffer.add(Bool(lineNum, columnNum, value))
        return reader.read() to 1
    }

    private fun isBoolStart(char: Char): Boolean {
        if (char == '#') {
            reader.mark(1)
            val next = reader.read().toChar()
            if (next == 'f' || next == 't') {
                reader.reset()
                return true
            }
            reader.reset()
            return false
        }
        return false
    }

    private fun lexSymbol(): Pair<Int, Int> {
        fun isSymbolChar(char: Char): Boolean =
            !isParenthesis(char) && !char.isWhitespace() && isAsciiPrintable(char)

        var read = reader.read()
        val builder = StringBuilder()
        var count = 0
        while (read != -1 && isSymbolChar(read.toChar())) {
            count++
            builder.append(read.toChar().toString())
            read = reader.read()
        }
        if (count == 0) {
            throw LexError("Bad Token at ($lineNum, $columnNum)")
        }
        tokenBuffer.add(Symbol(lineNum, columnNum, builder.toString()))
        return read to count
    }

    private fun isSymbolStart(char: Char): Boolean = char == '\''

    // (read, count)
    private fun lexNumber(first: Char): Pair<Int, Int> {
        var count = 0
        var read = first.code
        val builder = StringBuilder()
        while (read != -1 && read.toChar().isDigit()) {
            count++
            builder.append(read.toChar().toString())
            read = reader.read()
        }
        if (read.toChar() == '.') {
            count++
            builder.append(".")
            read = reader.read()
        }
        while (read.toChar().isDigit()) {
            count++
            builder.append(read.toChar().toString())
            read = reader.read()
        }
        tokenBuffer.add(Num(lineNum, columnNum, builder.toString()))
        return read to count
    }

    private fun isNumberStart(char: Char): Boolean = char.isDigit()
    private fun isParenthesis(char: Char): Boolean =
        char == '(' || char == ')' || char == '[' || char == ']'

    private fun lexParenthesis(first: Char): Pair<Int, Int> {
        tokenBuffer.add(Parenthesis(first, lineNum, columnNum))
        return reader.read() to 1
    }

    private fun lexString(): Pair<Int, Int> {
        // TODO: escape characters
        var read = reader.read()
        val builder = StringBuilder()
        var count = 0
        while (read != -1 && read.toChar() != '\"') {
            builder.append(read.toChar().toString())
            read = reader.read()
            count++
        }
        read = reader.read()
        tokenBuffer.add(Text(lineNum, columnNum, builder.toString()))
        return read to count + 2
    }

    private fun isStringStart(char: Char): Boolean = char == '\"'

    private fun lexChar(): Pair<Int, Int> {
        val value = reader.read().toChar()
        tokenBuffer.add(Character(lineNum, columnNum, value))
        return reader.read() to 3
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
            !isParenthesis(char) && !char.isWhitespace() && isAsciiPrintable(char)

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
        char != '\'' &&
                char != '#' &&
                char != '"' &&
                !char.isWhitespace() &&
                !char.isDigit() &&
                isAsciiPrintable(char)


    fun nextToken(): Token {
        if (tokenBuffer.isEmpty()) {
            lex()
        }
        if (tokenBuffer.peek() is EOF) {
            reader.close()
            return EOF(lineNum, columnNum)
        }
        return tokenBuffer.removeFirst()
    }

    fun peekToken(): Token {
        if (tokenBuffer.isEmpty()) {
            lex()
        }
        return tokenBuffer.peek()
    }
}