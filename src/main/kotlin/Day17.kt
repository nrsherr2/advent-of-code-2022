class Day17 {
    val part1TestExpected = 3068
    val part2TestExpected = -1
    fun part1(input: List<String>): Int {
        val directions = input.first().map { it }.toMutableList()
        val rockQueue = mutableListOf(minus, plus, revL, tetGod, blok)
        val order = (1..2022).map { rockQueue.rotate().toList() }
        val board = mutableListOf(0b1111111)
        order.forEach { block ->
            var bk = block
            //grow board and populate new block
            val topSpot = board.indexOfFirst { it > 0 }.let { if (it < 0) board.lastIndex else it }
            val desiredSize = board.size - topSpot + 7
            while (board.size < desiredSize) board.add(0, 0)
            var blockIdx = 3
            var condition = true
            while (condition) {
                val dir = directions.rotate()
                when (dir) {
                    '>' -> {
//                        println("push right")
                        if (bk.canShr(board, blockIdx)) {
//                            println("shr")
                            bk = bk.shr()
                        }
                    }

                    '<' -> {
//                        println("push left")
                        if (bk.canShl(board, blockIdx)) {
//                            println("shl")
                            bk = bk.shl()
                        }
                    }
                }
                if (notBlockedDown(board, bk, blockIdx)) blockIdx++ else condition = false
            }
            bk.forEachIndexed { index, line ->
                val row = board[blockIdx - (3 - index)]
                board[blockIdx - (3 - index)] = row or line
            }
//            board.visualize()
//            println()
        }
        board.visualize()
        return board.size - board.indexOfFirst { it > 0 }
    }

    fun part2(input: List<String>): Int {
        val directions = input.first().map { it }.reversed().toMutableList()
        val rockQueue = mutableListOf(blok, tetGod, revL, plus, minus)
        TODO()
    }

    fun List<Int>.visualize() {
        this.forEach {
            print('|')
            it.toString(2).padStart(7, '0').map { if (it == '0') '░' else '▓' }.forEach { print(it) }
            println('|')
        }
    }

    fun List<Int>.canShl(board: List<Int>, bottomRowNum: Int): Boolean {
        if (this.any { it and 0b1000000 != 0 }) return false
        val testShr = this.shl()
        testShr.forEachIndexed { index, i ->
            val row = board[bottomRowNum - (3 - index)]
            if (i and row != 0) return false
        }
        return true
    }

    fun List<Int>.shl(bitsLeft: Int = 1): List<Int> {
        return map { it.shl(bitsLeft) }
    }

    fun List<Int>.canShr(board: List<Int>, bottomRowNum: Int): Boolean {
        if (this.any { it and 1 == 1 }) return false
        val testShr = this.shr()
        testShr.forEachIndexed { index, i ->
            val row = board[bottomRowNum - (3 - index)]
            if (i and row != 0) return false
        }
        return true
    }

    fun List<Int>.shr(bitsRight: Int = 1): List<Int> {
        return map { it.shr(bitsRight) }
    }

    fun notBlockedDown(board: List<Int>, block: List<Int>, boardRowNum: Int): Boolean {
        if (boardRowNum + 1 == board.lastIndex) return false
        return board[boardRowNum + 1] and block.last() == 0
    }

    val minus = listOf(0, 0, 0, 0b1111.shl(1))
    val plus = listOf(0, 0b010.shl(2), 0b111.shl(2), 0b010.shl(2))
    val revL = listOf(0, 0b1.shl(2), 0b1.shl(2), 0b111.shl(2))
    val tetGod = listOf(1.shl(4), 1.shl(4), 1.shl(4), 1.shl(4))
    val blok = listOf(0, 0, 0b11.shl(3), 0b11.shl(3))


    fun <T> MutableList<T>.rotate(): T {
        val latest = this.removeFirst()
        this.add(latest)
        return latest
    }
}