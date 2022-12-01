import kotlin.math.max

class Day01 {
    val part1TestExpected = 24000L
    val part2TestExpected = 45000L

    fun part1(input: List<String>): Long {
        var curMax = Long.MIN_VALUE
        var acc: Long = 0
        input.forEach {
            if (it.isBlank()) {
                curMax = max(acc, curMax)
                acc = 0
            } else {
                acc += it.toLong()
            }
        }
        curMax = max(acc, curMax)
        return curMax
    }

    fun part2(input: List<String>): Long {
        val resuts = mutableListOf<Long>()
        var acc: Long = 0
        input.forEach {
            if (it.isBlank()) {
                resuts.add(acc)
                acc = 0
            } else {
                acc += it.toLong()
            }
        }
        resuts.add(acc)
        return resuts.sortedDescending().take(3).sum()
    }
}