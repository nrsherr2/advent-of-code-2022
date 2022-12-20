class Day17 {
    val part1TestExpected = 3068
    val part2TestExpected = 1514285714288
    fun part1(input: List<String>): Int {
        val directions = input.first().map { it }.toMutableList()
        val rockQueue = mutableListOf(minus, plus, revL, tetGod, blok)
        val order = (1..2022).map { rockQueue.rotate().map { it.copy() } }
        val board: MutableList<PointL> = floor.toMutableList()
        order.forEach { block ->
            dropBlock(block, board, directions)
        }
        visualize(board)
        return board.maxOf { it.rowNum }.toInt()
    }

    fun part2(input: List<String>): Long {
        val directions = input.first().map { it }.reversed().toMutableList()
        val rockQueue = mutableListOf(blok, tetGod, revL, plus, minus)
        val board: MutableList<PointL> = floor.toMutableList()
        val deltas = mutableListOf<List<Double>>()

        TODO()
    }

    private fun dropBlock(
        block: List<PointL>,
        board: MutableList<PointL>,
        directions: MutableList<Char>
    ) {
        var bk = block.map { pt -> pt + PointL(board.maxOf { it.rowNum } + 4, 2) }
        var condition = true
        while (condition) {
            when (directions.rotate()) {
                '>' -> {
                    val potential = bk.map { it + shrDelta }
                    if (potential.none { it.colNum == 7L || board.contains(it) }) {
                        bk = potential
                    }
                }

                '<' -> {
                    val potential = bk.map { it + shlDelta }
                    if (potential.none { it.colNum == -1L || board.contains(it) }) {
                        bk = potential
                    }
                }
            }
            val potential = bk.map { it + dropDelta }
            if (potential.any { board.contains(it) }) {
                condition = false
            } else {
                bk = potential
            }
        }
        board.addAll(bk)
    }

    fun visualize(board: List<PointL>) {
        for (i in board.maxOf { it.rowNum } downTo board.minOf { it.rowNum }) {
            print('|')
            for (j in 0L..6) {
                print(if (board.contains(PointL(i, j))) '▓' else '░')
            }
            print('|')
            println()
        }
        println("+-------+")
    }


    val minus = listOf(PointL(0, 0), PointL(0, 1), PointL(0, 2), PointL(0, 3))
    val plus = listOf(PointL(1, 0), PointL(0, 1), PointL(1, 1), PointL(2, 1), PointL(1, 2))
    val revL = listOf(PointL(0, 0), PointL(0, 1), PointL(2, 2), PointL(1, 2), PointL(0, 2))
    val tetGod = listOf(PointL(0, 0), PointL(1, 0), PointL(2, 0), PointL(3, 0))
    val blok = listOf(PointL(0, 0), PointL(1, 1), PointL(1, 0), PointL(0, 1))
    val floor = listOf(PointL(0, 0), PointL(0, 1), PointL(0, 2), PointL(0, 3), PointL(0, 4), PointL(0, 5), PointL(0, 6))
    val shrDelta = PointL(0, 1)
    val shlDelta = PointL(0, -1)
    val dropDelta = PointL(-1, 0)


    fun <T> MutableList<T>.rotate(): T {
        val latest = this.removeFirst()
        this.add(latest)
        return latest
    }
}