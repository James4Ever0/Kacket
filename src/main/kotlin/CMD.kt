class CMD(args: Array<String>) {
    val source: String = args[args.size - 1]
    private var pcc = false
    private var eopl = false

    init {
        parse(args)
    }

    private fun parse(args: Array<String>) {
        for (i in 0..args.size - 2) {
            when {
                args[i] == "--pcc" -> pcc = true
                args[i] == "--eopl" -> eopl = true
            }
        }
    }

    fun pcc(): Boolean = pcc
    fun eopl(): Boolean = eopl
}