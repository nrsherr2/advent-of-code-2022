class Day17 {
    val part1TestExpected = -1
    val part2TestExpected = -1
    fun part1(input: List<String>): Int {
        val directions = ArrayDeque(input.first().map { it })
        TODO()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    val minus = listOf(0, 0, 0, 0b1111.shl(1))
    val plus = listOf(0, 0b010.shl(2), 0b111.shl(2), 0b010.shl(2))
    val revL = listOf(0, 0b1.shl(1), 0b1.shl(1), 0b111.shl(2))
    val tetGod = listOf(1.shl(4), 1.shl(4), 1.shl(4), 1.shl(4))
    val blok = listOf(0, 0, 0b11.shl(3), 0b11.shl(3))


    var rockQueue = ArrayDeque(listOf(minus, plus, revL, tetGod, blok))
    fun <T> ArrayDeque<T>.rotate():T {
        val latest = this.removeFirst()
        this.addLast(latest)
        return latest
    }
}