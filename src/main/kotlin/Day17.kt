class Day17 {
    val part1TestExpected = 3068
    val part2TestExpected = 1514285714288
    fun part1(input: List<String>): Int {
        val directions = input.first().map { it }.toMutableList()
        val rockQueue = mutableListOf(minus, plus, revL, tetGod, blok)
        val order = (1..2022).map { rockQueue.rotate().map { it.copy() } }
        val board: MutableList<PointL> = floor.toMutableList()
        order.forEach { block ->
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
//        visualize(board)
        val small = solve(input, 2022)
        assertEquals(board.maxOf { it.rowNum }, small)
        return board.maxOf { it.rowNum }.toInt()
    }

    fun part2(input: List<String>): Long {
        val num = 1_000_000_000_000
        return solve(input, num)
    }

    private fun solve(input: List<String>, num: Long): Long {
        val directions = input.first().map { it }
        val rockQueue = mutableListOf(minus, plus, revL, tetGod, blok)
        val board: MutableList<PointL> = floor.toMutableList()
        val situations = mutableListOf<Situation>()
        var numRocks = 0
        fun height() = board.maxOf { it.rowNum }
        var offsetHeight = 0L
        var directionIdx = -1
        //1_000_000_000_000
        //https://github.com/ivzb/advent_of_code/blob/master/src/main/kotlin/_2022/Task17.kt
        var i = 0L
        while (i < num) {
            i++
            val block = rockQueue.rotate().map { it.copy() }
            var bk = block.map { pt -> pt + PointL(board.maxOf { it.rowNum } + 4, 2) }
            numRocks++
            var condition = true
            val sit = Situation(directionIdx, block, numRocks, height(), board.map { it.copy() })
            situations.add(sit)
            if (situations.size > 3 && offsetHeight == 0L) {
                var back = 1
                while (situations.size - (back * 5) > 0) {
                    val slice1 = situations.takeLast(back)
                    val slice2 = (situations - slice1).takeLast(back)
                    val slice3 = (situations - (slice1 + slice2)).takeLast(back)
                    val slice4 = (situations - (slice1 + slice2 + slice3)).takeLast(back)
                    val slice5 = (situations - (slice1 + slice2 + slice3 + slice4)).takeLast(back)
                    if (slice1.first().directionIdx == slice2.first().directionIdx &&
                        slice1.first().block == slice2.first().block &&
                        slice2.first().directionIdx == slice3.first().directionIdx &&
                        slice2.first().block == slice3.first().block &&
                        slice3.first().directionIdx == slice4.first().directionIdx &&
                        slice3.first().block == slice4.first().block &&
                        slice4.first().directionIdx == slice5.first().directionIdx &&
                        slice4.first().block == slice5.first().block &&
                        slice5.first().height > 1
                    ) {
                        val highestBoard = slice2.first().board - slice3.first().board
                        val midBoard = slice3.first().board - slice4.first().board
                        val lowestBoard = slice4.first().board - slice5.first().board
                        val stdHighest = highestBoard.minOf { it.rowNum }
                            .let { min -> highestBoard.map { PointL(it.rowNum - min, it.colNum) } }
                        val stdMid = midBoard.minOf { it.rowNum }
                            .let { min -> midBoard.map { PointL(it.rowNum - min, it.colNum) } }
                        val stdLowest = lowestBoard.minOf { it.rowNum }
                            .let { min -> lowestBoard.map { PointL(it.rowNum - min, it.colNum) } }
                        if (stdHighest == stdLowest && stdLowest == stdMid) {
                            visualize(highestBoard)
                            visualize(midBoard)
                            visualize(lowestBoard)
                            val patternSize = back
                            val patternHeight = stdHighest.maxOf { it.rowNum }
                            println("patternSize: $patternSize\tpatternHeight: $patternHeight")
                            println()
                            var j = num
                            while (j > 10) {
                                println("j: $j")
                                while (i + (patternSize * j) < num) {
                                    i += patternSize * j
                                    offsetHeight += patternHeight * j
                                }
                                println("we're up to $i now!")
                                println("height: $offsetHeight")
                                j /= 2
                            }
                            println()
                        }
                    }
                    back++
                }
            }
            while (condition) {
                directionIdx = (directionIdx + 1) % directions.size
                val dir = directions[directionIdx]

                when (dir) {
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
            println("numRocks: $i")
        }
        return height() + offsetHeight
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


    data class Situation(
        val directionIdx: Int,
        val block: List<PointL>,
        val numRocksDown: Int,
        val height: Long,
        val board: List<PointL>
    )

}