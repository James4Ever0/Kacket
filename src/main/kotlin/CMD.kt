class CMD(args: Array<String>) {
    val source: String = args[args.size - 1]
    private var amc = false

    init {
        parse(args)
    }

    private fun parse(args: Array<String>) {
        for (i in 0..args.size - 2) {
            when {
                args[i] == "--amc" -> amc = true
            }
        }
    }

    fun amc(): Boolean = amc
}