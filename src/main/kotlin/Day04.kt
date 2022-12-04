class Day04 {
    val part1TestExpected = 2
    val part2TestExpected = 4
    fun part1(input: List<String>): Int {
        return input.map { s ->
            s.split(",").map { r -> r.split("-").let { it[0].toInt()..it[1].toInt() } }.let { it[0] to it[1] }
        }.count { (range1, range2) -> range1.all { range2.contains(it) } || range2.all { range1.contains(it) } }
    }

    fun part2(input: List<String>): Int {
        return input.map { s ->
            s.split(",").map { r -> r.split("-").let { it[0].toInt()..it[1].toInt() } }.let { it[0] to it[1] }
        }.count { (range1, range2) -> range1.any { range2.contains(it) } || range2.any { range1.contains(it) } }
    }
}