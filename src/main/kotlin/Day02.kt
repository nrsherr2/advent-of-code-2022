class Day02 {
    val part1TestExpected = 15
    val part2TestExpected = 12
    fun part1(input: List<String>): Int {
        return input.asSequence().map { line -> line[0] to line[2] }.sumOf { (opponent, player) ->
            val throwScore = (player - 'W')
            val winScore = when (Pair(opponent, player)) {
                Pair('A', 'Y'), Pair('B', 'Z'), Pair('C', 'X') -> 6
                Pair('B', 'Y'), Pair('C', 'Z'), Pair('A', 'X') -> 3
                else -> 0
            }
            throwScore + winScore
        }
    }

    fun part2(input: List<String>): Int {
        return input.asSequence().map { line -> line[0] to line[2] }.sumOf { (opponent, outcome) ->
            //x: lose, y: draw, z: win
            val (add, score) = when (outcome) {
                'X' -> 2 to 0
                'Y' -> 0 to 3
                'Z' -> 1 to 6
                else -> throw IllegalArgumentException()
            }
            val player = ((opponent - 'A' + add) % 3) + 1
            player + score
        }
    }
}