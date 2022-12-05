import com.github.std.kacket.analysis.ProcCallAnalyzer
import com.github.std.kacket.analysis.arityEqual
import com.github.std.kacket.analysis.arityGreaterEqual
import com.github.std.kacket.analysis.exten.CasesAnalyzer
import com.github.std.kacket.analysis.exten.DefineDatatypeAnalyzer
import com.github.std.kacket.parse.Parser
import com.github.std.kacket.parse.exten.CasesParser
import com.github.std.kacket.parse.exten.DefineDatatypeParser
import java.io.ByteArrayInputStream
import java.io.FileReader
import java.io.InputStreamReader

private fun help() {
    val usage = """
        Usage: kacket <options> <source files>
        where possible options include:
        --pcc          procedure call check
        --eopl         check #lang eopl of racket
    """.trimIndent()
    println(usage)
}

private fun procedureCallCheck(source: String) {
    val input = FileReader(source)
    val parser = Parser(input)
    val analyzer = ProcCallAnalyzer(parser)
    analyzer.analyzeProgram()
}

private fun eoplCheck(source: String) {
    val input = FileReader(source)
    var read = input.read()
    while (read.toChar() != '\n') {
        read = input.read()
    }
    val parser = Parser(input, 2, 1)
        .addSExprExt(DefineDatatypeParser)
        .addSExprExt(CasesParser)

    val analyzer = ProcCallAnalyzer(parser)
        .addExtAnalyzer(DefineDatatypeAnalyzer)
        .addExtAnalyzer(CasesAnalyzer)
        .addProcRule("list-of", arityEqual(1))
        .addProcRule("eopl:error", arityGreaterEqual(1))
        .addProcRule("sllgen:make-define-datatypes", arityEqual(2))
        .addProcRule("sllgen:list-define-datatypes", arityEqual(2))
        .addProcRule("sllgen:make-string-scanner", arityEqual(2))
        .addProcRule("sllgen:make-string-parser", arityEqual(2))
        .addProcRule("sllgen:make-rep-loop", arityEqual(3))

    analyzer.analyzeProgram()
}

fun main(args: Array<String>) {
    if (args.size < 2) {
        help()
        return
    }
    val cmd = CMD(args)
    try {
        when {
            cmd.eopl() -> {
                eoplCheck(cmd.source)
            }

            cmd.pcc() -> {
                procedureCallCheck(cmd.source)
            }

            else -> help()
        }
    } catch (ex: Throwable) {
        println(ex.message)
    }

}