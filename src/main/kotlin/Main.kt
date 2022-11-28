import com.github.std.kacket.analysis.ArityMisMatchAnalyzer
import com.github.std.kacket.parse.EOF
import com.github.std.kacket.parse.Lexer
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStreamReader
import java.util.Arrays

private fun help() {
    val usage = """
        Usage: kacket <options> <source files>
        where possible options include:
        --amc           arity mismatch check
    """.trimIndent()
    println(usage)
}

private fun arityMismatchCheck(source: String) {
    ArityMisMatchAnalyzer(FileReader(source))
}

fun main(args: Array<String>) {
    if (args.size < 2) {
        help()
        return
    }
    val cmd = CMD(args)
    when {
        cmd.amc() -> {
            arityMismatchCheck(cmd.source)
        }
    }
}