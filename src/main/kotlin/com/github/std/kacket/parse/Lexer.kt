package com.github.std.kacket.parse

import org.apache.commons.lang3.CharUtils.isAsciiPrintable
import java.io.BufferedReader
import java.io.Reader
import java.lang.StringBuilder
import java.util.LinkedList

const val BUFFER_SIZE = 50

class Lexer(input: Reader) {
    private val reader = BufferedReader(input)
    private val tokenBuffer = LinkedList<Token>()
    private var columnNum = 1
    private var lineNum = 1
    private var read = reader.read()


    private fun close() {
        tokenBuffer.add(EOF(lineNum, columnNum))
        reader.close()

    }

    private fun lex() {
        for (tokenCount in 0..BUFFER_SIZE) {
            if (read == -1) {
                close()
                return
            }
            val char = read.toChar()
            val (readNext, count) = when {
                char == '\n' -> {
                    columnNum = 1
                    lineNum++
                    reader.read() to 0
                }

                char.isWhitespace() -> {
                    reader.read() to 1
                }

                char == ';' -> {
                    lexComment()
                }

                isIdentifierStart(char) -> {
                    lexIdentifier(char)
                }

                isCharStart(char) -> {
                    lexChar()
                }

                isBoolStart(char) -> {
                    lexBool()
                }

                isStringStart(char) -> {
                    lexString()
                }

                isNumberStart(char) -> {
                    lexNumber(char)
                }

                isSymbolStart(char) -> {
                    lexSymbol()
                }

                // should be after isSymbolStart
                isPunctuation(char) -> {
                    lexPunctuation(char)
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

    private fun isSymbolChar(char: Char): Boolean =
        !isPunctuation(char) && !char.isWhitespace() && isAsciiPrintable(char)

    private fun lexSymbol(): Pair<Int, Int> {
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

    private fun isSymbolStart(first: Char): Boolean {
        reader.mark(1)
        val next = reader.read().toChar()
        reader.reset()

        if (first == '\'' && isSymbolChar(next)) {
            return true
        }
        return false
    }

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

    private fun isPunctuation(char: Char): Boolean =
        char == '\'' ||
                char == '#' ||
                char == '"' ||
                isParenthesis(char)

    private fun isNumberStart(char: Char): Boolean = char.isDigit()
    private fun isParenthesis(char: Char): Boolean =
        char == '(' || char == ')' || char == '[' || char == ']'

    private fun lexPunctuation(first: Char): Pair<Int, Int> {
        tokenBuffer.add(Punctuation(first, lineNum, columnNum))
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
            !isPunctuation(char) && !char.isWhitespace() && isAsciiPrintable(char)

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
        !isPunctuation(char) &&
                !char.isDigit() &&
                isAsciiPrintable(char)


    fun nextToken(): Token {
        if (tokenBuffer.isEmpty()) {
            lex()
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