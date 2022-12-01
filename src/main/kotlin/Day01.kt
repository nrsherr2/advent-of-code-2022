import kotlin.math.max
import kotlin.random.Random

class Day01 {
    val part1TestExpected = 24000L
    val part2TestExpected = 45000L

    fun part1(input: List<String>): Long {
        /**
         * 2 ways of doing it, randomly choose
         */
        return when (Random.nextInt(0, 3)) {
            0 -> {
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
                max(acc, curMax)
            }

            1 -> {
                input.joinToString("\n").split("\n\n").maxOf { group -> group.split("\n").sumOf { it.toLong() } }
            }

            else -> {
                input.fold(Pair(0L, 0L)) { (max, acc), str ->
                    if (str.isBlank()) Pair(max(max, acc), 0L) else Pair(max, acc + str.toLong())
                }.first
            }
        }
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