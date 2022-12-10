class Day10 {
    val part1TestExpected = 13140
    val part2TestExpected = "▓▓░░▓▓░░▓▓░░▓▓░░▓▓░░▓▓░░▓▓░░▓▓░░▓▓░░▓▓░░\n" +
            "▓▓▓░░░▓▓▓░░░▓▓▓░░░▓▓▓░░░▓▓▓░░░▓▓▓░░░▓▓▓░\n" +
            "▓▓▓▓░░░░▓▓▓▓░░░░▓▓▓▓░░░░▓▓▓▓░░░░▓▓▓▓░░░░\n" +
            "▓▓▓▓▓░░░░░▓▓▓▓▓░░░░░▓▓▓▓▓░░░░░▓▓▓▓▓░░░░░\n" +
            "▓▓▓▓▓▓░░░░░░▓▓▓▓▓▓░░░░░░▓▓▓▓▓▓░░░░░░▓▓▓▓\n" +
            "▓▓▓▓▓▓▓░░░░░░░▓▓▓▓▓▓▓░░░░░░░▓▓▓▓▓▓▓░░░░░"

    fun part1(input: List<String>): Int {
//        println("***")
        var cycleNum = 0
        var register = 1
        var inc = 0

        fun keepTime() {
            cycleNum++
            listOf(20, 60, 100, 140, 180, 220).forEach { j ->
                if (cycleNum == j) {
                    val res = j * register
                    inc += res
//                    println("c=$j;\tX=$register ->\tr=$res;\tt=0$inc")
                }
            }
        }

        input.forEach { line ->
            if (line.startsWith("noop")) {
                keepTime()
            } else {
                keepTime(); keepTime()
                register += line.split(" ")[1].toInt()
            }
        }
        return inc
    }

    fun part2(input: List<String>): String {
        val pixels = mutableListOf<Char>()
        fun cycleNum() = pixels.size
        var register = 1
        fun registerRange() = (register - 1)..(register + 1)
        fun inRegisterRange() = (cycleNum() % 40) in (register - 1)..(register + 1)

        fun printSprite() {
            println("${cycleNum()} sprite position:")
            for (j in 1..40) {
                val printNum = j - 1
                if (printNum in registerRange()) print("#") else print(".")
            }
            println()
        }

        fun waitACycle() {
            val c = if (inRegisterRange()) '▓' else '░'
//            println("During cycle ${cycleNum() + 1}:\tCRT draws $c in position ${cycleNum()}")
            pixels.add(c)
//            println("Current CRT:")
//            println(pixels.joinToString("").chunked(40).joinToString("\n"))
        }

        fun noop() {
//            printSprite()
//            println("Start cycle ${cycleNum() + 1}:\tbegin executing NOOP")
            waitACycle()
//            println()
        }

        fun addx(addNum: Int) {
//            printSprite()
//            println("Start cycle ${cycleNum() + 1}:\tbegin executing ADDX $addNum")
            waitACycle()
            waitACycle()
            register += addNum
//            println("End of Cycle ${cycleNum()}:\tfinish executing ADDX $addNum\tRegister X is now $register")
        }



        input.forEach { line ->
            if (line == "noop") noop() else addx(line.split(" ")[1].toInt())
        }
        return pixels.joinToString("").chunked(40).joinToString("\n")
    }
}