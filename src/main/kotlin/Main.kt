import com.github.std.kacket.analysis.ProcCallAnalyzer
import com.github.std.kacket.parse.Parser
import java.io.ByteArrayInputStream
import java.io.FileReader
import java.io.InputStreamReader

private fun help() {
    val usage = """
        Usage: kacket <options> <source files>
        where possible options include:
        --pcc          procedure call check
    """.trimIndent()
    println(usage)
}

fun main(args: Array<String>) {
    if (args.size < 2) {
        help()
        return
    }
    val cmd = CMD(args)
    when {
        cmd.pcc() -> {
            val input = FileReader(cmd.source)
            val parser = Parser(input)
            val analyzer = ProcCallAnalyzer(parser)
            analyzer.analyzeProgram()
        }
    }
}