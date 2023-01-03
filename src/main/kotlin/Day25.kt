import kotlin.math.pow

class Day25 {
    val part1TestExpected = "2=-1=0"
    val part2TestExpected = -1
    fun part1(input: List<String>): String {
        val digit = input.sumOf { decodeSnafu(it) }
        println(digit)
        return encodeSnafu(digit).also { println(decodeSnafu(it)) }
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    private fun decodeSnafu(snafuString: String): Long {
        var num = 0L
        snafuString.reversed().forEachIndexed { index, c ->
            val mul = 5.0.pow(index.toDouble())
            val digit = when (c) {
                '0', '1', '2' -> c.digitToInt()
                '=' -> -2
                '-' -> -1
                else -> TODO()
            }
            num += (digit * mul.toLong())
        }
        return num
    }

    private fun encodeSnafu(num: Long): String {
        var n = num
        var newNum = ""
        while (n > 0) {
//            println(n.toString(5))
            val laste = n.toString(5).last()
            when (laste) {
                '0', '1', '2' -> newNum = "$laste$newNum"
                '3' -> {
                    newNum = "=$newNum"
                    n += 5
                }

                '4' -> {
                    newNum = "-$newNum"
                    n += 5
                }

                else -> TODO()
            }
//            println(newNum)
            n /= 5
        }
        return newNum
    }
}