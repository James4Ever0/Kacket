import com.github.std.kacket.analysis.ProcCallAnalyzer
import java.io.FileReader

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
            val analyzer = ProcCallAnalyzer(FileReader(cmd.source))
            analyzer.analyzeProgram()
        }
    }
}