import com.github.std.kacket.parse.EOF
import com.github.std.kacket.parse.Lexer
import java.io.ByteArrayInputStream
import java.io.InputStreamReader

fun main() {
    val code = "(define (fib n) (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2)))))"
    val lexer = Lexer(InputStreamReader(ByteArrayInputStream(code.toByteArray())))
    var token = lexer.nextToken()
    while (token != EOF) {
        println(token)
        token = lexer.nextToken()
    }
}